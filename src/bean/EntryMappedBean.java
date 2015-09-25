package bean;

public class EntryMappedBean {

	private String wikid;
	private String mid;
	
	
	public EntryMappedBean(String wikipediaId,String freebaseId) {
		wikid = wikipediaId;
		mid = freebaseId;
		
	}
	
	public String getMid() {
		return mid;
	}
	
	public String getWikid() {
		return wikid;
	}
	
	public void setMid(String mid) {
		this.mid = mid;
	}

	public void setWikid(String wikid) {
		this.wikid = wikid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mid == null) ? 0 : mid.hashCode());
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
		EntryMappedBean other = (EntryMappedBean) obj;
		if (mid == null) {
			if (other.mid != null)
				return false;
		} else if (!mid.equals(other.mid))
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
		return "MappingBean [wikid=" + wikid + ", mid=" + mid + "]";
	}
	
	
}


