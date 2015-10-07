package MultiThreadTest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import edu.stanford.nlp.util.Pair;
import bean.WikiArticleOld;



public class prova {

	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));

		return br2;
	}	
	public static void main(String[] args) {
		List<WikiArticleOld> queue = new ArrayList<WikiArticleOld>();
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
		File dir = new File("/home/roberto/Scrivania/TesiMagistrale/parseWikiExtractor(py)/AA");
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
				try {
					br = getBufferedReaderForCompressedFile(dir+"/"+f.getName());
				} catch (FileNotFoundException | CompressorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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

						WikiArticleOld wikiArticle = new WikiArticleOld(titleWikiArticle,titleWikiArticle.replaceAll(" ","_"),textWikiArticle);
						System.out.println(wikiArticle);
						queue.add(wikiArticle);
						System.out.println("aggiunto "+wikiArticle.toString());
						System.out.println(queue.toString());

					}
				}
			}

			
			
			WikiArticleOld s = null;

			while ((s = queue.get(0)) != null){
				System.out.println(s);
				queue.remove(0);
		}
			
		}
	}
}
