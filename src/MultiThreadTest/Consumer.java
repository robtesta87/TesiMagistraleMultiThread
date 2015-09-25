package MultiThreadTest;

import index.mapping_table.SearcherMid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import Configuration.Configuration;
import ExtractorMentions.ExtractorMentions;
import ExtractorMentions.ExtractorMentions.Version;
import bean.WikiArticle;

class Consumer implements Runnable {
	private CountDownLatch latch;
	private BlockingQueue<bean.WikiArticle> queue;
	private String version;
	private SearcherMid searcherMid;

	public Consumer(CountDownLatch latch, BlockingQueue<bean.WikiArticle> queue,String version,SearcherMid searcherMid) {
		this.latch = latch;
		this.queue = queue;
		this.version = version;
		this.searcherMid = searcherMid;
	}

	public void run() {
		System.out.println("Started.");
		String value;
		WikiArticle wikiArticle = null;
		TreeMap<String, Pair<String,String>> treemap = null;
		System.out.println("thread: "+Thread.currentThread().getName());
		ExtractorMentions extractor = new ExtractorMentions();
		while ((wikiArticle = queue.poll()) != null){
			try {
				switch (version) {
				case "Base":
					
					treemap = extractor.getMidModulate(wikiArticle,null, Version.Base,searcherMid);
					break;
				case "Intermedia":
					
					treemap = extractor.getMidModulate(wikiArticle, null, Version.Intermedia,searcherMid);
					break;
				case "Completa":
					
					treemap = extractor.getMidModulate(wikiArticle, null, Version.Completa,searcherMid);
					break;
				default:
					System.out.println("Versione non specificata correttamente nel file di configurazione");
					break;
				}
			} catch (ClassCastException | ClassNotFoundException
					| IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	*/		
		}

		latch.countDown();

	}
}