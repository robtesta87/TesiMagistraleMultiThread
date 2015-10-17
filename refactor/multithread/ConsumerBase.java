package multithread;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import redirect.RedirectSearcher;
import Logger.Logger;
import Printer.PrinterOutput;
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
			FreebaseSearcher searcher,AbstractSequenceClassifier<CoreLabel> classifier, String analysis_folder, Logger logger, Logger logger_quantitativeAnalysis, Logger logger_countMid, RedirectSearcher redirect_searcher,PrinterOutput printer_output) {
		super(latch, input_buffer, searcher, classifier, analysis_folder, logger, logger_quantitativeAnalysis, logger_countMid, redirect_searcher, printer_output);
	}

	@Override
	public void run() {
		
		
		WikiArticle current_article = null;
		List<String> phrases = null;
		Queue<String> logQueue = new ConcurrentLinkedQueue<String>();
		Queue<String> logQueueOutput = new ConcurrentLinkedQueue<String>();

		int size_queue = 0;
		while ((current_article = input_buffer.poll()) != null){
			System.out.println(current_article.getTitle());
			/*
			PrintWriter outArticle = null;
			PrintWriter outMentions = null;
			try {
				 outArticle = new PrintWriter(new BufferedWriter(new FileWriter(analysis_folder+articleBase_folder+current_article.getTitle()+".txt", true)));
				 outMentions = new PrintWriter(new BufferedWriter(new FileWriter(analysis_folder+mentionBase_folder+current_article.getTitle()+".csv", true)));
			} catch (IOException e) {
				System.out.println("errore nella creazione file di testo di analisi!");
				e.printStackTrace();
			}	
			*/
			//printer.PrintDirtyText(outArticle, current_article.getText());
			
			extractMentions(current_article);
			
			//printer.PrintCleanedText(outArticle, current_article.getText());
			
			updateMid(current_article);
			phrases = getSentences(current_article.getText());	
			
						
			phrases = replaceMid(phrases, current_article.getMentions());
			current_article.setPhrases(phrases);
			logQueueOutput.add(getOutput(current_article));
			//printer.PrintMention(outArticle, current_article);
			//printer.PrintMention(outMentions, current_article);
			
			//outArticle.close();
			//outMentions.close();
			
			//aggiungo la quantità delle mention trovate in un log
			logQueue.add(current_article.getTitle()+"\t"+current_article.getMentions().size());
			size_queue++;
			if (size_queue>=30){
				logger_quantitativeAnalysis.addResult(logQueue);
				printer_output.addResult(logQueueOutput);
				size_queue = 0;
			}
			
			//conto quanti mid ci sono per frase e salvo i risultati in un log
			countMid(current_article);
			
		}
		logger_quantitativeAnalysis.addResult(logQueue);
		printer_output.addResult(logQueueOutput);
		
		latch.countDown();
	}

}
