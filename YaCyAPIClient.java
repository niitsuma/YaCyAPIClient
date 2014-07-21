package yacyapiclient;
//package YaCyAPIClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
//import com.google.guava:guava;
//import java.util.Collections;
//import com.google.common.collect.
//import com.google.common.io.


public class YaCyAPIClient {
	private static String YaCy_URL = "localhost:8090";
	private static int queryRowsEmulateWildcard=100;
	private static String wildcardSeparator=
		"\\*"
		//" \\* "
		;
	
	public static String queryStrToURLString(String queryActual,int start,int rows){
		//String queryActual=query;
		//queryActual = queryActual.replaceAll(" ", "%20");
		//queryActual = queryActual.replaceAll("\"", "%22");
		String urlStr="http://" 
						+ YaCy_URL 
						+ "/solr/collection1/select?q="
						+ queryActual
						+ "&defType=edismax";
		if(start > -1){urlStr = urlStr	+ "&start=" + start;}
		if(rows > 0  ){urlStr = urlStr 	+ "&rows=" + rows;}
						//+ "&start=" + start + "&rows=" + rows;
		return urlStr;}
	
	public static Document queryURLToDocument(URL url){
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
		return doc;}
	
	public static Document queryURLStrToDocument(String urlStr){
		System.out.println(urlStr);
		Document doc = null;
		try {
			URL url = new URL(urlStr);	
			doc=	queryURLToDocument(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();}
		return doc;}
	
	public static String queryDocToNumFoundStr(Document doc){
		String numFoundStr = null;
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
	
	public static Collection<String> queryDocToSummaryStringList(Document doc){
		ArrayList<String> summaryStringList = new ArrayList<String>();
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

	public static String queryWildcardSplitedToQueryActual(String[] querySplited){
		StringBuilder builder = new StringBuilder();			
		for(String str : querySplited) {
			//System.out.println(str);
			//builder.append("\"");
			builder.append("%22");
			builder.append(str.replaceAll(" ", "%20"));
			builder.append("%22");
			builder.append("%20AND%20");
		}
		String queryActual = builder.substring(0, builder.length() - 9);
		//System.out.println(queryActual);
		return queryActual;	
	}
	
	public static Collection<String> queryExactSentenceEmulateWildcardToSummaryStringList(String query,int start,int rows){
		//System.out.println(query);
		String[] querySplited = query.split(wildcardSeparator);
		//System.out.println(querySplited.length);
		//for(String str : queryAry) {System.out.println(str);}		
		//if(queryAry.length > 1 ){
			String pat=query.replaceAll("\\*", ".*");
			//System.out.println(pat);
			String queryActual = queryWildcardSplitedToQueryActual(querySplited);
			int numFound=Integer.parseInt(queryDocToNumFoundStr(queryURLStrToDocument(queryStrToURLString(queryActual,-1,-1))));
			if(start > numFound -2 || numFound == 0  ) {return new ArrayList<String>();}
			rows= Math.min(rows ,  numFound - start -2 );
			//System.out.println(queryActual);
			//ArrayList<String> 
			Collection<String> summaryStringList0 = 
				queryDocToSummaryStringList(
						queryURLStrToDocument(queryStrToURLString(queryActual,start,rows)));
			//System.out.println(summaryStringList0.size());
			Collection<String> summaryStringList =
					Collections2.filter(summaryStringList0,Predicates.containsPattern(pat));
			//System.out.println(summaryStringList.size());
			int start1=start+rows;
			while(start == 0  &&  summaryStringList.size() < rows && start1 < numFound ){ 
				Collection<String> summaryStringList1 = 
						queryDocToSummaryStringList(
								queryURLStrToDocument(queryStrToURLString(queryActual,start1,rows)));
				summaryStringList.addAll(				
						Collections2.filter(summaryStringList1,Predicates.containsPattern(pat)));
				start1=start1+rows; 
			}

			return summaryStringList;
			//for(String item: summaryStringList){System.out.println("element: " + item);}
		//}
	}
	
	public static String queryExactSentenceToNumFoundStr(String query){
		String[] querySplited = query.split(wildcardSeparator);
		//System.out.println(queryAry.length);
		//for(String str : queryAry) {System.out.println(str);}
		if(querySplited.length > 1 ){
			int numFund0=Integer.parseInt(
				queryDocToNumFoundStr(
					queryURLStrToDocument(
						queryStrToURLString(
							queryWildcardSplitedToQueryActual(querySplited)
					,-1,-1))));
			if(numFund0 == 0) {return String.valueOf(0);}
			int rows=Math.min(numFund0,queryRowsEmulateWildcard);
			Collection<String> summaryStringList = queryExactSentenceEmulateWildcardToSummaryStringList(query,0,rows);			
			int numFund1=summaryStringList.size();
			return String.valueOf(numFund0*numFund1/rows);//Approximated ratio
		/*
		}else if(Pattern.compile("%22").matcher(query).find()){
			System.out.println("aleadyReplacedQuery : "+query);
		    return queryDocToNumFoundStr(
			     queryURLStrToDocument(
			       queryStrToURLString(query,-1,-1)));
		*/
		}else{		
		    //String numFoundStr = null;
		    query = query.replaceAll(" ", "%20");
		    String queryActual=null;
		    if(Pattern.compile("%22").matcher(query).find()){
		    	queryActual=query;
		    }else{
		    	queryActual="%22" + query + "%22";
		    }
		    return queryDocToNumFoundStr(
			     queryURLStrToDocument(queryStrToURLString(queryActual,-1,-1)));
		}	}
	public static int queryExactSentenceToNumFound(String aQuery){
		return Integer.parseInt(queryExactSentenceToNumFoundStr(aQuery));}
	
	public static Collection<String> queryExactSentenceStartEndToSummaryStringList(String query,int start, int rows){
		String[] querySplited = query.split(wildcardSeparator);
		if(querySplited.length > 1 ){
			return queryExactSentenceEmulateWildcardToSummaryStringList(query,start,rows);			
		}else{
			String queryActual=null;
			if(Pattern.compile("%22").matcher(query).find()){
				queryActual=query;
			}else{
				queryActual="%22" +   query.replaceAll(" ", "%20") + "%22" ;		
			}
			Collection<String> summaryStringList 
			//= queryDocToSummaryStringList(doc);
			= queryDocToSummaryStringList(
				queryURLStrToDocument(queryStrToURLString(queryActual,start,rows)));
			return summaryStringList;
		}
	}
	
	
}
