package ExtractorMentions;

import index.mapping_table.SearcherMid;
import index.redirect.SearcherRedirect;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import CutterText.CutterText;
import bean.EntryRedirect;
import bean.WikiArticleOld;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;

public class ExtractorMentions {
	final static String mentionRegex = "\\[\\[[\\w+\\sÉé?!#,\"'.îóçë&–üáà:°í#ἀνã\\|\\(\\)_-]*\\]\\]";
	final static String boldRegex = "\"'[\\w+\\s\"]*\"'";

	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator = 
				new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = k1.toString().length()-k2.toString().length();
				if (compare > 0) 
					return -1;
				else 
					return 1;
			}
		};

		Map<K, V> sortedByValues = 
				new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}
	public enum Version{
		Base,
		Intermedia,
		Completa;
	}
	public static void extractMentionsRefactoring (String text, WikiArticleOld wikiArticle){

		wikiArticle.addMention(wikiArticle.getTitle());
		Pattern pattern = Pattern.compile(mentionRegex);
		Matcher matcher = pattern.matcher(text);
		String mentionString=null;
		String stringCleaned=null;
		String[] splitted=null;
		while(matcher.find()){
			mentionString = matcher.group();
			stringCleaned = mentionString.substring(2, mentionString.length()-2);
			
			if(stringCleaned.contains("|")){
				splitted = stringCleaned.split("\\|");
				//primo campo: text secondo campo: wikiid
				if (!(splitted[0].contains("#"))){
					if (!splitted[0].equals("")){
						wikiArticle.addMention(splitted[0]);
						text = text.replace(mentionString, "[["+splitted[0]+"]]");
					}
					else{
						text = text.replace(mentionString, splitted[1]);
					}
				}
				
			}
			else{
				//evito di inserire le sotto-mention
				if (!(stringCleaned.contains("#")))
					wikiArticle.addMention(stringCleaned);
				else{
					//provo a prendere l'articolo riguardante la sotto-mention anzichè scartarla
					wikiArticle.addMention(stringCleaned.split("#")[0]);
				}

			}

		}	

		//rilevamento mention dalle parole in grassetto
		pattern = Pattern.compile(boldRegex);
		if (text.length()>300)
			matcher = pattern.matcher(text.substring(0, 300));
		else
			matcher = pattern.matcher(text.substring(0, text.length()-1));
		while(matcher.find()){
			mentionString = matcher.group();
			stringCleaned = mentionString.substring(3, mentionString.length()-3);
			wikiArticle.addMention(stringCleaned, wikiArticle.getWikid());
		}	

		wikiArticle.setText(text);
	}
	
	public  void extractMentions (String text, WikiArticleOld wikiArticle){

		wikiArticle.addMention(wikiArticle.getTitle());
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

		//rilevamento mention dalle parole in grassetto
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
	
	public static String CleanText (String text){
		//pulizia testo
		WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
		text = wikiModel.render(new PlainTextConverter(), text);
		return text;
	}
	
	public void addPerson(List<String> entities, WikiArticleOld wikiArticle){
		String currentEntity= null;
		TreeMap<String, Pair<String,String>> treemap = null;

		for (int i = 0; i < entities.size(); i++) {
			currentEntity = entities.get(i);
			treemap = wikiArticle.getWikiEntities();
			String[] personSpitted = currentEntity.split(" ");
			Pair<String,String> pair = treemap.get(currentEntity);
			if (currentEntity.equals(wikiArticle.getTitle())){
				wikiArticle.addMention(currentEntity,pair.first,pair.second);
				wikiArticle.addMentionPerson(personSpitted[personSpitted.length-1],wikiArticle.getWikid(),pair.second);
			}
			else{

				String[] titleSplitted = wikiArticle.getTitle().split(" ");

				if ((wikiArticle.getTitle().contains(currentEntity))&&(titleSplitted[titleSplitted.length-1].equals(personSpitted[personSpitted.length-1]))){
					pair =treemap.get(wikiArticle.getTitle());
					wikiArticle.addMention(currentEntity,pair.first,pair.second);
					wikiArticle.addMentionPerson(personSpitted[personSpitted.length-1],wikiArticle.getWikid(),pair.second);

				}
				else{
					if (pair!=null){
						wikiArticle.addMention(currentEntity,pair.first,pair.second);
						wikiArticle.addMentionPerson(personSpitted[personSpitted.length-1],pair.first,pair.second);
					}
				}
			}
		}
	}
	
	public void addEntity(List<String> entities, WikiArticleOld wikiArticle){
		String currentEntity= null;
		TreeMap<String, Pair<String,String>> treemap = wikiArticle.getWikiEntities();

		for (int i = 0; i < entities.size(); i++) {
			currentEntity = entities.get(i);
			if (treemap.containsKey(currentEntity)){
				Pair<String,String> pair = treemap.get(currentEntity);
				wikiArticle.addMention(currentEntity,pair.first,pair.second);
			}
		}

	}
	
	public void addRedirect (List<String> entities, WikiArticleOld wikiArticle,SearcherRedirect searcherRedirect){
		TreeMap<String, Pair<String,String>> treemap = wikiArticle.getWikiEntities();
		for (int i = 0; i < entities.size(); i++) {
			String currentEntity = entities.get(i);
			if (!treemap.containsKey(currentEntity)){
				EntryRedirect mappingBean = null;
				try {
					mappingBean = searcherRedirect.getRedirect(currentEntity);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (mappingBean!=null){
					wikiArticle.addMention(currentEntity, mappingBean.getWikid());
				}
				
			}
		}
	}
	
	public TreeMap<String, Pair<String,String>> getMidModulate (WikiArticleOld wikiArticle, AbstractSequenceClassifier<CoreLabel> classifier, Version version,SearcherMid searcherMid) throws IOException, ClassCastException, ClassNotFoundException{
		String text = wikiArticle.getText();

		CutterText cutterText = new CutterText();
		//elimino le informazioni iniziali dell'articolo
		text = cutterText.cutText(text);

		//elimino tutti i ref presenti nel testo
		text = cutterText.cutRef(text);
		EntityDetect ed = new EntityDetect();
		SentenceDetect sd = new SentenceDetect();
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;

		
		switch (version) {
		case Base:
			extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			wikiArticle.setPhrases(phrases);
			break;
		case Intermedia:
			extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			addEntity(entitiesMap.get("MISC"), wikiArticle);
			addEntity(entitiesMap.get("LOCATION"), wikiArticle);
			
			wikiArticle.setPhrases(phrases);
			break;
		case Completa:
			extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			wikiArticle.setPhrases(phrases);
			break;

		}
		return wikiArticle.getWikiEntities();
	}
	
	public TreeMap<String, Pair<String,String>> getMid(WikiArticleOld wikiArticle, AbstractSequenceClassifier<CoreLabel> classifier, Version version,SearcherMid searcherMid, SearcherRedirect searcherRedirect) throws IOException, ClassCastException, ClassNotFoundException{
		String text = wikiArticle.getText();
		EntityDetect ed = new EntityDetect();
		SentenceDetect sd = new SentenceDetect();
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;
		
		switch (version) {
		case Base:
			extractMentions(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
						
			//phrases = sd.getSentences(wikiArticle.getText());
			//wikiArticle.setPhrases(phrases);
			break;
		case Intermedia:
			extractMentions(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
						
			phrases = sd.getSentences(wikiArticle.getText());
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			
			addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			addEntity(entitiesMap.get("MISC"), wikiArticle);
			addEntity(entitiesMap.get("LOCATION"), wikiArticle);
						
			wikiArticle.setPhrases(phrases);
			break;
		case Completa:
			System.out.println(wikiArticle.toString());
			extractMentions(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
						
			phrases = sd.getSentences(wikiArticle.getText());
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			
			addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			addEntity(entitiesMap.get("MISC"), wikiArticle);
			addEntity(entitiesMap.get("LOCATION"), wikiArticle);
			
			addRedirect(entitiesMap.get("ORGANIZATION"), wikiArticle,searcherRedirect);
			addRedirect(entitiesMap.get("MISC"), wikiArticle,searcherRedirect);
			addRedirect(entitiesMap.get("LOCATION"), wikiArticle,searcherRedirect);
			//wikiArticle.updateMid(searcherMid);
			
			wikiArticle.setPhrases(phrases);
			break;

		}
		return wikiArticle.getWikiEntities();
	}

}
