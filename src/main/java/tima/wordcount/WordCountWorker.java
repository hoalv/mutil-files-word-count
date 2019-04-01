package tima.wordcount;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WordCountWorker extends Thread{

	private BlockingQueue<String> queue;
	private static final int QUEUE_SIZE = 10;

	private boolean isProcessDone = false;

	private ConcurrentMap<String, Integer> wordCountMap;

	public WordCountWorker(ConcurrentMap<String, Integer> wordCountMap) {
		super();
		this.queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
		this.wordCountMap = wordCountMap;
	}
	public ConcurrentMap<String, Integer> getWordCount() {
		return wordCountMap;
	}

	public void push(String file) throws InterruptedException {
		queue.put(file);
	}

	public String readFileAsString(BufferedReader reader, int size)
			throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(size);
		int numRead=0;

		while(size > 0) {
			int bufsz = 1024 > size ? size : 1024;
			char[] buf = new char[bufsz];
			numRead = reader.read(buf,0,bufsz);
			if (numRead == -1)
				break;
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			size -= numRead;
		}
		return fileData.toString();
	}


	@Override
	public void run() {
		while (true){
			try{
				String file = queue.take();
				process(file);

			}catch (InterruptedException ex){
				System.out.println("Worker getting interrupted " + ex);
			}

			if(isProcessDone) {
				System.out.println("kill thread " + Thread.currentThread().getName());
				break;
			}
		}

	}

	public void process(String filePath){
		BufferedReader reader = null;
		int chunksize = 1024;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			while (true) {
				String res = readFileAsString(reader,chunksize);
				if (res.equals("")) {
					break;
				}
				countWords(res);
			}

//			writeCountingResult(filePath, "input/out.txt");
			isProcessDone = true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeCountingResult(String fileInput, String fileOutput){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileOutput, true));
			int total = 0;
			for (Map.Entry<String,Integer> entry : wordCountMap.entrySet()) {
				int count = entry.getValue();
				total += 1;
				String strWord = String.format("%-30s %d\n", entry.getKey(),count);
				writer.write(strWord);

			}
			writer.close();
			System.out.println(fileInput + " Total words = " + total);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void countWords(String buffer){
//	    buffer.replaceAll("\\.", "");
		for(String word: buffer.split("\\s+|\\t+|,|;|\\.|:")){
			word = word.replaceFirst("[^\\p{L}]*", "");
			word = new StringBuffer(word).reverse().toString();
			word = word.replaceFirst("[^\\p{L}]*", "");
			word = new StringBuffer(word).reverse().toString();

			Integer count = this.wordCountMap.get(word);
			this.wordCountMap.put(word, count == null ? 1 : count + 1);
		}
	}


}
