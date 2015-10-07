package index.redirect;


import java.io.File;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import bean.EntryRedirect;

public class SearcherRedirect {
	static final String IndexPath = "util/index_redirect/";

	private static final float SCORE_THRESHOLD = 0.5f;
	private static final String Field = "redirect";

	private static Analyzer analyzer;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private static QueryParser parser;

	public SearcherRedirect() throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new KeywordAnalyzer();
		parser = new QueryParser(Version.LUCENE_47, Field, analyzer);
	}

	public synchronized EntryRedirect getRedirect(String redirect) throws IOException, UnsupportedEncodingException {
		EntryRedirect entryRedirect = null;
		redirect=redirect.replaceAll(" ", "_");
		List<EntryRedirect> mappingResults = new ArrayList<EntryRedirect>();
		int maxHits = 10;
		
		try {
			if (!(redirect.equals(""))){
				Query query = parser.parse(redirect);

				TopDocs results = searcher.search(query,maxHits);
				ScoreDoc[] hits = results.scoreDocs;
				
				/*if (hits.length>0){
					int docId = hits[0].doc;
					Document d = searcher.doc(docId);
					mappingResults.add(new EntryRedirect(d.get("redirect"),  d.get("wikID")));
					System.out.println(d.get("redirect"));
				}*/
				for(int i=0;i<hits.length;++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					mappingResults.add(new EntryRedirect(d.get("redirect"),  d.get("wikID")));
					
				}
				
			}

		} catch (ParseException e) {
			System.err.println("Incorrect Query");
		}
		if (mappingResults.size()>0){
			entryRedirect = mappingResults.get(0);
		}
		
		return entryRedirect;
	}	

	public static void main(String[] args) {
		SearcherRedirect s = null;
		EntryRedirect mappingResults = null;
		try {
			s = new SearcherRedirect();
			mappingResults = s.getRedirect("BC");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mappingResults!=null)
			System.out.println(mappingResults.toString());
		else
			System.out.println("null");
	}
}
