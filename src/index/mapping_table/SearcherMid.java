package index.mapping_table;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

import bean.EntryMappedBean;

public class SearcherMid {
	static final String IndexPath = "util/index_lucene/";

	private static final float SCORE_THRESHOLD = 0.5f;
	private static final String Field = "title";

	private static StandardAnalyzer analyzer;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private static QueryParser parser;

	public SearcherMid() throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, Field, analyzer);
	}

	public EntryMappedBean getMid(String wikid) throws IOException, UnsupportedEncodingException {
		EntryMappedBean entryMid =null;
		List<EntryMappedBean> mappingResults = new ArrayList<EntryMappedBean>();
		int maxHits = 10;

		try {
			if (!wikid.equals("")){
				//System.out.println("WIKID: "+wikid);
				Query query = parser.parse(QueryParser.escape(wikid));

				TopDocs results = searcher.search(query, maxHits);
				ScoreDoc[] hits = results.scoreDocs;
				if (hits.length>0){
					int docId = hits[0].doc;
					Document d = searcher.doc(docId);
					mappingResults.add(new EntryMappedBean(d.get("title"),  d.get("mid")));
				}
			}

		} catch (ParseException e) {
			System.err.println("Incorrect Query");
		}
		if (mappingResults.size()>0){
			entryMid = mappingResults.get(0);
		}

		return entryMid;
	}
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		SearcherMid sm = new SearcherMid();
		//EntryMappedBean e = getMid("Alfred_Nobel");
		//System.out.println(e.getMid());
	}
}
