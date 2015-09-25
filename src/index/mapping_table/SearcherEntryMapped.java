package index.mapping_table;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;

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

public class SearcherEntryMapped {

	static final String IndexPath = "util/index_lucene/";

	private static final float SCORE_THRESHOLD = 0.5f;
	private static final String Field = "title";

	private static StandardAnalyzer analyzer;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private static QueryParser parser;

	public SearcherEntryMapped() throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, Field, analyzer);
	}

	public static void main(String[] args) throws Exception {
		executeQuery();
	}

	private static void executeQuery() throws IOException, UnsupportedEncodingException {
		String queries = null;
		String queryString = null;
		int hitsPerPage = 10;

		reader = DirectoryReader.open(FSDirectory.open(new File(IndexPath)));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer(Version.LUCENE_47);
		parser = new QueryParser(Version.LUCENE_47, Field, analyzer);

		BufferedReader in = null;
		in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

		while (true) {
			if (queries == null && queryString == null) { // prompt the user
				System.out.println("Enter query: ");
			}

			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			try {
				Query query = parser.parse(line);
				String readableQuery = query.toString(Field);
				System.out.println("Searching...");

				TopDocs results = searcher.search(query, 5 * hitsPerPage);
				ScoreDoc[] hits = results.scoreDocs;

				/* print results */
				//System.out.println("Found " + hits.length + " hits.");
				for(int i=0;i<hits.length;++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					System.out.println("title: " + d.get("title") + " mid: " + d.get("mid"));
				}

				if (hits.length == 0)
					System.out.println("No results found for:\t"
							+ readableQuery);
				else
					System.out.println(hits.length + " results found for:\t"
							+ readableQuery);
				
			} catch (ParseException e) {
				System.err.println("Incorrect Query");
			}
		}
	}


	public List<EntryMappedBean> searchRecordFor(String wikid) throws IOException, UnsupportedEncodingException {

		List<EntryMappedBean> mappingResults = new ArrayList<EntryMappedBean>();
		int maxHits = 10;
	
		try {
			Query query = parser.parse(wikid);
			//System.out.println("Searching...");

			TopDocs results = searcher.search(query, maxHits);
			ScoreDoc[] hits = results.scoreDocs;

			for(int i=0;i<hits.length;++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				mappingResults.add(new EntryMappedBean(d.get("title"),  d.get("mid")));
			}

		} catch (ParseException e) {
			System.out.println(wikid);
			System.err.println("Incorrect Query");
		}
		return mappingResults;
	}



}