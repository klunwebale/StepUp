package edu.dance;


import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

public class SearchNodes {

	GraphDatabaseService graphDb;
	Node n1=null;
	SearchNodes(GraphDatabaseService graphDb){
		this.graphDb = graphDb;
	}

	public Node searchNodes(String indexName,String key,String value){
		Transaction tx = graphDb.beginTx();
		try
		{

			Index<Node> nodeIndex = graphDb.index().forNodes( indexName
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			//for ( Node n : nodeIndex.query(key + ":" + value + "*" ) ) {
			for ( Node n : nodeIndex.query(new TermQuery(new Term(key, value))) ) {
				n1=n;
				//System.out.println(n.getProperty("MainMove"));
			}
			tx.success();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return n1;
	}


	public ArrayList<Node> searchNodesByTerm(String indexName,String key,String value){
		ArrayList<Node> searchResults = new ArrayList<Node>();
		Transaction tx = graphDb.beginTx();
		try
		{
			Index<Node> nodeIndex = graphDb.index().forNodes( indexName);
			for ( Node n : nodeIndex.query(key + ":*"+value+"*") ) {
				searchResults.add(n);
			}
			tx.success();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return searchResults;
	}
}

