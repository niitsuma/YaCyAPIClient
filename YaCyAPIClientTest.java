package yacyapiclient;

import java.util.Collection;

import junit.framework.TestCase;

public class YaCyAPIClientTest extends TestCase {
	public YaCyAPIClientTest(String name) {
	    super(name);
	  }
	 
	  public void testYaCyAPIClient() {
		  System.out.println(
				  YaCyAPIClient.queryExactSentenceToNumFoundStr("This is a")
					);
			System.out.println(
					YaCyAPIClient.queryExactSentenceToNumFound("This is a")
					);
			System.out.println(
					  YaCyAPIClient.queryExactSentenceToNumFoundStr("%22This%20is%20a%22AND%22That%20is%22")
						);
			System.out.println(
					  YaCyAPIClient.queryExactSentenceToNumFoundStr("This * a pen")
						);
			System.out.println("---");
			//ArrayList<String> 
			Collection<String>
				summaryStringList =
					YaCyAPIClient.queryExactSentenceStartEndToSummaryStringList("This is a",1,4);
			for(String item: summaryStringList){System.out.println("element: " + item);}
			
			System.out.println("---");
			
			Collection<String>
			summaryStringList2 =
			YaCyAPIClient.queryExactSentenceEmulateWildcardToSummaryStringList("This is * pen",2,5);
			for(String item: summaryStringList2){System.out.println("element: " + item);}

			//YaCyAPIClient.queryExactSentenceEmulateWildcardToSummaryStringList("This is a",-1,-1);
	
			//System.out.println(summaryStringList.get(0));
			//System.out.println(summaryStringList.get(1));
	    //assertEquals("Error not number type", tn1, test1); 
	  }

}
