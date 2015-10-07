package multithread;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import bean.WikiArticle;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import freebase.FreebaseSearcher;

class ConsumerBase extends Consumer {
	private static String articleBase_folder = "raccoltaDati/ArticleBase/";
	private static String mentionBase_folder = "raccoltaDati/mentionBase/";
	/**
	 * 
	 * @param latch
	 * @param input_buffer
	 * @param output_buffer
	 * @param searcher
	 */
	public ConsumerBase(CountDownLatch latch, Queue<WikiArticle> input_buffer, 
			Queue<WikiArticle> output_buffer, FreebaseSearcher searcher,AbstractSequenceClassifier<CoreLabel> classifier, String analysis_folder) {
		super(latch, input_buffer, output_buffer, searcher, classifier, analysis_folder);
	}

	@Override
	public void run() {
		
		
		WikiArticle current_article = null;
		List<String> phrases = null;
		while ((current_article = input_buffer.poll()) != null){
			System.out.println(current_article.getTitle());

			PrintWriter outArticle = null;
			PrintWriter outMentions = null;
			try {
				 outArticle = new PrintWriter(new BufferedWriter(new FileWriter(analysis_folder+articleBase_folder+current_article.getTitle()+".txt", true)));
				 outMentions = new PrintWriter(new BufferedWriter(new FileWriter(analysis_folder+mentionBase_folder+current_article.getTitle()+".csv", true)));
			} catch (IOException e) {
				System.out.println("errore nella creazione file di testo di analisi!");
				e.printStackTrace();
			}	
			
			printer.PrintDirtyText(outArticle, current_article.getText());
			
			extractMentions(current_article);
			
			printer.PrintCleanedText(outArticle, current_article.getText());
			
			updateMid(current_article);
			phrases = getSentences(current_article.getText());	
			
			/*for (String phrase : phrases) {
				phrase = phrase.toLowerCase();
				System.out.println(phrase);
			}*/
			
			replaceMid(phrases, current_article.getMentions(),outArticle);
			current_article.setPhrases(phrases);
			
			printer.PrintMention(outArticle, current_article);
			printer.PrintMention(outMentions, current_article);
			
			outArticle.close();
			outMentions.close();
			
			output_buffer.add(current_article);
		}
		latch.countDown();
	}

}
