package multithread;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class prova_SEPARAZIONE_FRASI {
	public static  List<String> getSentences(String text){

		List<String> sentenceList = new ArrayList<String>();
		BreakIterator bi = BreakIterator.getSentenceInstance();
		bi.setText(text);
		int index = 0;
		while (bi.next() != BreakIterator.DONE) {
			String sentence = text.substring(index, bi.current());
			System.out.println("Sentence: " + sentence);
			sentenceList.add(sentence);
			index = bi.current();
		}

		return sentenceList;
	}

	public static List<String> getSentences2(String text){
		Reader reader = new StringReader(text);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();
		String sentenceString = null;
		for (List<HasWord> sentence : dp) {
			sentenceString = Sentence.listToString(sentence);
			System.out.println(sentence);
			sentenceList.add(sentenceString.toString());
		}

		return sentenceList;
	}

	public  void SentenceSplitter(String text){
		SentenceDetector sentenceDetector = null;
		InputStream modelIn = null;

		try {
			modelIn = getClass().getResourceAsStream("/home/roberto/Scrivania/en-sent.bin");
			final SentenceModel sentenceModel = new SentenceModel(modelIn);
			modelIn.close();
			sentenceDetector = new SentenceDetectorME(sentenceModel);
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {}
			}
		}
		String sentences[]=(sentenceDetector.sentDetect(text));
		for(int i=0; i<sentences.length;i++)
		{
			System.out.println(sentences[i]);
		}
	}

	public static void main(String[] args) {
		String text = "\"'Bargoti\"' (FILM) is a gotra of Jats found in Bulandshahr district of Uttar Pradesh in India. Literally it means 'big clan' in sanskrit. Bargoti Jats are also known as Lor/Laur or we can say that correct description for Bargotis should be Bargoti Lor/Laur by which they are generally known in Bulandshahr.They have held prominent positions in the government; of which include doctors, architects,business magnates who have heartily supported active development of the society and the country altogether.Bargotis have played an active role largely in building the nation and for the improvement of national healthcare. They have been working consistently for the prosperity of the nation.";
		//getSentences(text);
		prova_SEPARAZIONE_FRASI prova = new prova_SEPARAZIONE_FRASI();
		//prova.SentenceSplitter(text);
		SentenceDetector sentenceDetector = null;
		InputStream modelIn = null;

		try {
			modelIn = prova.getClass().getResourceAsStream("/home/roberto/Scrivania/en-sent.bin");
			final SentenceModel sentenceModel = new SentenceModel(modelIn);
			modelIn.close();
			sentenceDetector = new SentenceDetectorME(sentenceModel);
		}
		catch (final IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {}
			}
		}
		String sentences[]=(sentenceDetector.sentDetect(text));
		for(int i=0; i<sentences.length;i++)
		{
			System.out.println(sentences[i]);
		}
	}

}
