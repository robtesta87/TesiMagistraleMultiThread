package multithread;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import util.Pair;
import Logger.Logger;
import Printer.Printer;
import SortMap.SortMap;
import bean.WikiArticle;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.util.Triple;
import freebase.FreebaseSearcher;

abstract class Consumer implements Runnable {
	protected CountDownLatch latch;
	protected Queue<WikiArticle> input_buffer;
	protected Queue<WikiArticle> output_buffer;
	protected FreebaseSearcher searcher;
	protected AbstractSequenceClassifier<CoreLabel> classifier;
	protected String analysis_folder;
	protected Printer printer;
	public static SortMap sortMap;
	protected Logger logger;

	final static String mentionRegex = "\\[\\[[\\w+\\sÉé?!#,\"'.îóçë&–üáà:°í#ἀνã\\|\\(\\)_-]*\\]\\]";

	private static String boldRegex = "\"'[\\w+\\s\"]*\"'";


	/**
	 * 
	 * @param latch
	 * @param input_buffer
	 * @param output_buffer
	 * @param searcher
	 */
	public Consumer(CountDownLatch latch, Queue<WikiArticle> input_buffer, Queue<WikiArticle> output_buffer, FreebaseSearcher searcher, AbstractSequenceClassifier<CoreLabel> classifier, String analysis_folder, Logger logger){
		this.latch = latch;
		this.input_buffer = input_buffer;
		this.output_buffer = output_buffer;
		this.searcher = searcher;
		this.analysis_folder = analysis_folder;
		this.classifier = classifier;
		this.printer = new Printer();
		this.sortMap = new SortMap();
		this.logger = logger;

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

	/**
	 * 
	 * @param wikiArticle
	 */
	public  void extractMentions (WikiArticle wikiArticle){
		String text = wikiArticle.getText();

		//aggiungo il titolo come mention
		wikiArticle.addMention(wikiArticle.getTitle());

		//estrazione mention attraverso l'espressione regolare mentionRegex
		//e sostituisco le mention trovate con i wikid corrispondenti
		Pattern pattern = Pattern.compile(mentionRegex);
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()){
			String mentionString = matcher.group();
			String stringCleaned = mentionString.substring(2, mentionString.length()-2);

			if(stringCleaned.contains("|")){
				String[] splitted = stringCleaned.split("\\|");
				//primo campo: text secondo campo: wikiid
				if (!(splitted[0].contains("#"))){
					if (!splitted[0].equals("")){
						wikiArticle.addMention(splitted[0]);
						text = text.replace(mentionString, splitted[0]);
					}
					else{
						text = text.replace(mentionString, splitted[1]);
					}
				}
				else{
					//provo a prendere l'articolo riguardante la sotto-mention anzichè scartarla
					wikiArticle.addMention(splitted[0].split("#")[0]);

					text = text.replace(mentionString, stringCleaned);
				}
			}
			else{
				//evito di inserire le sotto-mention
				if (!(stringCleaned.contains("#"))){
					wikiArticle.addMention(stringCleaned);
					text = text.replace(mentionString, stringCleaned);
				}
				else{
					//provo a prendere l'articolo riguardante la sotto-mention anzichè scartarla
					wikiArticle.addMention(stringCleaned.split("#")[0]);
					text = text.replace(mentionString, stringCleaned);
				}

			}

		}	

		//rilevamento mention dalle parole in grassetto con l'espressione regolare bolRegex
		pattern = Pattern.compile(boldRegex);
		if (text.length()>300)
			matcher = pattern.matcher(text.substring(0, 300));
		else
			matcher = pattern.matcher(text.substring(0, text.length()-1));
		while(matcher.find()){
			String mentionString = matcher.group();
			String stringCleaned = mentionString.substring(2, mentionString.length()-2);
			wikiArticle.addMention(stringCleaned, wikiArticle.getWikid());
		}	
		wikiArticle.setText(text);

	}


