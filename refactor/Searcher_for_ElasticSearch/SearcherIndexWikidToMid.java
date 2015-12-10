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

public class SearcherIndexWikidToMid {
	private final static String index_name = "index_mapping_wikid_mid";
	private final static String index_type = "wikid_mid";

	public SearcherIndexWikidToMid() {

	}


	public String findMidFor(Client client, String wikid){
		CorrectorEntity corrector = new CorrectorEntity();
		wikid = corrector.correctSpecialCharacters(wikid);

		QueryBuilder queryBuilder = QueryBuilders.matchQuery("wikid",wikid);

		SearchResponse response = client.prepareSearch(index_name)
				.setTypes(index_type)
				.setSearchType(SearchType.DEFAULT)
				.setQuery(queryBuilder)
				.execute().actionGet();

		SearchHit[] results = response.getHits().getHits();
		if(results.length > 0) {
			return (String)results[0].getSource().get("mid");
		}


		return null;

	}

	public static void main(String[] args) {
		Client client = null;
		try {
			SearcherIndexWikidToMid searcher = new SearcherIndexWikidToMid();
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
			Thread.sleep(5000);
			String mid= searcher.findMidFor(client, "Silvio_Berlusconi");
			System.out.println("Mid trovato: "); 
			System.out.println(mid);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
