package introsde.rest.adapterservices;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.ejb.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.*;
@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/")
public class Adapters {

	@GET
	@Path("hola")
	@Produces(MediaType.TEXT_PLAIN)
	public String hola(){
		return "Hola";
	}
	
	@GET
    @Path("instagram-pics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInstagramPics() throws Exception {
		
		String[] hashtags = {"pioneeradventures", "vacationwolf"};
		final String ACCESS_TOKEN = "466302969.5b9e1e6.0e108d1f4c864b348232c4d821643d89";
		
		int random_hashtag = 0 + (int)(Math.random()*(hashtags.length-1)); 
		String instagram_endpoint = "https://api.instagram.com/v1/tags/"
				+hashtags[random_hashtag]
				+"/media/recent?access_token="
				+ACCESS_TOKEN;
		
		String jsonResponse = "";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(instagram_endpoint);
		HttpResponse response = client.execute(request);
		
		BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		JSONObject o = new JSONObject(result.toString());
		
		if(response.getStatusLine().getStatusCode() == 200){
			
			jsonResponse += "{\"status\": \"OK\",";
			
			JSONArray arr = o.getJSONArray("data");
			int responseCount = arr.length();
			jsonResponse += "\"responseCount\": "+responseCount + ",";
			
			jsonResponse += "\"results\": [";
			
			for (int i = 0; i < arr.length(); i++){
				
				String type = arr.getJSONObject(i).getString("type");
        		
        		if(type.equals("image")){
        			
        			jsonResponse += "{";
        			String standard_resolution_url = arr.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url");
        			String thumbnail = arr.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail").getString("url");
        			jsonResponse += "\"url\": \""+standard_resolution_url+"\",";
        			jsonResponse += "\"thumbUrl\": \""+thumbnail+"\"";
        			
        			if(i == arr.length()-1){
        				jsonResponse += "}";
        			} else {
        				jsonResponse += "},";
        			}
        		}
			}
			
			jsonResponse += "]}";
			
			return Response.ok(jsonResponse).build();
		} else {
			jsonResponse += "{\"status\": \"ERROR\","
					+ "\"error\": \""+response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase()+"\"}";
			
			return Response.status(404).entity(jsonResponse).build();
		}
	}
	
	@GET
    @Path("motivation-quote")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMotivationQuote() throws Exception{
		
		String forismaticAPI = "http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en";
		
		String jsonResponse = "";
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(forismaticAPI);
		HttpResponse response = client.execute(request);
		
		BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		
		JSONObject o = new JSONObject(result.toString());
		
		if(response.getStatusLine().getStatusCode() == 200){
			jsonResponse += "{\"status\": \"OK\",";
			
			jsonResponse += "\"result\": {";
			
			String quoteText = o.getString("quoteText");
			String quoteAuthor = o.getString("quoteAuthor");
			
			jsonResponse += "\"quote\": \""+quoteText+"\",";
			
			if(quoteAuthor != null && !quoteAuthor.isEmpty()){
				jsonResponse += "\"author\": \""+quoteAuthor+"\"";
			} else {
				jsonResponse += "\"author\": \"Anonymous\"";
			}
			
			jsonResponse += "}}";
			
			return Response.ok(jsonResponse).build();
			
		} else {
			jsonResponse += "{\"status\": \"ERROR\","
					+ "\"error\": \""+response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase()+"\"}";
			return Response.status(404).entity(jsonResponse).build();
		}
	}
}








