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

public class YaCyAPIClient {
	private static String YaCy_URL = "localhost:8091";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(
				queryExactSentenceNumFoundStr("This is a")
				);
		System.out.println(
				queryExactSentenceNumFound("This is a")
				);
	}
	public static String queryExactSentenceNumFoundStr(String aQuery)

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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element root = doc.getDocumentElement();
		//System.out.println("root name" + root.getTagName());
		NodeList nodeList = root.getElementsByTagName("result");
		//System.out.println("num root node list：" + nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);
			//System.out.println(element.getAttribute("numFound"));
			numFoundStr=element.getAttribute("numFound");
		}
		return numFoundStr;
		
	}
	
	public static int queryExactSentenceNumFound(String aQuery){
		return Integer.parseInt(queryExactSentenceNumFoundStr(aQuery));
	}
}
