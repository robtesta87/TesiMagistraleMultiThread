package ExtractorMentions;

public class prova {
	public static void main(String[] args) {
		String stringCleaned = "text|wikid";
		if(stringCleaned.contains("|")){
			System.out.println("yes");
			String[] splitted = stringCleaned.split("|");
			System.out.println(splitted[0]);
		}
	}
}
