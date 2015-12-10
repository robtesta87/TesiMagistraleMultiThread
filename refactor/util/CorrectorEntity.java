package util;

public class CorrectorEntity {

	public String correctSpecialCharacters(String wikid) {
		if(wikid.contains("#")){
			wikid = (wikid.split("#"))[0];
		}

		wikid = wikid.replace("/", "\\/");
		wikid = wikid.replace("!", "\\!");
		wikid = wikid.replace("?", "\\?");
		wikid = wikid.replace("(", "\\(");
		wikid = wikid.replace(")", "\\)");
		wikid = wikid.replace("[", "\\[");
		wikid = wikid.replace("]", "\\]");
		wikid = wikid.replace("+", "\\+");
		wikid = wikid.replace("-", "\\-");
		wikid = wikid.replace(":", "\\:");
		wikid = wikid.replace("*", "\\*");
		wikid = wikid.replace("~", "\\~");
		wikid = wikid.replace("^", "\\^");
		return wikid;
	}

}
