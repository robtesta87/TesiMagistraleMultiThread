package MultiThreadTest;

import index.mapping_table.SearcherMid;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import bean.WikiArticle;
import Configuration.Configuration;

public class ParseWikiExtractor {
	private static int cores =Runtime.getRuntime().availableProcessors()*2;

	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

		return br2;
	}	



	public static void main(String[] args) throws FileNotFoundException, CompressorException {
		ConcurrentLinkedQueue<WikiArticle> queue = new ConcurrentLinkedQueue<WikiArticle>();
		Configuration config = Configuration.instance();
		String version = config.version;
		/*
		//avvio del classificatore del ner
		String serializedClassifier = config.classificatore;
		AbstractSequenceClassifier<CoreLabel> classifier = null;
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 */
		System.out.println("Versione selezionata: "+config.version);
		File dir = new File(config.segmentWikiExtractorPath);
		File[] directoryListing = dir.listFiles();
		String text = null;
		BufferedReader br=null;
		String[] docs = null;
		String[] docSplitted = null;
		String[] titleText = null;
		String textWikiArticle = null;
		String titleWikiArticle = null;
		if (directoryListing != null) {

			for (File f : directoryListing) {
				System.out.println("Analisi del file:"+ f.getName());
				br = getBufferedReaderForCompressedFile(dir+"/"+f.getName());

				StringBuilder builder = new StringBuilder();
				String aux = "";

				try {
					while ((aux = br.readLine()) != null) {
						builder.append(aux+"\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				text = builder.toString();
				docs = text.split("<doc id=");
				for (String doc : docs) {
					docSplitted = doc.split("title=\"");
					if (docSplitted.length>1){
						titleText = docSplitted[1].split("\">");
						titleWikiArticle = titleText[0];
						textWikiArticle = titleText[1].split("</doc>")[0];

						WikiArticle wikiArticle = new WikiArticle();
						wikiArticle.setText(textWikiArticle);
						wikiArticle.setTitle(titleWikiArticle);
						wikiArticle.setWikid(titleWikiArticle.replaceAll(" ","_"));
						queue.add(wikiArticle);
						

					}
				}

			}
			

			System.out.println("OK!");
			int queueSize = queue.size();

			CountDownLatch latch = new CountDownLatch(cores);
			Date start = new Date();
			ExecutorService executor = Executors.newFixedThreadPool(cores);

			SearcherMid searcherMid = null;
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
}
