package wikiextraction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class WikiExtraction {
public static void main(String[] args) throws IOException{
		
		Set<String> categorie = getCategories("Nanoindentation");
		for(String s : categorie){
			System.out.println(s);
		}
		
		Set<String> links = getLinks("nanoindentation");
		for(String s : links){
			System.out.println(s);
		}
		
		Set<String> linkshere = getLinksHere("nanoindentation");
		for(String s : linkshere){
			System.out.println(s);
		}

		Set<String> categoryMembers = getCategoryMembers("hardness_tests");
		for(String s : categoryMembers){
			System.out.println(s);
		}
		
		String extract = getExtract("nanoindentation");
		System.out.println(extract);
	}
	
	/*
	con il metodo getJsonElement restituisco l'elemento JSON privato dell'identificativo.
	Posso quindi poi interrogare ed estrarre ulteriori informazioni evitando di elaborarlo ogni volta per info diverse
	*/
	private static JsonElement getJsonElement(String action, String title, String page, String req) throws IOException{
		URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&"+title+"="+page+"&"+action+"="+req+"&format=json");
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
        JsonElement pages = jsonElement.getAsJsonObject().get("query").getAsJsonObject().get("pages");
        Set<Entry<String, JsonElement>> entrySet = pages.getAsJsonObject().entrySet();
        JsonElement yourDesiredElement = null;
        for(Map.Entry<String,JsonElement> entry : entrySet){
            yourDesiredElement = entry.getValue();
        } 
		return yourDesiredElement;
	}
	
	private static String getExtract(String page) throws IOException{
		URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exintro=&explaintext=&titles="+page);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
        JsonElement pages = jsonElement.getAsJsonObject().get("query").getAsJsonObject().get("pages");
		JsonElement elem = getJsonElement("prop", "titles",page+"&exintro=&explaintext=", "extracts");
		String extract = elem.getAsJsonObject().get("extract").toString();
		extract = extract.substring(1, extract.length()-5);
		return extract;
	}
	private static Set<String> getCategories(String page) throws IOException{
		JsonElement elem = getJsonElement("prop", "titles", page, "categories");
		HashSet<String> result = new HashSet<String>();
		for(JsonElement cat : elem.getAsJsonObject().get("categories").getAsJsonArray()){
			//scorro gli elementi categoria nell'array
			String category = cat.getAsJsonObject().get("title").toString();
			category = category.substring(10, category.length()-1);
			result.add(category);
		}
		return result;
	}
	private static Set<String> getLinks(String page) throws IOException{
		JsonElement elem = getJsonElement("prop", "titles",page, "links");
		HashSet<String> result = new HashSet<String>();
		for(JsonElement l : elem.getAsJsonObject().get("links").getAsJsonArray()){
			String link = l.getAsJsonObject().get("title").toString();
			link = link.substring(1, link.length()-1);
			result.add(link);
		}
		return result;
	}
	
	private static Set<String> getLinksHere(String page) throws IOException{
		JsonElement elem = getJsonElement("prop", "titles",page, "linkshere");
		HashSet<String> result = new HashSet<String>();
		for(JsonElement l : elem.getAsJsonObject().get("linkshere").getAsJsonArray()){
			String link = l.getAsJsonObject().get("title").toString();
			link = link.substring(1, link.length()-1);
			result.add(link);
			System.out.println(link);
		}
		return result;
	}
	private static Set<String> getCategoryMembers(String page) throws IOException{
		URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&list=categorymembers&format=json&cmtitle=Category:"+page);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        HashSet<String> result = new HashSet<String>();
        JsonElement jsonElement = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
        JsonElement categorymembers = jsonElement.getAsJsonObject().get("query").getAsJsonObject().get("categorymembers");
        JsonArray catMembArray = categorymembers.getAsJsonArray();
        for(int i = 0; i<catMembArray.size(); i++){
        	String category = (catMembArray.get(i).getAsJsonObject().get("title").toString());
        	category = category.substring(1, category.length()-1);
        	result.add(category);
        }
        return result;
	}
}
