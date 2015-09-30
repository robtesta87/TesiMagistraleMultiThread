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
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		EntryMention other = (EntryMention) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (wikid == null) {
			if (other.wikid != null)
				return false;
		} else if (!wikid.equals(other.wikid))
			return false;
		return true;
	};
	
	
	
}