	/**
	 * 
	 * @param freebaseSearcher
	 * @param wikiArticle
	 */
	public void updateMid (WikiArticle wikiArticle){
		TreeMap<String, Pair<String, String>> mentions = wikiArticle.getMentions();
		Iterator<String> keyIterator = mentions.keySet().iterator();
		String currentEntity = null;
		Pair<String, String> pair = null;
		String wikid = null; 
		Pair<String, String> mappingBean = null;
		while(keyIterator.hasNext()){
			currentEntity = keyIterator.next();
			pair = mentions.get(currentEntity);
			wikid = pair.getKey();
			try {
				mappingBean = searcher.getMid(wikid);
				//System.out.println(mappingBean.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String mid="";
			if (mappingBean!=null){
				mid= mappingBean.getValue();
				pair.setValue(mid);
			}
		}
	}

	/**
	 * metodo che restituisce, dato una stringa (testo), una lista di frasi
	 * @param text
	 * @return
	 */
	public static List<String> getSentences(String text){
		Reader reader = new StringReader(text);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();
		String sentenceString = null;
		for (List<HasWord> sentence : dp) {
			sentenceString = Sentence.listToString(sentence);
			sentenceList.add(sentenceString.toString());
		}

		return sentenceList;
	}

	/**
	 * 
	 * @param phrases
	 * @return
	 */
	public  Map<String,List<String>> getEntities(List<String> phrases){

		List<String> person = new ArrayList<String>();
		List<String> misc = new ArrayList<String>();
		List<String> location = new ArrayList<String>();
		List<String> organization = new ArrayList<String>();
		Map<String,List<String>> entityMap = new HashMap<String, List<String>>();
		String log = "";
		Queue<String> logQueue = new ConcurrentLinkedQueue<String>();
		try {
			for(String currentPhrase : phrases){
				List<Triple<String,Integer,Integer>> triples = null;
				triples = classifier.classifyToCharacterOffsets(currentPhrase);
				for (Triple<String,Integer,Integer> trip : triples) {
					String text = currentPhrase.substring(trip.second(), trip.third());
					if (text.contains("|")){
						System.out.println("entità non riconosciuta: "+text);
						logQueue.add(text);
					}
					switch (trip.first) {
					case "PERSON":
						person.add(text);
						break;
					case "ORGANIZATION":
						organization.add(text);
						break;
					case "LOCATION":
						location.add(text);
						break;
					case "MISC":
						misc.add(text);
						break;
					default:
						break;
					}
				}
			}

		} catch (ClassCastException e) {
			e.printStackTrace();
		} 
		entityMap.put("PERSON", person);
		entityMap.put("LOCATION",location);
		entityMap.put("ORGANIZATION", organization);
		entityMap.put("MISC", misc);

		logger.addResult(logQueue);
		return entityMap;
	}

	public static List<String> replaceMid (List<String> phrases,TreeMap<String, Pair<String,String>> treemap,PrintWriter out){
		Map<String, Pair<String, String>> sortedMap = null;

		sortedMap = sortMap.sortByValues(treemap);

		Set<Entry<String, Pair<String, String>>> setMap = sortedMap.entrySet();

		List<String> phrasesMid = new ArrayList<String>();

		for (String phrase:phrases){
			Iterator<Entry<String, Pair<String, String>>> i = setMap.iterator();
			out.println("FRASE");
			out.println(phrase);
			while((i.hasNext())) {
				Entry<String, Pair<String, String>> me = i.next();
				String key = me.getKey().toString().replaceAll("\\(","-LRB- ");
				key = key.replaceAll( "\\)"," -RRB-");
				String mid =  me.getValue().getValue();
				//phrase = phrase.replaceAll(key, "[["+key+"|"+mid+"]]");
				Pattern pattern = Pattern.compile("^"+key+"|([-,.\\s]"+key+"[.\\s,-])");
				Matcher matcher = pattern.matcher(phrase);
				while(matcher.find()){
					int startMention = matcher.start();
					int endMention = matcher.end();
					phrase = phrase.substring(0, startMention)+"[["+key+"|"+mid+"]]"+phrase.substring(endMention,phrase.length());
				}
			}
			phrasesMid.add(phrase);
			out.println(phrase);

		}
		return phrasesMid;
	}


}
