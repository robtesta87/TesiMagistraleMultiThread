package multithread;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import bean.WikiArticle;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import freebase.FreebaseSearcher;

class ConsumerIntermedia extends Consumer {

	public ConsumerIntermedia(CountDownLatch latch,
			Queue<WikiArticle> input_buffer, Queue<WikiArticle> output_buffer,
			FreebaseSearcher searcher,AbstractSequenceClassifier<CoreLabel> classifier) {
		super(latch, input_buffer, output_buffer, searcher, classifier);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		WikiArticle current_article = null;
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;
		while ((current_article = input_buffer.poll()) != null){
			//System.out.println(current_article.getText());

			extractMentions(current_article);
			updateMid(current_article);
			
			phrases = getSentences(current_article.getText());
			entitiesMap = getEntitiesFromPhrasesListMap(phrases);
			System.out.println(entitiesMap.toString());
			output_buffer.add(current_article);
		}
		latch.countDown();
	}

}
