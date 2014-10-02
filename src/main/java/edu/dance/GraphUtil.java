package edu.dance;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphUtil {

	private static GraphDatabaseService graphDb;
	
	public static GraphDatabaseService getGraphConnection() {
		try {
			String path = System.getProperty("user.home")+"/dance/neo4j_files/";
			
			if (graphDb == null) {
				graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( path );
				registerShutdownHook(graphDb);
			} else if (!graphDb.isAvailable(100)) {
				graphDb.shutdown();
				graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( path );
			}
			return graphDb;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				graphDb.shutdown();
				System.out.println("Shutting down graph server");
				
			}
		});
	}
}
