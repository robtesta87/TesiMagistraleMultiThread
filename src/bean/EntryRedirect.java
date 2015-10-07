package bean;

public class EntryRedirect {
	private String wikid;
	private String redirect;
	
	
	public EntryRedirect(String redirect,String wikipediaId) {
		wikid = wikipediaId;
		this.redirect = redirect;
		
	}
	
	
	
	public String getRedirect() {
		return redirect;
	}



	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}



	public String getWikid() {
		return wikid;
	}
	


	public void setWikid(String wikid) {
		this.wikid = wikid;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((redirect == null) ? 0 : redirect.hashCode());
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
		EntryRedirect other = (EntryRedirect) obj;
		if (redirect == null) {
			if (other.redirect != null)
				return false;
		} else if (!redirect.equals(other.redirect))
			return false;
		if (wikid == null) {
			if (other.wikid != null)
				return false;
		} else if (!wikid.equals(other.wikid))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "EntryRedirect [wikid=" + wikid + ", redirect=" + redirect + "]";
	}


	
	
}

