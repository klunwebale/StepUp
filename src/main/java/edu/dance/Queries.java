package edu.dance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

import scala.util.parsing.json.JSON;

public class Queries {

	GraphDatabaseService graphDb=null;
	SearchNodes searchNode =null;

	public Queries(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		this.searchNode = new SearchNodes(graphDb);
	}

	public void findAllRelationsOfADance(String node1_name, String node2_name)
	{
		Node node1 = searchNode.searchNodes("danceIndex", "DanceName", node1_name.trim());
		//System.out.println(node1.getProperty("Type"));
		Node node2 = searchNode.searchNodes("danceIndex", "DanceName", node2_name.trim());
		//System.out.println(node2.getProperty("Type"));

		Node reln;
		Node reln2;
		Iterable<Relationship> rels = node1.getRelationships();
		for (Relationship rel: rels) {
			System.out.println(rel.getProperty("propertyname"));
			reln = rel.getOtherNode(node1);
			if(reln.getProperty("Type").equals("CategoryNode"))
				System.out.println(reln.getProperty("CategoryName") + "  id: "+reln.getId());
			System.out.println("for node1  "+reln.getLabels());
		}

		Iterable<Relationship> rels2 = node2.getRelationships();
		for (Relationship rel2: rels2) {
			System.out.println(rel2.getProperty("propertyname"));
			reln2 = rel2.getOtherNode(node2);
			if(reln2.getProperty("Type").equals("CategoryNode"))
				System.out.println(reln2.getProperty("CategoryName")+ "  id: "+reln2.getId());

			System.out.println("for node2  "+reln2.getLabels());

		}
	}

	public JSONObject findCentrality_dance(String node1_name, String node2_name)
	{
		Node node1 = searchNode.searchNodes("danceIndex", "DanceName", node1_name.trim());
		System.out.println(node1);
		Node node2 = searchNode.searchNodes("danceIndex", "DanceName", node2_name.trim());
		System.out.println(node2);
		find_all_path(node1,node2);
		
		JSONObject drawAllNodes = new JSONObject();
		drawAllNodes = find_all_path(node1,node2);
		
		drawAllNodes.put("start",node1_name);
		drawAllNodes.put("end",node2_name);
		drawAllNodes.put("type","Dance");
		return drawAllNodes;



	}

	public JSONObject findCentrality_moves(String node1_name, String node2_name)
	{

		Node node1 = searchNode.searchNodes("moveIndex", "MoveName", node1_name.trim());
		System.out.println(node1);
		Node node2 = searchNode.searchNodes("moveIndex", "MoveName", node2_name.trim());
		System.out.println(node2);
		find_all_path(node1,node2);
		JSONObject drawAllNodes = new JSONObject();
		drawAllNodes = find_all_path(node1,node2);
		
		drawAllNodes.put("start",node1_name);
		drawAllNodes.put("end",node2_name);
		drawAllNodes.put("type","Move");
		return drawAllNodes;


	}

	public JSONObject findCentrality_ParentMoves(String node1_name, String node2_name)
	{

		Node node1 = searchNode.searchNodes("masteMoveIndex", "MasterMoveName", node1_name.trim());
		System.out.println(node1);
		Node node2 = searchNode.searchNodes("masteMoveIndex", "MasterMoveName", node2_name.trim());
		System.out.println(node2);
		find_all_path(node1,node2);
		
		JSONObject drawAllNodes = new JSONObject();
		drawAllNodes = find_all_path(node1,node2);
		
		drawAllNodes.put("start",node1_name);
		drawAllNodes.put("end",node2_name);
		drawAllNodes.put("type","MM");
		return drawAllNodes;


	}


	public JSONObject findCentrality_category(String node1_name, String node2_name)
	{
		Node node1 = searchNode.searchNodes("categoryIndex", "CategoryName", node1_name.trim());
		System.out.println(node1);
		Node node2 = searchNode.searchNodes("categoryIndex", "CategoryName", node2_name.trim());
		System.out.println(node2);
		
		JSONObject drawAllNodes = new JSONObject();
		drawAllNodes = find_all_path(node1,node2);
		
		drawAllNodes.put("start",node1_name);
		drawAllNodes.put("end",node2_name);
		drawAllNodes.put("type","Category");
		return drawAllNodes;

	}
	
