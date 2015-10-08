package multithread;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import Logger.Logger;
import bean.WikiArticle;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import freebase.FreebaseSearcher;

class ConsumerIntermedia extends Consumer {
	private static String articleBase_folder = "raccoltaDati/ArticleIntermedia/";
	private static String mentionBase_folder = "raccoltaDati/mentionIntermedia/";
	

	public ConsumerIntermedia(CountDownLatch latch,
			Queue<WikiArticle> input_buffer, Queue<WikiArticle> output_buffer,
			FreebaseSearcher searcher,AbstractSequenceClassifier<CoreLabel> classifier, String analysis_folder, Logger logger, Logger quantitativeAnalysisBase, Logger countMidFile) {
		super(latch, input_buffer, output_buffer, searcher, classifier, analysis_folder, logger, quantitativeAnalysisBase, countMidFile);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		WikiArticle current_article = null;
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;
		
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
			entitiesMap = getEntities(phrases);
			
			printer.PrintEntities(outArticle, entitiesMap);
			
			addPerson(entitiesMap.get("PERSON"), current_article);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			addEntity(entitiesMap.get("ORGANIZATION"), current_article);
			addEntity(entitiesMap.get("MISC"), current_article);
			addEntity(entitiesMap.get("LOCATION"), current_article);
			
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
