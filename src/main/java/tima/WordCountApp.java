package tima;

import tima.global.ConfigLoader;
import tima.wordcount.WordCountWorker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCountApp {
    public static void main(String[] args) throws IOException {

        Properties properties = ConfigLoader.getInstance().getProperties();
        String folder = properties.getProperty("folder_path");
        int numThreads = Integer.parseInt(properties.getProperty("num_threads"));
        int chunksize = Integer.parseInt(properties.getProperty("chunksize"));
        ConcurrentMap<String, Integer> wordCountMap = new ConcurrentHashMap<String, Integer>();

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
//        Map<Integer, WordCountWorker> workerMap = new HashMap<>();

        try (
                Stream<Path> walk = Files.walk(Paths.get(folder))
        ) {

            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".txt")).collect(Collectors.toList());

            int count = 0;
            for (String file : result) {
                try {

                    WordCountWorker worker = new WordCountWorker(wordCountMap);
                    pool.submit(worker);
                    System.out.println("Start worker " + count % numThreads + "th");
                    worker.push(file);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //wait for all task finish
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.out.println("Pool interrupted!");
            System.exit(1);
        }

        //log word count
        int total = 0;
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            int count = entry.getValue();
            total += 1;
            String strWord = String.format("%-30s %d\n", entry.getKey(), count);
            System.out.println(strWord);
        }
        System.out.println(" Total words = " + total);

    }
}
