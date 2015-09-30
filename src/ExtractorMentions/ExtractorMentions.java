package ExtractorMentions;

import index.mapping_table.SearcherMid;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.WikiModel;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Configuration.Configuration;
import CutterText.CutterText;
import bean.WikiArticle;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Pair;

public class ExtractorMentions {
	final static String mentionRegex = "\\[\\[[\\w+\\s#\\|\\(\\)_-]*\\]\\]";
	final static String boldRegex = "'''[\\w+\\s]*'''";

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
	public static void extractMentionsRefactoring (String text, WikiArticle wikiArticle){

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
	
	public  void extractMentions (String text, WikiArticle wikiArticle){

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
			String stringCleaned = mentionString.substring(3, mentionString.length()-3);
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
	
	public void addPerson(List<String> entities, WikiArticle wikiArticle){
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
	
	public void addEntity(List<String> entities, WikiArticle wikiArticle){
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
	
	public TreeMap<String, Pair<String,String>> getMidModulate (WikiArticle wikiArticle, AbstractSequenceClassifier<CoreLabel> classifier, Version version,SearcherMid searcherMid) throws IOException, ClassCastException, ClassNotFoundException{
		String title = wikiArticle.getTitle();
		String text = wikiArticle.getText();

		CutterText cutterText = new CutterText();
		//elimino le informazioni iniziali dell'articolo
		text = cutterText.cutText(text);

		//elimino tutti i ref presenti nel testo
		text = cutterText.cutRef(text);
		ExtractorMentions e = new ExtractorMentions();
		EntityDetect ed = new EntityDetect();
		SentenceDetect sd = new SentenceDetect();
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;

		
		switch (version) {
		case Base:
			e.extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = e.CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			wikiArticle.setPhrases(phrases);
			break;
		case Intermedia:
			e.extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = e.CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			
			e.addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			e.addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			e.addEntity(entitiesMap.get("MISC"), wikiArticle);
			e.addEntity(entitiesMap.get("LOCATION"), wikiArticle);
			
			wikiArticle.setPhrases(phrases);
			break;
		case Completa:
			e.extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
			text = e.CleanText(text);
			wikiArticle.setText(text);
			
			phrases = sd.getSentences(text);
			wikiArticle.setPhrases(phrases);
			break;

		}
		return wikiArticle.getWikiEntities();
	}
	
	public TreeMap<String, Pair<String,String>> getMid(WikiArticle wikiArticle, AbstractSequenceClassifier<CoreLabel> classifier, Version version,SearcherMid searcherMid) throws IOException, ClassCastException, ClassNotFoundException{
		String title = wikiArticle.getTitle();
		String text = wikiArticle.getText();

		ExtractorMentions e = new ExtractorMentions();
		EntityDetect ed = new EntityDetect();
		SentenceDetect sd = new SentenceDetect();
		List<String> phrases = null;
		Map<String,List<String>> entitiesMap = null;
		
		switch (version) {
		case Base:
			e.extractMentions(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
						
			phrases = sd.getSentences(text);
			wikiArticle.setPhrases(phrases);
			break;
		case Intermedia:
			e.extractMentionsRefactoring(text, wikiArticle);
			//wikiArticle.updateMid(searcherMid);
						
			phrases = sd.getSentences(text);
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			
			//e.addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			//e.addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			//e.addEntity(entitiesMap.get("MISC"), wikiArticle);
			//e.addEntity(entitiesMap.get("LOCATION"), wikiArticle);
			
			
			wikiArticle.setPhrases(phrases);
			break;
		case Completa:
			e.extractMentionsRefactoring(text, wikiArticle);
			wikiArticle.updateMid(searcherMid);
						
			phrases = sd.getSentences(text);
			
			//mappa di tutte le entità riconosciute dal NER con duplicati
			entitiesMap = ed.getEntitiesFromPhrasesListMap(phrases, classifier);
			
			e.addPerson(entitiesMap.get("PERSON"), wikiArticle);
			
			//controllo se nelle entità ci sono dei match esatti nelle mention originali per il controllo quantitativo
			e.addEntity(entitiesMap.get("ORGANIZATION"), wikiArticle);
			e.addEntity(entitiesMap.get("MISC"), wikiArticle);
			e.addEntity(entitiesMap.get("LOCATION"), wikiArticle);
			
			
			
			wikiArticle.setPhrases(phrases);
			break;

		}
		return wikiArticle.getWikiEntities();
	}

	public static void main(String[] args) {

	}
}
