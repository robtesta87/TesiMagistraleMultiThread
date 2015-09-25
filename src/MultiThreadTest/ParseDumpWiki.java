package MultiThreadTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.xml.sax.SAXException;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;
import Configuration.Configuration;
import ExtractorMentions.ExtractorMentions;
import ExtractorMentions.ExtractorMentions.Version;
import index.mapping_table.SearcherMid;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;

public class ParseDumpWiki {


	public enum Version{
		Base,
		Intermedia,
		Completa;
	}

	static  class DemoArticleFilter implements IArticleFilter {

		private static int cont;
		private BlockingQueue<bean.WikiArticle> queue;
		private static String version;

		public DemoArticleFilter() {
			this.queue = new LinkedBlockingQueue<bean.WikiArticle>(10);
		}

		public void addWikiArticle(bean.WikiArticle wikiArticle){
			try {
				this.queue.put(wikiArticle);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public int getCiont(){
			return cont;
		}

		public void setCont(int cont){
			this.cont=cont;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public void process(WikiArticle page, Siteinfo siteinfo) {
			String title = page.getTitle();
			String text = page.getText();

			if (!(title.contains("List of"))&&!(text.contains("#REDIRECT"))&&!((text.contains("#redirect")))){
				bean.WikiArticle wikiArticle = new bean.WikiArticle();
				wikiArticle.setText(text);
				wikiArticle.setTitle(title);
				wikiArticle.setWikid(title.replaceAll(" ","_"));
				addWikiArticle(wikiArticle);
			}
			if (queue.size()==10){
				System.out.println("OK!");
				int queueSize = queue.size();
				int cores =Runtime.getRuntime().availableProcessors()/2;
				CountDownLatch latch = new CountDownLatch(cores);
				Date start = new Date();
				ExecutorService executor = Executors.newFixedThreadPool(cores);
				
				SearcherMid searcherMid;
				try {
					searcherMid = new SearcherMid();
				
				for(int i=0; i < cores; i++) {
					executor.submit(new Consumer(latch,queue,version,searcherMid));
				}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Date end = new Date();
				//System.out.println("Tempo di esecuzione in ms: "+(end.getTime()-start.getTime()));
				PrintWriter outDate=null;
				try {
					outDate = new PrintWriter(new BufferedWriter(new FileWriter("/home/roberto/Scrivania/tempo.txt", true)));
					outDate.println("Tempo di esecuzione in ms ("+cores+" thread, "+queueSize+" articoli): "+(end.getTime()-start.getTime()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				outDate.close();
				executor.shutdown();

				System.out.println("Completed.");
			}
		}

		public static void main(String[] args) throws IOException, ClassCastException, ClassNotFoundException {
			Date start = new Date();

			Configuration config = Configuration.instance();
			String version = config.version;
			
			DemoArticleFilter demo = new DemoArticleFilter();
			demo.setCont(0);
			demo.setVersion(version);
			String bz2Filename = config.dumpWikiPath;
			System.out.print("inserimento articoli in coda...");

			File filename = new File(bz2Filename);
			try {
				IArticleFilter handler = new DemoArticleFilter();
				WikiXMLParser wxp = new WikiXMLParser(filename, handler);
				wxp.parse();

			} catch (Exception e) {
				e.printStackTrace();
			}	

			Date end = new Date();
			System.out.println("Tempo di esecuzione in ms: "+(end.getTime()-start.getTime()));

		}
	}
}
