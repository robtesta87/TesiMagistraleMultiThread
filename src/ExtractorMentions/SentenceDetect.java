package ExtractorMentions;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class SentenceDetect {
	
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

	public static void main(String[] args) {
		String paragraph = "Back to the Future is a 1985 American comic science fiction film directed by Robert Zemeckis, written by Zemeckis and Bob Gale, produced by Gale and Neil Canton, and stars Michael J. Fox, Christopher Lloyd, Lea Thompson, Crispin Glover and Thomas F. Wilson. Steven Spielberg, Kathleen Kennedy, and Frank Marshall served as executive producers. In the film, teenager Marty McFly (Fox) is sent back in time to 1955, where he meets his future parents in high school and accidentally becomes his mother's romantic interest. Marty must repair the damage to history by causing his parents-to-be to fall in love, and with the help of eccentric scientist Dr. Emmett \"Doc\" Brown (Lloyd), he must find a way to return to 1985.";
		List<String> sentenceList = getSentences(paragraph);
		int i=1;
		for (String sentence : sentenceList) {
			System.out.println(i+": "+sentence);
			i++;
		}
	}
}
