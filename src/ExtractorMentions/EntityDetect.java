package ExtractorMentions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class EntityDetect {



	public static Map<String,List<String>> getEntitiesFromPhrasesListMap(List<String> phrases, AbstractSequenceClassifier<CoreLabel> classifier){

		List<String> person = new ArrayList<String>();
		List<String> misc = new ArrayList<String>();
		List<String> location = new ArrayList<String>();
		List<String> organization = new ArrayList<String>();
		Map<String,List<String>> entityMap = new HashMap<String, List<String>>();

		try {
			for(String currentPhrase : phrases){
				List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(currentPhrase);
				for (Triple<String,Integer,Integer> trip : triples) {
					String text = currentPhrase.substring(trip.second(), trip.third());
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
		return entityMap;

	}


}
