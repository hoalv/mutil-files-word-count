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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCountApp {
    public static void main(String[] args) throws IOException {

        Properties properties = ConfigLoader.getInstance().getProperties();
        String folder = properties.getProperty("folder_path");
        int numThreads = Integer.parseInt(properties.getProperty("num_threads"));
        int chunksize = Integer.parseInt(properties.getProperty("chunksize"));
        ConcurrentMap<String,Integer> wordCountMap = new ConcurrentHashMap<String,Integer>();

        Map<Integer, WordCountWorker> workerMap = new HashMap<>();

        try (
                Stream<Path> walk = Files.walk(Paths.get(folder))
        ) {

            List<String> result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".txt")).collect(Collectors.toList());

            int count = 0;
            for (String file : result) {
                try {
                    int hash = count % numThreads;
                    if(!workerMap.containsKey(hash)){
                        WordCountWorker worker = new WordCountWorker(wordCountMap);
                        worker.start();
                        System.out.println("Start worker " + count % numThreads + "th");
                        workerMap.put(hash, worker);
                    }
                    workerMap.get(hash).push(file);
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //sleep 5s
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //log word count
        int total = 0;
        for (Map.Entry<String,Integer> entry : wordCountMap.entrySet()) {
            int count = entry.getValue();
            total += 1;
            String strWord = String.format("%-30s %d\n", entry.getKey(),count);
            System.out.println(strWord);
        }
        System.out.println(" Total words = " + total);

    }
}