	public JSONObject findCentrality_artists(String node1_name, String node2_name)
	{
		Node node1 = searchNode.searchNodes("artistIndex", "ArtistName", node1_name.trim());
		System.out.println(node1);
		Node node2 = searchNode.searchNodes("artistIndex", "ArtistName", node2_name.trim());
		System.out.println(node2);
		
		JSONObject drawAllNodes = new JSONObject();
		drawAllNodes = find_all_path(node1,node2);
		
		drawAllNodes.put("start",node1_name);
		drawAllNodes.put("end",node2_name);
		drawAllNodes.put("type","Artist");
		return drawAllNodes;

	}


	public JSONObject find_all_path(Node start, Node end) {
		if(start == null || end == null) {
			return null;
		}
		
		PathFinder<Path> finder = GraphAlgoFactory.allPaths(
				PathExpanders.allTypesAndDirections(), 3);
		Iterable<Path> paths = finder.findAllPaths(start, end);
		Iterator<Path> itrPath = paths.iterator();
		
		JSONObject drawNodes = new JSONObject();
		JSONArray moves = new JSONArray();
		JSONArray categories = new JSONArray();
		JSONArray artists = new JSONArray();
		JSONArray mms = new JSONArray();
		JSONArray dances = new JSONArray();
		JSONArray beats = new JSONArray();
		JSONArray origins = new JSONArray();
		
		while(itrPath.hasNext()) {
			//			System.out.println("path:------ " + pathCount++);
			Iterator<Node> itr = itrPath.next().nodes().iterator();
			Node current;
			StringBuilder buf = new StringBuilder();
			
			while(itr.hasNext()) {
				current = itr.next();
				try {
					if(current.equals(start))
						continue;
					
					if(current.equals(end))
						continue;
					
					String type = current.getProperty("Type").toString();
					
					if(type.equalsIgnoreCase("DanceNode")) {
							buf.append(current.getProperty("DanceName").toString()+" :"+ current.getId()+ "  -D-> " );
							JSONObject objJSON = new JSONObject();
							objJSON.put("key", "DanceNode");
							objJSON.put("value",current.getProperty("DanceName").toString());
							dances.put(objJSON);
					} 
					
					else if(type.equalsIgnoreCase("Move")) {
						buf.append(current.getProperty("MoveName").toString()  +" :"+ current.getId()+ "  -M-> ");
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "MoveNode");
						objJSON.put("value",current.getProperty("MoveName").toString());
						moves.put(objJSON);
					} 
					
					else if(type.equalsIgnoreCase("CategoryNode")) {
						
						buf.append(current.getProperty("CategoryName").toString()+" :"+ current.getId() + "  -C-> ");
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "CategoryNode");
						objJSON.put("value",current.getProperty("CategoryName").toString());
						categories.put(objJSON);
						
					} 
					
					else if(type.equalsIgnoreCase("ArtistNode")) {
						buf.append(current.getProperty("ArtistName").toString() +" :"+ current.getId()+ "  -A-> ");
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "ArtistNode");
						objJSON.put("value",current.getProperty("ArtistName").toString());
						artists.put(objJSON);
					} 
					
					else if(type.equalsIgnoreCase("MasterMove")) {
						buf.append(current.getProperty("MasterMoveName").toString()+" :"+ current.getId() + "  -MM-> "); 
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "MasterMove");
						objJSON.put("value",current.getProperty("MasterMoveName").toString());
						mms.put(objJSON);
					}
					
