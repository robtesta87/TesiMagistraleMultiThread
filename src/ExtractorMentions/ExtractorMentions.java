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
					wikiArticle.addMention(splitted[0]);
					text = text.replace(mentionString, "[["+splitted[0]+"]]");
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
	
	public static String CleanText (String text){
		//pulizia testo
		WikiModel wikiModel = new WikiModel("http://www.mywiki.com/wiki/${image}", "http://www.mywiki.com/wiki/${title}");
		text = wikiModel.render(new PlainTextConverter(), text);
		return text;
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

	public static void main(String[] args) {

	}
}
