package bean;

public class EntryMention {
	
	private String text;
	private String wikid;
	
	public EntryMention() {
		
	}
	public EntryMention(String text, String wikid) {
		super();
		this.text = text;
		this.wikid = wikid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getWikid() {
		return wikid;
	}

	public void setWikid(String wikid) {
		this.wikid = wikid;
	}

	@Override
	public String toString() {
		return "EntryMention [text=" + text + ", wikid=" + wikid + "]";
	};
	
	
	
}
