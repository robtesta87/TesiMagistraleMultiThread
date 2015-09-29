package bean;

import index.mapping_table.SearcherMid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.stanford.nlp.util.Pair;

public class WikiArticle{
	//
	private String title;
	private String wikid;

	private String text;
	
	// testo--->wikid,mid
	TreeMap<String, Pair<String,String>> wikiEntities ;
	
	//lista delle mention con duplicati per analisi quantitativa
	List<EntryMention> mentions;
	
	//lista delle frasi annotate con i mid di freebase
	List<String> phrases;
	
	
	public WikiArticle(){
		this.wikiEntities = new TreeMap<String, Pair<String,String>>();
		this.mentions = new ArrayList<EntryMention>();
		this.phrases = null;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
		
	public String getWikid() {
		return wikid;
	}
	public void setWikid(String wikid) {
		this.wikid = wikid;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}




	public TreeMap<String, Pair<String, String>> getWikiEntities() {
		return wikiEntities;
	}

	public void setWikiEntities(TreeMap<String, Pair<String, String>> wikiEntities) {
		this.wikiEntities = wikiEntities;
	}

	
	
	public List<EntryMention> getMentions() {
		return mentions;
	}

	public void setMentions(List<EntryMention> mentions) {
		this.mentions = mentions;
	}

	public void addWikiEntities (String text, String wikid, String mid){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(wikid);
		pair.setSecond(mid);
		this.wikiEntities.put(text, pair);
	}
	
	public void addMention (String text,String wikid,String mid){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(wikid);
		pair.setSecond(mid);
		this.wikiEntities.put(text, pair);
		
		
		EntryMention entryMention = new EntryMention();
		entryMention.setText(text);
		entryMention.setWikid(wikid);
		this.mentions.add(entryMention);
		
		
	}
	
	public void addMention (String text,String wikid){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(wikid);
		pair.setSecond(null);
		this.wikiEntities.put(text, pair);
		
		
		EntryMention entryMention = new EntryMention();
		entryMention.setText(text);
		entryMention.setWikid(wikid);
		this.mentions.add(entryMention);
		
		
	}
	public void addMention (String text){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(text.replaceAll(" ", "_"));
		pair.setSecond(null);
		this.wikiEntities.put(text, pair);
		
		
		
		EntryMention entryMention = new EntryMention();
		entryMention.setText(text);
		entryMention.setWikid(text.replaceAll(" ", "_"));
		this.mentions.add(entryMention);
		
	}
	public void addMentionPerson(String text,String wikid,String mid){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(wikid);
		pair.setSecond(mid);
		this.wikiEntities.put(text, pair);
	}
	public void addMentionPerson(String text,String wikid){
		Pair<String, String> pair = new Pair<String,String>();
		pair.setFirst(wikid);
		pair.setSecond(null);
		this.wikiEntities.put(text, pair);
	}
	
	public void updateMid (SearcherMid searcherMid){
		Iterator<String> keyIterator = this.wikiEntities.keySet().iterator();
		String currentEntity = null;
		Pair pair = null;
		String wikid = null; 
		EntryMappedBean mappingBean = null;
		SearcherMid s=null;
		try {
			s = new SearcherMid();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(keyIterator.hasNext()){
			currentEntity = keyIterator.next();
			pair = this.wikiEntities.get(currentEntity);
			wikid = (String) pair.first;
			
			try {
				mappingBean = s.getMid(wikid);
				//mappingBean = searcherMid.getMid(wikid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String mid="";
			if (mappingBean!=null){
				mid= mappingBean.getMid();
				pair.setSecond(mid);
			}
			
			
		}
	}

	public List<String> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}
	
	

}
