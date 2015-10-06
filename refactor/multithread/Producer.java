package multithread;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.compress.compressors.CompressorException;

import bean.WikiArticle;
import configuration.Configuration;
import loader.Loader;

public class Producer {

	private Configuration config;
	private static int cores = 2*Runtime.getRuntime().availableProcessors()-1;
	
	/**
	 * 
	 * @param config
	 */
	public Producer(Configuration config){
		this.config = config;	
	}

	/**
	 * @throws InterruptedException 
	 * 
	 */
	public void process() throws InterruptedException{
		// carica aricoli da sorgente esterna
		Loader loader = new Loader(config.getWikipediaDump_path());
		
		System.out.println("Classificatore: "+config.getClassificatore_path());
		System.out.println("Analysis_folder: "+config.getAnalysis_folder());
		System.out.println("WikipediaDump_path: "+config.getWikipediaDump_path());
		System.out.println("Version: "+config.getVersion());
		System.out.println("Freebase_searcher: "+config.getFreebase_searcher());

		// input_buffer
		Queue<WikiArticle> input_buffer = null;
		try {
			input_buffer = new ConcurrentLinkedQueue<WikiArticle>(loader.getArticles());
		} catch (FileNotFoundException | CompressorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// output_buffer
		Queue<WikiArticle> output_buffer = new ConcurrentLinkedQueue<WikiArticle>();

		// multithread architecture
		int threads = Math.min(cores, input_buffer.size());
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(threads);

		System.out.println("Inizio processo. Output Buffer Size:\t" + output_buffer.size());
		
		Date start = new Date();
		// a seconda della versione, scegli il consumer adatto
		switch(config.getVersion()){
		case Base:
			for (int i = 0; i< threads ; i++){
				executor.submit(new ConsumerBase(latch, input_buffer, output_buffer, config.getFreebase_searcher()));
			}
			break;
		case Intermedia:
			/*for (int i = 0; i< threads ; i++){
				executor.submit(new Consumer(latch, queue, version,searcherMid,classifier));
			}*/
			break;
		case Completa:
			/*for (int i = 0; i< threads ; i++){
				executor.submit(new Consumer(latch, queue, version,searcherMid,classifier));
			}*/
			break;
		}

		latch.await();
		executor.shutdown();

		System.out.println("Processo finito. Output Buffer Size:\t" + output_buffer.size());

		Date end = new Date();
		System.out.println("Tempo di esecuzione in ms: "+(end.getTime()-start.getTime()));
	}


	
	
	
	/**
	 * Entry point.
	 * @param args
	 */
	public static void main(String[] args){

		String config_file = "/home/roberto/workspace/TesiMagistraleMultiThread/refactor/util/config.properties";
		Configuration config = new Configuration(config_file);
		Producer producer = new Producer(config);
		
		try {
			producer.process();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

}