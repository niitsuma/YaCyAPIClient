package YaCyAPIClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.ArrayList;

public class YaCyAPIClient {
	private static String YaCy_URL = "localhost:8090";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(
				queryExactSentenceToNumFoundStr("This is a")
				);
		System.out.println(
				queryExactSentenceToNumFound("This is a")
				);
		ArrayList<String> summaryStringList =
				queryExactSentenceStartEndToSummaryStringList("This is a",0,3);
		System.out.println(summaryStringList.get(0));
				
	}
	
	public static Document queryURLToDocument(URL url)
	{
		Document doc = null;
		try {
			//URL url = new URL(url_str);	
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			//urlConn.setInstanceFollowRedirects(false);
			//urlConn.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			urlConn.connect();
			
			//doc = getDocumet(urlConn.getInputStream());
			DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc=docbuilder.parse(urlConn.getInputStream());
			
			urlConn.disconnect();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		//} catch (MalformedURLException e) {
		//	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;		
	}
	public static String queryExactSentenceToNumFoundStr(String aQuery)
	{
		String numFoundStr = null;
		aQuery = aQuery.replaceAll(" ", "%20");
		Document doc = null;
		String url_str="http://" 
				+ YaCy_URL 
				+ "/solr/collection1/select?q=%22"
				+ aQuery 
				+ "%22&defType=edismax&start=0&rows=3";
		try {
			URL url = new URL(url_str);	
			doc=	queryURLToDocument(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		Element root = doc.getDocumentElement();
		//System.out.println("root name" + root.getTagName());
		NodeList resultList = root.getElementsByTagName("result");
		//System.out.println("num root node list：" + resultList.getLength());
		for (int i = 0; i < resultList.getLength(); i++) {
			Element element = (Element)resultList.item(i);
			//System.out.println(element.getAttribute("numFound"));
			numFoundStr=element.getAttribute("numFound");
		}
		return numFoundStr;
	}
	
	public static int queryExactSentenceToNumFound(String aQuery){
		return Integer.parseInt(queryExactSentenceToNumFoundStr(aQuery));
	}
	
	public static ArrayList<String> queryExactSentenceStartEndToSummaryStringList(String aQuery,int start, int rows)
	{
		ArrayList<String> summaryStringList = new ArrayList<String>();
		aQuery = aQuery.replaceAll(" ", "%20");
		Document doc = null;
		String url_str="http://" 
				+ YaCy_URL 
				+ "/solr/collection1/select?q=%22"
				+ aQuery 
				+ "%22&defType=edismax&start=" + start + "&rows=" + rows;
		try {
			URL url = new URL(url_str);	
			doc=	queryURLToDocument(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
		Element root = doc.getDocumentElement();
		//System.out.println("root name" + root.getTagName());
		NodeList resultList = root.getElementsByTagName("result");
		//System.out.println("num root node list：" + resultList.getLength());
		for (int i = 0; i < resultList.getLength(); i++) {
			Element resultElement = (Element)resultList.item(i);
			//System.out.println("resultElement name" + resultElement.getTagName());
		    //System.out.println(element.getAttribute("numFound"));
		    //numFoundStr=element.getAttribute("numFound");
		    NodeList docNodeList = resultElement.getElementsByTagName("doc");
		    //System.out.println("num doc：" + docNodeList.getLength());
		    for (int j = 0; j < docNodeList.getLength(); j++) {
		    	Element docElement = (Element)docNodeList.item(j);
		    	//System.out.println("docElement name" + docElement.getTagName());
		    	NodeList strNodeList = docElement.getElementsByTagName("str");
		    	//System.out.println("num str：" + strNodeList.getLength());
		    	for (int k	 = 0; k < strNodeList.getLength(); k++) {
		    		Element strElement = (Element)strNodeList.item(k);
		    		//System.out.println(strElement.getAttribute("name"));
		    		//System.out.println(getElementContent(strElement));
		    		//System.out.println(strElement.getNodeValue());
		    		if(strElement.getAttribute("name").equals("text_t"))	{
		    			//System.out.println(strElement.getAttribute("name"));
		    			//System.out.println(strElement.getFirstChild().getNodeValue());
		    			summaryStringList.add(strElement.getFirstChild().getNodeValue());
		    		}
		    		
		    	}
		    }
		}
		return summaryStringList;
	}
	
	
}
