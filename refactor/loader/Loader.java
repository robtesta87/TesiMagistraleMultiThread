package loader;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import bean.WikiArticle;


public class Loader {

	private String file2load;

	/**
	 * 
	 * @param file2load
	 */
	public Loader(String file2load){
		this.file2load = file2load;
	}

	/**
	 * 
	 * @param fileIn
	 * @return
	 * @throws FileNotFoundException
	 * @throws CompressorException
	 */
	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
		return br2;
	}	


	/**
	 * 
	 * @return
	 * @throws CompressorException 
	 * @throws FileNotFoundException 
	 */
	public List<WikiArticle> getArticles() throws FileNotFoundException, CompressorException{
		List<WikiArticle> articles = new ArrayList<WikiArticle>();


		File dir = new File(file2load);
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
						WikiArticle wikiArticle = new WikiArticle(titleWikiArticle,titleWikiArticle.replaceAll(" ","_"),textWikiArticle);
						articles.add(wikiArticle);
					}
				}
			}
			System.out.println("OK!");
		
		}








		return articles;
	}




}
