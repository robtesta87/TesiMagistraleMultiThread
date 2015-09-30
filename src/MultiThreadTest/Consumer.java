package MultiThreadTest;

import index.mapping_table.SearcherMid;

import java.io.IOException;
import java.util.TreeMap;
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
	AbstractSequenceClassifier<CoreLabel> classifier;

	public Consumer(CountDownLatch latch,  ConcurrentLinkedQueue<bean.WikiArticle> queue,String version,SearcherMid searcherMid,AbstractSequenceClassifier<CoreLabel> classifier) {
		this.latch = latch;
		this.queue = queue;
		this.version = version;
		this.searcherMid = searcherMid;
		this.classifier = classifier;
	}

	public void run() {
		System.out.println("Started.");
		System.out.println("thread: "+Thread.currentThread().getName());
		//avvio del classificatore del ner
		WikiArticle wikiArticle = null;
		ExtractorMentions extractor = new ExtractorMentions();
		Configuration config = Configuration.instance();
		String serializedClassifier = config.classificatore;
				
		while ((wikiArticle = queue.poll()) != null){
		
			try {
				TreeMap<String, Pair<String, String>> treemap = null;
				switch (version) {
				case "Base":
					//treemap = extractor.getMidModulate(wikiArticle,null, Version.Base,searcherMid);
					synchronized (searcherMid) {
						treemap  = extractor.getMid(wikiArticle,classifier, Version.Base,searcherMid);
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