package edu.dance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.spi.http.HttpContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.graphdb.Node;

import scala.io.BufferedSource;

@Path("controller")
public class Controller {
	
	private static final SearchNodes searchObj = new SearchNodes(GraphUtil.getGraphConnection());
	private static final Queries queryObj = new Queries(GraphUtil.getGraphConnection());

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    @GET
    @Path("/searchDance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDance(
    		@Context HttpHeaders headers,
    		@QueryParam("term") String term) {
    	
    	try {
    		System.out.println("term : " + term);
    		ArrayList<Node> nodes = searchObj.searchNodesByTerm("danceIndex", "DanceName", term.trim());
    		JSONArray res = new JSONArray();
    		JSONObject obj;
    		for(Node n : nodes) {
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("DanceName"));
    			res.put(obj);
    		}
    		System.out.println("Got dances : " + res.length());
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @POST
    @Path("/searchDance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchDancePost(
    		@Context HttpHeaders headers,
    		@FormParam("term") String term) {
    	
    	try {
    		System.out.println("term : " + term);
    		Node n = searchObj.searchNodes("danceIndex", "DanceName", term.trim());
    		JSONArray res = new JSONArray();
    		JSONObject obj;
    		
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("DanceName"));
    			obj.put("desc",n.getProperty("Description"));
    			obj.put("link",n.getProperty("Link"));
    			obj.put("thumbnail",n.getProperty("Thumbnail"));
    			res.put(obj);
    		
    		System.out.println("Got dances : " + res.length());
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    
    
    @POST
    @Path("/findAllMoves")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllMoves(
    		@Context HttpHeaders headers,
    		@FormParam("term") String term) {
    	try {
    		JSONObject res = queryObj.findAllMoves(term);
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @POST
    @Path("/findCentralityDances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCentralityDances(
    		@Context HttpHeaders headers,
    		@FormParam("term1") String term1,@FormParam("term2") String term2) {
    	try {
    		JSONObject res = queryObj.findCentrality_dance(term1,term2);
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    @POST
    @Path("/findCentralityArtists")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCentralityArtists(
    		@Context HttpHeaders headers,
    		@FormParam("term1") String term1,@FormParam("term2") String term2) {
    	try {
    		JSONObject res = queryObj.findCentrality_artists(term1,term2);
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
   
    @POST
    @Path("/findCentralityMoves")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCentralityMoves(
    		@Context HttpHeaders headers,
    		@FormParam("term1") String term1,@FormParam("term2") String term2) {
    	try {
    		JSONObject res = queryObj.findCentrality_moves(term1,term2);
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
   
    @POST
    @Path("/findCentralityParentMoves")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCentralityParentMoves(
    		@Context HttpHeaders headers,
    		@FormParam("term1") String term1,@FormParam("term2") String term2) {
    	try {
    		JSONObject res = queryObj.findCentrality_ParentMoves(term1,term2);
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @POST
    @Path("/findNodesUnderACriteria")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findNodesUnderACriteria(
    		@Context HttpHeaders headers,
    		@FormParam("term1") String term1,@FormParam("term2") String term2) {
    	try {
    		System.out.println("term1="+term1+"term2="+term2);
    		JSONObject obj = queryObj.findNodesUnderACriteria(term1, term2);
    		System.out.println("In controller");
    		if(obj!=null){
    //			System.out.println(obj.toString());
    		}
    		return Response.status(200).entity(obj.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @POST
    @Path("/findMoveVideos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMoveVideos(
    		@Context HttpHeaders headers,
    		@FormParam("term") String term) {
    	try {
    		String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&videoEmbeddable=true&type=video&safeSearch=moderate&order=rating&key=AIzaSyD0iLxYOM_x7_0uO3jLXQF2_OO1e95cuZg&q="
    				+ URLEncoder.encode(term);
    		URL urlobj = new URL(url);
    		StringBuilder jsonString = new StringBuilder();
    		try {
    			URLConnection conn = urlobj.openConnection();
    			BufferedReader buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    			String line;
    			while((line = buf.readLine()) != null) {
    				jsonString.append(line);
    			}
    			
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		return Response.status(200).entity(jsonString.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @POST
    @Path("/findDanceData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findDanceData(
    		@Context HttpHeaders headers,
    		@FormParam("term") String term) {
    	try {
    		JSONObject obj = new JSONObject();
    		return Response.status(200).entity(obj.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
    @GET
    @Path("/findMoveVideos")
    @Produces(MediaType.TEXT_PLAIN)
    public Response findMoveVideosGet(
    		@Context HttpHeaders headers,
    		@QueryParam("term") String term) {
    	return Response.status(200).entity("Test").build();
    }
    
    @POST
	@Path("/closestdance")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findClosestDance(
			@Context HttpHeaders headers,
			@DefaultValue("") @FormParam("term") String term) {
		try {
			JSONArray obj = queryObj.findNearestDance(term);
			return Response.status(200).entity(obj.toString()).build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
	}
    
    @GET
	@Path("/closestdance")
	@Produces(MediaType.TEXT_PLAIN)
	public Response findClosestDanceGET(
			@Context HttpHeaders headers,
			@DefaultValue("") @QueryParam("term") String term) {
		return Response.status(200).entity("This method is supported for POST request").build();
	}
    
    @GET
    @Path("/searchAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchAll(
    		@Context HttpHeaders headers,
    		@QueryParam("term") String term) {
    	
    	try {
    		System.out.println("term : " + term);
    		
    		// for dance
    		ArrayList<Node> nodes = searchObj.searchNodesByTerm("danceIndex", "DanceName", term.trim());
    		JSONArray res = new JSONArray();
    		JSONObject obj;
    		for(Node n : nodes) {
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("DanceName"));
    			obj.put("label", "[Dance] " + n.getProperty("DanceName"));
    			obj.put("type", "dance");
    			res.put(obj);
    		}
    		
    		// for moves
    		nodes = searchObj.searchNodesByTerm("moveIndex", "MoveName", term.trim());
    		for(Node n : nodes) {
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("MoveName"));
    			obj.put("label", "[Move] " + n.getProperty("MoveName"));
    			obj.put("type", "move");
    			res.put(obj);
    		}
    		System.out.println("Got moves : " + res.length());
    		
    		// for artist
    		nodes = searchObj.searchNodesByTerm("artistIndex", "ArtistName", term.trim());
    		for(Node n : nodes) {
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("ArtistName"));
    			obj.put("label", "[Artist] " + n.getProperty("ArtistName"));
    			obj.put("type", "artist");
    			res.put(obj);
    		}
    		System.out.println("Got artist : " + res.length());
    		
    		//for parent moves
    		nodes = searchObj.searchNodesByTerm("masteMoveIndex", "MasterMoveName", term.trim());
    		for(Node n : nodes) {
    			obj = new JSONObject();
    			obj.put("id", n.getId());
    			obj.put("name", n.getProperty("MasterMoveName"));
    			obj.put("label", "[Master Move] " + n.getProperty("MasterMoveName"));
    			obj.put("type", "mm");
    			res.put(obj);
    		}
    		System.out.println("Got mm : " + res.length());
    		
    		return Response.status(200).entity(res.toString()).build();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return Response.status(500).entity(new JSONObject().put("Error", "Query term").toString()).build();
    }
    
    
}
