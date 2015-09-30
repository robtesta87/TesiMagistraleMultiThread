package MultiThreadTest;

import index.mapping_table.SearcherMid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	private  ConcurrentLinkedQueue<bean.WikiArticle> queue;
	private String version;
	private SearcherMid searcherMid;

	public Consumer(CountDownLatch latch,  ConcurrentLinkedQueue<bean.WikiArticle> queue,String version,SearcherMid searcherMid) {
		this.latch = latch;
		this.queue = queue;
		this.version = version;
		this.searcherMid = searcherMid;
	}

	public void run() {
		Configuration config = new Configuration();
		System.out.println("Started.");
		System.out.println("thread: "+Thread.currentThread().getName());
		//avvio del classificatore del ner
		String serializedClassifier = config.classificatore;
		List<String> list = new ArrayList<String>();
		AbstractSequenceClassifier<CoreLabel> classifier = null;
					
		String value;
		WikiArticle wikiArticle = null;
		TreeMap<String, Pair<String,String>> treemap = null;
		ExtractorMentions extractor = new ExtractorMentions();
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while ((wikiArticle = queue.poll()) != null){
		
			try {
				switch (version) {
				case "Base":
					//treemap = extractor.getMidModulate(wikiArticle,null, Version.Base,searcherMid);
					synchronized (searcherMid) {
						treemap = extractor.getMid(wikiArticle,classifier, Version.Base,searcherMid);
					}
					break;
				case "Intermedia":
					System.out.println(wikiArticle);
					//treemap = extractor.getMidModulate(wikiArticle, null, Version.Intermedia,searcherMid);
					synchronized(searcherMid){
						treemap = extractor.getMid(wikiArticle, classifier, Version.Intermedia,searcherMid);
					}
					break;
				case "Completa":
					
					//treemap = extractor.getMidModulate(wikiArticle, null, Version.Completa,searcherMid);
					treemap = extractor.getMid(wikiArticle, classifier, Version.Completa,searcherMid);
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
	
		}

		latch.countDown();

	}
}