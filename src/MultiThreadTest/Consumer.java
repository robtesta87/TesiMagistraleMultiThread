package MultiThreadTest;

import index.mapping_table.SearcherMid;
import index.redirect.SearcherRedirect;

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
import bean.WikiArticleOld;

public class Consumer implements Runnable {
	private CountDownLatch latch;
	private  ConcurrentLinkedQueue<bean.WikiArticleOld> queue;
	private String version;
	private SearcherMid searcherMid;
	private SearcherRedirect searcherRedirect;
	AbstractSequenceClassifier<CoreLabel> classifier;

	public Consumer(CountDownLatch latch,  ConcurrentLinkedQueue<bean.WikiArticleOld> queue,String version,SearcherMid searcherMid,SearcherRedirect searcherRedirect, AbstractSequenceClassifier<CoreLabel> classifier) {
		this.latch = latch;
		this.queue = queue;
		this.version = version;
		this.searcherMid = searcherMid;
		this.searcherRedirect = searcherRedirect;
		this.classifier = classifier;
	}

	public void run() {
		System.out.println("Started.");
		System.out.println("thread: "+Thread.currentThread().getName());
		String serializedClassifier = null;
		/*AbstractSequenceClassifier<CoreLabel> classifier2 = null;
		switch (Thread.currentThread().getName()) {
		case "pool-1-thread-1":
			//avvio del classificatore del ner
			serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
			try {
				classifier2 = CRFClassifier.getClassifier(serializedClassifier);
			} catch (ClassCastException | ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			break;

		case "pool-1-thread-2":
			serializedClassifier = "classifiers2/english.conll.4class.distsim.crf.ser.gz";
			try {
				classifier2 = CRFClassifier.getClassifier(serializedClassifier);
			} catch (ClassCastException | ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		default:
			break;
		}*/





		WikiArticleOld wikiArticle = null;
		ExtractorMentions extractor = new ExtractorMentions();
		Configuration config = Configuration.instance();

		while ((wikiArticle = queue.poll()) != null){

			try {
				TreeMap<String, Pair<String, String>> treemap = null;
				switch (version) {
				case "Base":
					//treemap = extractor.getMidModulate(wikiArticle,null, Version.Base,searcherMid);
					synchronized (searcherMid) {
						treemap  = extractor.getMid(wikiArticle,classifier, Version.Base,searcherMid,searcherRedirect);
					}
					break;
				case "Intermedia":
					System.out.println(wikiArticle);
					//treemap = extractor.getMidModulate(wikiArticle, null, Version.Intermedia,searcherMid);
					synchronized(searcherMid){
						treemap = extractor.getMid(wikiArticle, classifier, Version.Intermedia,searcherMid,null);
					}
					break;
				case "Completa":
					//treemap = extractor.getMidModulate(wikiArticle, null, Version.Completa,searcherMid);
					synchronized(searcherMid){
							treemap = extractor.getMid(wikiArticle, classifier, Version.Completa,searcherMid,searcherRedirect);
					}
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