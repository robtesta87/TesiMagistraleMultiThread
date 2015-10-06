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

import org.apache.lucene.search.Query;

import edu.stanford.nlp.util.Pair;

public class WikiArticleOld{
	//
	private String title;
	private String wikid;

	private String text;

	// testo--->wikid,mid
	private TreeMap<String, Pair<String,String>> wikiEntities ;

	//lista delle mention con duplicati per analisi quantitativa
	private List<EntryMention> mentions;

	//lista delle frasi annotate con i mid di freebase
	private List<String> phrases;

	public WikiArticleOld(String title, String wikid, String text){
		this.title = title;
		this.wikid = wikid;
		this.text = text;
		this.wikiEntities = new TreeMap<String, Pair<String,String>>();
		this.mentions = new ArrayList<EntryMention>();
		this.phrases = null;
	}
	public WikiArticleOld(){
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
		while(keyIterator.hasNext()){
			currentEntity = keyIterator.next();
			pair = this.wikiEntities.get(currentEntity);
			wikid = (String) pair.first;

			try {
				mappingBean = searcherMid.getMid(wikid);
				//mappingBean = searcherMid.getMid(wikid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("errore");
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

	@Override
	public String toString() {
		return "WikiArticle [title=" + title + ", wikid=" + wikid + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((wikid == null) ? 0 : wikid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WikiArticleOld other = (WikiArticleOld) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (wikid == null) {
			if (other.wikid != null)
				return false;
		} else if (!wikid.equals(other.wikid))
			return false;
		return true;
	}

	

}
