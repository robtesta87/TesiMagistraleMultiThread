package multithread;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import bean.WikiArticle;
import freebase.FreebaseSearcher;

abstract class Consumer implements Runnable {
	protected CountDownLatch latch;
	protected Queue<WikiArticle> input_buffer;
	protected Queue<WikiArticle> output_buffer;
	protected FreebaseSearcher searcher;
	
	/**
	 * 
	 * @param latch
	 * @param input_buffer
	 * @param output_buffer
	 * @param searcher
	 */
	public Consumer(CountDownLatch latch, Queue<WikiArticle> input_buffer, Queue<WikiArticle> output_buffer, FreebaseSearcher searcher){
		this.latch = latch;
		this.input_buffer = input_buffer;
		this.output_buffer = output_buffer;
		this.searcher = searcher;
	}

	/**
	 * @return the latch
	 */
	public CountDownLatch getLatch() {
		return latch;
	}

	/**
	 * @param latch the latch to set
	 */
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	/**
	 * @return the input_buffer
	 */
	public Queue<WikiArticle> getInput_buffer() {
		return input_buffer;
	}

	/**
	 * @param input_buffer the input_buffer to set
	 */
	public void setInput_buffer(Queue<WikiArticle> input_buffer) {
		this.input_buffer = input_buffer;
	}

	/**
	 * @return the output_buffer
	 */
	public Queue<WikiArticle> getOutput_buffer() {
		return output_buffer;
	}

	/**
	 * @param output_buffer the output_buffer to set
	 */
	public void setOutput_buffer(Queue<WikiArticle> output_buffer) {
		this.output_buffer = output_buffer;
	}

	/**
	 * @return the searcher
	 */
	public FreebaseSearcher getSearcher() {
		return searcher;
	}

	/**
	 * @param searcher the searcher to set
	 */
	public void setSearcher(FreebaseSearcher searcher) {
		this.searcher = searcher;
	}

}
