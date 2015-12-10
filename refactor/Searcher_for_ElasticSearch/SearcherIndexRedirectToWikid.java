package Searcher_for_ElasticSearch;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import util.CorrectorEntity;

public class SearcherIndexRedirectToWikid {
	private final static String index_name = "index_redirect";
	private final static String index_type = "redirect_wikid";

	public SearcherIndexRedirectToWikid() {

	}


	public String findWikidFor(Client client, String redirect){
		CorrectorEntity corrector = new CorrectorEntity();
		redirect = corrector.correctSpecialCharacters(redirect);

		QueryBuilder queryBuilder = QueryBuilders.matchQuery("redirect",redirect);

		SearchResponse response = client.prepareSearch(index_name)
				.setTypes(index_type)
				.setSearchType(SearchType.DEFAULT)
				.setQuery(queryBuilder)
				.execute().actionGet();

		SearchHit[] results = response.getHits().getHits();



		if(results.length > 0)
			return (String) results[0].getSource().get("wikid");
		else
			return null;

	}


	public static void main(String[] args) {
		Client client = null;
		try {
			SearcherIndexRedirectToWikid searcher = new SearcherIndexRedirectToWikid();
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
			String wikid = searcher.findWikidFor(client,"'Dampieri'");
			if(wikid!=null){
				System.out.println("Wikid trovato: "); 
				System.out.println(wikid);
			}
			else
				System.out.println("No result!");


		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
