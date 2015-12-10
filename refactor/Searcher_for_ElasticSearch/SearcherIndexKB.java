package Searcher_for_ElasticSearch;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class SearcherIndexKB {

	private final static String index_name = "index_kb";
	private final static String index_type = "kb";

	public SearcherIndexKB() {

	}

	public List<String> findRelationsFromFreebase(Client client, String mid1, String mid2){
		List<String> relationsFound = new LinkedList<String>();

		QueryBuilder queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("mid1",mid1)).must(QueryBuilders.matchQuery("mid2",mid2));

		SearchResponse response = client.prepareSearch(index_name)
				.setTypes(index_type)
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(queryBuilder)
				.execute().actionGet();

		SearchHit[] results = response.getHits().getHits();
		for (SearchHit hit : results) {
			relationsFound.add((String) hit.getSource().get("predicate"));
		}

		return relationsFound;
	}


	public Set<String> findTypesFor(Client client, String mid) {
		Set<String> typesFound = new LinkedHashSet<>();

		QueryBuilder queryBuilder = QueryBuilders.matchQuery("mid1",mid);

		SearchResponse response = client.prepareSearch(index_name)
				.setTypes(index_type)
				.setSearchType(SearchType.DEFAULT)
				.setQuery(queryBuilder)
				.execute().actionGet();

		SearchHit[] results = response.getHits().getHits();
		if(results.length > 0){
			String types1 = (String) results[0].getSource().get("types1");
			String[] types1_splitted = types1.split(",");
			for(String type : types1_splitted)
				typesFound.add(type);
		}
		else{
			queryBuilder = QueryBuilders.matchQuery("mid2",mid);

			response = client.prepareSearch(index_name)
					.setTypes(index_type)
					.setSearchType(SearchType.DEFAULT)
					.setQuery(queryBuilder)
					.execute().actionGet();
			SearchHit[] results2 = response.getHits().getHits();
			if(results2.length > 0){
				String types2 = (String) results2[0].getSource().get("types2");
				String[] types2_splitted = types2.split(",");
				for(String type : types2_splitted)
					typesFound.add(type);
			}
		}

		return typesFound;
	}
}
