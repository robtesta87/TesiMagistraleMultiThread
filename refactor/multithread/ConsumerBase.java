package multithread;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import bean.WikiArticle;
import freebase.FreebaseSearcher;

class ConsumerBase extends Consumer {

	/**
	 * 
	 * @param latch
	 * @param input_buffer
	 * @param output_buffer
	 * @param searcher
	 */
	public ConsumerBase(CountDownLatch latch, Queue<WikiArticle> input_buffer, 
			Queue<WikiArticle> output_buffer, FreebaseSearcher searcher) {
		super(latch, input_buffer, output_buffer, searcher);
	}

	@Override
	public void run() {
		WikiArticle current_article = null;
		while ((current_article = input_buffer.poll()) != null){
			//System.out.println(current_article.getText());
			
			extractMentions(current_article);
			updateMid(current_article);
			//... lavora wikiarticle... //
			
			output_buffer.add(current_article);
		}
		latch.countDown();
	}

}