					else if(type.equalsIgnoreCase("OriginNode")) {
						buf.append(current.getProperty("OriginName").toString()+" :"+ current.getId() + "  -O-> "); 
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "OriginName");
						objJSON.put("value",current.getProperty("OriginName").toString());
						origins.put(objJSON);
					} 
					
					else if(type.equalsIgnoreCase("BeatNode")) {
						
						buf.append(current.getProperty("BeatName").toString()+" :"+ current.getId() + "  -O-> "); 
						JSONObject objJSON = new JSONObject();
						objJSON.put("key", "BeatName");
						objJSON.put("value",current.getProperty("BeatName").toString());
						beats.put(objJSON);
						
					} 
					
					else {
						buf.append(type + "  --> ");
//						JSONObject objJSON = new JSONObject();
//						objJSON.put("node","No Matches");
//						drawNodes.put(objJSON);
					
						}

					
				} catch(Exception e1) {
					System.out.println(e1.getMessage());
					Iterator<String> itr3 = current.getPropertyKeys().iterator();
					while(itr3.hasNext()) {
						String k  =itr3.next();
						System.out.println(k + " : " + current.getProperty(k));
					}

				}
				
			}
			System.out.println(buf.toString());

		}
		drawNodes.put("categories",categories);
		drawNodes.put("artists",artists);
		drawNodes.put("moves",moves);
		drawNodes.put("mms",mms);
		drawNodes.put("beats", beats);
		drawNodes.put("origins", origins);
		drawNodes.put("dances", dances);
		
		return drawNodes;
	}

	
	public JSONObject findNodesUnderACriteria(String criteria,String criteriaName){
		Node criteriaNode = null;
		Node dance;
		
		JSONObject retVal = new JSONObject();
		ArrayList<JSONObject> res=new ArrayList<JSONObject>();
		
		if("Category".endsWith(criteria)){
			criteriaNode = searchNode.searchNodes("categoryIndex", "CategoryName", criteriaName);
			//System.out.println("criteria Node :" + criteriaNode.getId());

			Iterable<Relationship> rels2 = criteriaNode.getRelationships();
			for (Relationship rel2: rels2) {
				System.out.println(rel2.getProperty("propertyname"));
				dance = rel2.getOtherNode(criteriaNode);
				if(dance.getProperty("Type").equals("DanceNode")){
					System.out.println("------------Printing classification-------");
					res.add(findAllMoves(dance.getProperty("DanceName").toString()));
					
				}
				System.out.println(dance.getProperty("DanceName")+ "  id: "+ dance.getId());
			}
			retVal.put("input", criteria);
			retVal.put("criteriaName", criteriaName);
			retVal.put("dances", res);
		}
		else if("Origin".equals(criteria)){
			System.out.println("----------------Origin---------------------");
			criteriaNode = searchNode.searchNodes("originIndex", "OriginName", criteriaName);
			//System.out.println("criteria Node :" + criteriaNode.getId());

			Iterable<Relationship> rels2 = criteriaNode.getRelationships();
			for (Relationship rel2: rels2) {
				System.out.println(rel2.getProperty("propertyname"));
				dance = rel2.getOtherNode(criteriaNode);
				if(dance.getProperty("Type").equals("DanceNode")){
					System.out.println("------------Printing classification-------");
					res.add(findAllMoves(dance.getProperty("DanceName").toString()));
				}
				System.out.println(dance.getProperty("DanceName")+ "  id: "+ dance.getId());
			}
			retVal.put("input", criteria);
			retVal.put("criteriaName", criteriaName);
			retVal.put("dances", res);
		}

		else if("Beats".equals(criteria)){
			criteriaNode = searchNode.searchNodes("beatIndex", "BeatName", criteriaName);
			//System.out.println("criteria Node :" + criteriaNode.getId());

			Iterable<Relationship> rels2 = criteriaNode.getRelationships();
			for (Relationship rel2: rels2) {
				System.out.println(rel2.getProperty("propertyname"));
				dance = rel2.getOtherNode(criteriaNode);
				if(dance.getProperty("Type").equals("DanceNode")){
					System.out.println("------------Printing classification-------");
					res.add(findAllMoves(dance.getProperty("DanceName").toString()));
				}
				System.out.println(dance.getProperty("DanceName")+ "  id: "+ dance.getId());
			}
			retVal.put("input", criteria);
			retVal.put("criteriaName", criteriaName);
			retVal.put("dances", res);
		}

		else if("Artist".endsWith(criteria)){
			criteriaNode = searchNode.searchNodes("artistIndex", "ArtistName", criteriaName);
			//System.out.println("criteria Node :" + criteriaNode.getId());

			Iterable<Relationship> rels2 = criteriaNode.getRelationships();
			for (Relationship rel2: rels2) {
				System.out.println(rel2.getProperty("propertyname"));
				dance = rel2.getOtherNode(criteriaNode);
				if(dance.getProperty("Type").equals("DanceNode")){
					System.out.println("------------Printing classification-------");
					res.add(findAllMoves(dance.getProperty("DanceName").toString()));
				}
				System.out.println(dance.getProperty("DanceName")+ "  id: "+ dance.getId());
			}
			retVal.put("input", criteria);
			retVal.put("criteriaName", criteriaName);
			retVal.put("dances", res);
		}

		System.out.println("RETURNED JSON!!!");
		return retVal;
	}

	
	public void depthFirstSearch(String start) {
		Node startNode = searchNode.searchNodes("danceIndex", "DanceName", start.trim());

		int numberOfNodes = 0;
		StringBuilder buf = new StringBuilder();
		buf.append(startNode.getProperty( "DanceName" ) + "'s moves:\n");
		org.neo4j.graphdb.traversal.Traverser friendsTraverser = getMoves( startNode );
		for ( Path friendPath : friendsTraverser )
		{
			String type = friendPath.endNode().getProperty("Type").toString();
			if(type.equalsIgnoreCase("DanceNode")) {
				buf.append("\n"+numberOfNodes+"  -D-> " + friendPath.endNode().getProperty("DanceName").toString()+" :"+ friendPath.endNode().getId()) ;
			} else if(type.equalsIgnoreCase("Move")) {
				System.out.println();
				buf.append( "\n"+numberOfNodes+"  -M-> "+ friendPath.endNode().getProperty("MoveName").toString()  +" :"+ friendPath.endNode().getId());
			}	
			else{
				buf.append("extras :"+ friendPath.endNode().getId());
			}

			numberOfNodes++;
		}
		buf.append( "\n" + "Number of moves found: " + numberOfNodes + "\n");
		System.out.println(buf.toString());


	}

	private static org.neo4j.graphdb.traversal.Traverser getMoves(final Node start )
	{
		TraversalDescription td = Traversal.description()
				.depthFirst()
				.relationships( CreateNodes.RelTypes.hasMove , Direction.BOTH )
				.evaluator( Evaluators.toDepth(2));
		return td.traverse( start );
	}

	public class danceMoveCount{
		public String danceName;
		public Integer count;

		public danceMoveCount(String danceName,Integer count){
			this.danceName = danceName;
			this.count =  count;
		}

	}


	public JSONObject findAllMoves(String danceName){
		JSONObject retVal = new JSONObject();
		HashMap<String, ArrayList<String>> moves = new HashMap<String, ArrayList<String>>();
		
		Node moveNode,parentNode;
		Integer count1=0;
		System.out.println("for dance:"+ danceName);
		Node danceNode = searchNode.searchNodes("danceIndex", "DanceName", danceName.trim());
		Iterable<Relationship> rels = danceNode.getRelationships(CreateNodes.RelTypes.hasMove,Direction.BOTH);
		for (Relationship rel12: rels) {
			count1++;
			moveNode= rel12.getOtherNode(danceNode);	
			Iterable<Relationship> relspp = moveNode.getRelationships(CreateNodes.RelTypes.parentMove,Direction.BOTH);
			for (Relationship rel123: relspp) {
				parentNode= rel123.getOtherNode(moveNode);
				System.out.println(count1+":"+ moveNode.getProperty("MoveName")+":"+parentNode.getProperty("MasterMoveName"));
				if(!moves.containsKey(parentNode.getProperty("MasterMoveName").toString())) {
					moves.put(parentNode.getProperty("MasterMoveName").toString(), new ArrayList<String>());
				}
				if(!moves.get(parentNode.getProperty("MasterMoveName").toString()).contains(moveNode.getProperty("MoveName").toString()))
					moves.get(parentNode.getProperty("MasterMoveName").toString()).add(moveNode.getProperty("MoveName").toString());
			}
		}
		retVal.put("danceName", danceName);
		retVal.put("moves", moves);
		return retVal;
	}
	
	public JSONArray findNearestDance(String start){
		JSONArray dataPoints = new JSONArray();
		
		ArrayList<danceMoveCount> map  = new ArrayList<Queries.danceMoveCount>();
		Node startNode = searchNode.searchNodes("danceIndex", "DanceName", start.trim());
		PathFinder<Path> finder = GraphAlgoFactory.allPaths(
				PathExpanders.forTypeAndDirection(CreateNodes.RelTypes.hasMove,Direction.BOTH ), 2 );
		Index<Node> nodeIndex = graphDb.index().forNodes( "danceIndex");
		for ( Node n : nodeIndex.query("DanceName:*"))  {
			if(n.equals(startNode))
				continue;
			Iterator<Path> paths = finder.findAllPaths(startNode, n).iterator();
			int count = 0;
			while(paths.hasNext()) {
				count++;
				System.out.println(paths.next());
			}

			if(count == 0) {
				continue;
			}

			System.out.println(startNode.getProperty("DanceName").toString()+":"+ count+"->"+n.getProperty("DanceName"));
			map.add(new danceMoveCount(n.getProperty("DanceName").toString(), count));
		}

		Collections.sort(map, new Comparator<danceMoveCount>() {

			public int compare(danceMoveCount o1, danceMoveCount o2) {

				return o2.count - o1.count;
			}
		});
		int total = map.size() > 6 ? 6 : map.size(); 
		for(int i=0; i<total; i++) {
			JSONObject objJSON = new JSONObject();
			objJSON.put("key", map.get(i).danceName);
			objJSON.put("value", map.get(i).count);
			dataPoints.put(objJSON);
		}

		System.out.println("closest dance:" + map.get(0).danceName);
		return dataPoints;

	}
	
}