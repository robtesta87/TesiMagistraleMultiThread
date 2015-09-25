package persistence.dao;

import java.io.IOException;
import java.util.List;

import Configuration.Configuration;
import bean.EntryMappedBean;
import index.mapping_table.SearcherEntryMapped;

public class EntryMappedDAOImpl implements EntryMappedDAO {


	private SearcherEntryMapped searcher;
	static private EntryMappedDAOImpl _instance = null;

	public EntryMappedDAOImpl() {
		try {
			searcher = new SearcherEntryMapped();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public EntryMappedBean getMidFromWikID(String wikid) {
		EntryMappedBean mappingResult = null;

		try {
			List<EntryMappedBean> listOfresults = searcher.searchRecordFor(wikid);
			if(listOfresults.size()>0)
				mappingResult = listOfresults.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mappingResult;
	}


	public static void main(String[] args) {
		//String wikid = "Dracula_(1931_English-language_film)";
		String wikid = "Byzantine_Empire";
		EntryMappedDAO dao = new EntryMappedDAOImpl();

		EntryMappedBean mappingBean = dao.getMidFromWikID(wikid);
		if(mappingBean!=null){
			System.out.println("Ricerca per :"+wikid);
			System.out.println("Risultato: "+mappingBean.getMid());
		}
		else{
			System.out.println("Nessun risultato per "+wikid+"!");
		}
	}
	
	static public EntryMappedDAOImpl instance(){
		if (_instance == null) {
			System.out.println("creazione oggetto");
			_instance = new EntryMappedDAOImpl();
		}
		return _instance;
	}
}
