package edu.dance;


import java.io.BufferedReader;
import java.io.FileReader;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;


public class CreateNodes {

	GraphDatabaseService graphDb=null;
	CreateNodes(GraphDatabaseService graphDb)
	{
		this.graphDb =  graphDb;
	}


	public static enum RelTypes implements RelationshipType
	{

		hasBeat,
		hasTempo,
		hasRhythm,
		hasOrigin,
		hasCategory,
		parentMove,
		hasArtist,
		hasMove

	}

	private static enum LabelTypes implements Label
	{
		Description,
		Dance,
		Link,
		Beat,
		Tempo,
		Rhythm,
		Origin,
		Category,
		Artist,
		Move,
		MasterMove	
	}

	Index<Node> categoryIndex;
	Index<Node> beatIndex;
	Index<Node> tempoIndex;
	Index<Node> rhythmIndex;
	Index<Node> originIndex;
	Index<Node> artistIndex;
	Index<Node> masteMoveIndex;
	Index<Node> moveIndex;
	Index<Node> danceIndex;

	Node firstNode;
	Node secondNode;
	Relationship relationship;

	public void createMasterMoveNodes() 
	{
		//GraphDatabaseService graphDb = GraphUtil.getGraphConnection();
		Transaction tx = graphDb.beginTx();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/glosary-new.csv"));
			String line;

			masteMoveIndex = graphDb.index().forNodes( "masteMoveIndex");

			while ((line = br.readLine()) != null) {
				if(line.length() < 2) {
					continue;
				}
				String[]columns = line.split(",");
				//String value = line.trim().toLowerCase();
				//				Node node = nodeIndex.get( key, value ).getSingle();
				//				if ( node == null )
				//				{
				//System.out.println(value);
				//				if(columns.length<3)
				//					continue;
				Node node = graphDb.createNode();

				node.setProperty( "MasterMoveName", columns[0].trim());
				node.setProperty( "Link", columns[1]);
				node.setProperty( "Description", columns[2]);
				node.setProperty("Type", "MasterMove");
				node.addLabel(LabelTypes.MasterMove);
				System.out.println("mastermove node created "+columns[0]);
				masteMoveIndex.add( node, "MasterMoveName", columns[0] );

			}
			tx.success();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tx.finish();
		}
	}

	public void createMoveNodes() 
	{
		//GraphDatabaseService graphDb = GraphUtil.getGraphConnection();
		Transaction tx = graphDb.beginTx();
		try
		{
//			BufferedReader br = new BufferedReader(new FileReader("F:\\study\\sem 2\\CS548\\project\\neha_proj\\Fril\\Finalest\\dance-move-category.csv"));
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/dance-move-category.csv"));
			String line;

			moveIndex = graphDb.index().forNodes( "moveIndex" 
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);

			danceIndex = graphDb.index().forNodes( "danceIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);


			while ((line = br.readLine()) != null) {
				if(line.length() < 2) {
					continue;
				}
				String[]columns = line.split(",");
				//				String value = line.trim().toLowerCase();
				//				Node node = nodeIndex.get( key, value ).getSingle();
				//				if ( node == null )
				//				{
				//System.out.println(value);
				//				if(columns.length<3)
				//					continue;

				/*Changes by purva*/		
				Relationship rel1,reln;
				String value = columns[2].trim();
				Node mm=  masteMoveIndex.get("MasterMoveName", value).getSingle();
				Node moveNode=  moveIndex.get("MoveName", columns[1].trim()).getSingle();
				if(moveNode==null)
				{
					moveNode = graphDb.createNode();
					moveNode.setProperty( "MoveName", columns[1].trim() );
					moveNode.setProperty("Type", "Move");
					moveNode.addLabel(LabelTypes.Move);
					moveIndex.add( moveNode, "MoveName", columns[1].trim());

				}	// create the move- > parent move realtion

				Relationship rel=moveNode.createRelationshipTo(mm,RelTypes.parentMove);
				rel.setProperty("propertyname", "parentMove");
				System.out.println( moveNode.getId()+":" + moveNode.getProperty( "MoveName" ) +"---"+ rel.getProperty( "propertyname" )+"-----"+mm.getProperty( "MasterMoveName" ));



				Node dance=  danceIndex.get("DanceName", columns[0].trim()).getSingle();
				Node moven=null;
				if(dance==null)
				{
					System.out.println("ERROR:"+columns[0]);
				}

				else
				{

					Iterable<Relationship> rels = dance.getRelationships(CreateNodes.RelTypes.hasMove,Direction.BOTH);
					boolean flag = false;
					for (Relationship rel12: rels) {
						moven= rel12.getOtherNode(dance);
						if(moven.equals(moveNode))
						{
							flag = true;
							break;
						}
					}
					if(flag==false)
					{

						rel1=dance.createRelationshipTo(moveNode, RelTypes.hasMove);
						rel1.setProperty("propertyname", "hasMove");
						System.out.println( dance.getId() +":"+dance.getProperty( "DanceName" ) +"---"+ rel1.getProperty( "propertyname" )+"-----"+ moveNode.getId()+":"+moveNode.getProperty( "MoveName" ));
					}
				}
			}

			tx.success();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tx.finish();
		}
	}

	public void createDanceNodes(){

		Transaction tx = graphDb.beginTx();
		try
		{
//			BufferedReader br = new BufferedReader(new FileReader("F:\\study\\sem 2\\CS548\\project\\neha_proj\\Fril\\Finalest\\mastercsv_final2.tsv"));
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/mastercsv_final2.tsv"));
			String line;

			danceIndex= graphDb.index().forNodes( "danceIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);

			categoryIndex= graphDb.index().forNodes( "categoryIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			beatIndex= graphDb.index().forNodes( "beatIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			tempoIndex= graphDb.index().forNodes( "tempoIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			rhythmIndex= graphDb.index().forNodes( "rhythmIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			originIndex= graphDb.index().forNodes( "originIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);
			artistIndex= graphDb.index().forNodes( "artistIndex"
					//MapUtil.stringMap( IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true" )  
					);

			int cnt=0;
			while ((line = br.readLine()) != null) {
				if(line.length() < 2) {
					continue;
				}
				String[]columns = line.split("\t");
				//String value = line.trim().toLowerCase();
				//				Node node = nodeIndex.get( key, value ).getSingle();
				//				if ( node == null )
				//				{
				//System.out.println(value);
				if(columns.length<10)
				{
					System.out.println("-------------Line omitted----------------");
					System.out.println(line);
					continue;
				}



				Node node = graphDb.createNode();

				node.setProperty( "DanceName", columns[2]);
				node.setProperty( "Link", columns[0]);
				node.setProperty( "Thumbnail", columns[4]);
				node.setProperty( "Description", columns[5]);
				node.setProperty("Type", "DanceNode");
				node.addLabel(LabelTypes.Dance);

				System.out.println("Dance :"+columns[2]+":created");
				Node temp;Relationship rel;
				//Category
				String[]arrcategory = columns[3].split(";");

				for (int i = 0; i < arrcategory.length; i++) {
					if (categoryIndex.get("CategoryName", arrcategory[i]).getSingle() == null) {
						temp = graphDb.createNode();
						temp.addLabel(LabelTypes.Category);
						temp.setProperty("CategoryName", arrcategory[i]);
						temp.setProperty("Type", "CategoryNode");
						categoryIndex.add(temp, "CategoryName", arrcategory[i]);
						//				System.out.println("------------Category "+arrcategory[i]+" created!!-------------------");
					}
					temp = categoryIndex.get("CategoryName", arrcategory[i])
							.getSingle();
					rel = node.createRelationshipTo(temp, RelTypes.hasCategory);
					rel.setProperty("propertyname", "hasCategory");
				}


				//Artists
				if(columns.length>10){
					String[]arrartists = columns[10].split(";");

					for (int i = 0; i < arrartists.length; i++) {
						if(arrartists[i].trim().length()<1)
							continue;
						if (artistIndex.get("ArtistName", arrartists[i]).getSingle() == null) {
							temp = graphDb.createNode();
							temp.addLabel(LabelTypes.Artist);
							temp.setProperty("ArtistName", arrartists[i]);
							temp.setProperty("Type", "ArtistNode");
							artistIndex.add(temp, "ArtistName", arrartists[i]);
							//		System.out.println("------------Artist "+arrartists[i]+" created!!-------------------");
						}
						else{
							cnt++;
							//				System.out.println( arrartists[i] +"-------- "+columns[2]);

						}
						temp = artistIndex.get("ArtistName", arrartists[i])
								.getSingle();
						rel = node.createRelationshipTo(temp, RelTypes.hasArtist);
						rel.setProperty("propertyname", "hasArtist");
					}
				}

				//Beat
				if(beatIndex.get("BeatName", columns[6]).getSingle()==null){
					temp = graphDb.createNode();
					temp.addLabel(LabelTypes.Beat);
					temp.setProperty("BeatName", columns[6]);
					temp.setProperty("Type", "BeatNode");
					beatIndex.add(temp,"BeatName", columns[6]);
				}
				temp=beatIndex.get("BeatName", columns[6]).getSingle();
				rel=node.createRelationshipTo(temp,RelTypes.hasBeat);
				rel.setProperty("propertyname", "hasBeat");

				//Tempo
				if(tempoIndex.get("TempoName", columns[7]).getSingle()==null){
					temp = graphDb.createNode();
					temp.addLabel(LabelTypes.Tempo);
					temp.setProperty("TempoName", columns[7]);
					temp.setProperty("Type", "TempoNode");
					tempoIndex.add(temp,"TempoName", columns[7]);
				}
				temp=tempoIndex.get("TempoName", columns[7]).getSingle();
				rel=node.createRelationshipTo(temp,RelTypes.hasTempo);
				rel.setProperty("propertyname", "hasTempo");

				//Rhythm	
				if(rhythmIndex.get("RhythmName", columns[8]).getSingle()==null){
					temp = graphDb.createNode();
					temp.addLabel(LabelTypes.Rhythm);
					temp.setProperty("RhythmName", columns[8]);
					temp.setProperty("Type", "RhythemNode");
					rhythmIndex.add(temp,"RhythmName", columns[8]);
				}
				temp=rhythmIndex.get("RhythmName", columns[8]).getSingle();
				rel=node.createRelationshipTo(temp,RelTypes.hasRhythm);
				rel.setProperty("propertyname", "hasRhythm");

				//Origin
				if(originIndex.get("OriginName", columns[9]).getSingle()==null){
					temp = graphDb.createNode();
					temp.addLabel(LabelTypes.Origin);
					temp.setProperty("OriginName", columns[9]);
					temp.setProperty("Type", "OriginNode");
					originIndex.add(temp,"OriginName", columns[9]);
				}
				temp=originIndex.get("OriginName", columns[9]).getSingle();
				rel=node.createRelationshipTo(temp,RelTypes.hasOrigin);
				rel.setProperty("propertyname", "hasOrigin");
				danceIndex.add( node, "DanceName", columns[2] );
				line="";


			}
			//	System.out.println(cnt+"Artists are common");
			tx.success();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tx.finish();
		}

	}




}