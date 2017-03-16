import java.io.StringReader;
import java.io.File;
import java.nio.file.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import java.lang.Math;

public class Searcher implements Comparator<Item> {
   
        public Searcher() {}
        static double latitude,longitude,width;
	    
	    static boolean basicSearch;

	
   

	    public static void main(String[] args) throws Exception {
		   String usage = "java Searcher";

		if(args.length == 1){
			basicSearch = true; 
			search(args[0], "indexes");
		}
		else if(args.length == 7){
			
			System.out.println("Test");
			if( args[1].equals("-x") && args[3].equals("-y") && args[5].equals("-w") ){

				double latitude,longitude,width;

				latitude = Double.valueOf(args[4]);
				longitude = Double.valueOf(args[2]);
				width = Double.valueOf(args[6]);
				
			//	spatialSearch(args[0], "indexes", longitude, latitude,width );
			 	spatial2(args[0], "indexes", longitude, latitude,width);
			 				 		
		}

           	}
}
        
        private static TopDocs search(String searchText, String p) {   
	    System.out.println("Running search(" + searchText + ")");
	    
	     Connection dbConn = null;
	     PreparedStatement statm = null; 
	     LinkedList<Item> list = new LinkedList<Item>();	     
	     double prev_scr = -2;		    

	    try 
	{   
	        Path path = Paths.get(p);
	        Directory directory = FSDirectory.open(path);       
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        QueryParser queryParser = new QueryParser("searchKey", new SimpleAnalyzer());  
	        Query query = queryParser.parse(searchText);
	        TopDocs topDocs = indexSearcher.search(query,10000);
		   
				
			
		       
		dbConn = DbManager.getConnection(true);
	        
	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) 
		{           
		    Document document = indexSearcher.doc(scoreDoc.doc);
		    
	   	  
		 String item_id = document.get("item_id");
		 String item_name = document.get("item_name");
		 double score = scoreDoc.score;
		 double current_price;
		


		String SQLquery = "SELECT current_price FROM auction WHERE item_id = " + item_id;		 
		statm = dbConn.prepareStatement(SQLquery);
		ResultSet result = statm.executeQuery();
		result.next();
		current_price = result.getInt("current_price");
			
		if(prev_scr == score)
		{
			
				Item item1 = new Item(item_id, item_name, score, current_price);			
       			list.add(item1);
		}
		
		else
		{
			if(prev_scr !=-2)
			{

				Collections.sort(list, new Searcher());
				for(int i=0; i<list.size(); i++)
				{
				    System.out.println(list.get(i));
				}
				list.clear();
				Item item1 = new Item(item_id, item_name, score, current_price);	
		        list.add(item1);	
			/*	System.out.println("item_id: " + document.get("item_id") 
				       + ", score: " + scoreDoc.score + " [" + document.get("item_name") +"]");	 */
				
			}
			else
			{
			    Item item1 = new Item(item_id, item_name, score, current_price);	
			    list.add(item1);	

			}
			
		}

		prev_scr = score;
	        }

		if(prev_scr !=-2)
		{
			Collections.sort(list, new Searcher());
			for(int i=0; i<list.size(); i++)
			{
			    System.out.println(list.get(i));
			}
			list.clear();
			
		}

	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        statm.close();
		dbConn.close();
	        return topDocs;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
        }
/*
 private static TopDocs spatialSearch(String searchText, String p, double longitude, double latitude, double width)
{
			
	  double R = 6371;  // earth radius in km

	  double radius = 50; // km

	 
	 double x1 = longitude - Math.toDegrees(radius/R/Math.cos(Math.toRadians(latitude)));

	 double x2 = longitude + Math.toDegrees(radius/R/Math.cos(Math.toRadians(latitude)));

	 double y1 = latitude + Math.toDegrees(radius/R);

	 double y2 = latitude - Math.toDegrees(radius/R);	

	     Connection dbConnection = null;
	     PreparedStatement statm = null; 
	     LinkedList<Item> list = new LinkedList<Item>();	     
	     double prev_scr = -2;		    
		 double score ;
		 double current_price;	
		 double dist;
	    try 
		{   
	        Path path = Paths.get(p);
	        Directory directory = FSDirectory.open(path);       
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        QueryParser queryParser = new QueryParser("searchKey", new SimpleAnalyzer());  
	        Query query = queryParser.parse(searchText);
	        TopDocs topDocs = indexSearcher.search(query,10000);
		   
						
			dbConnection = DbManager.getConnection(true);
	        
	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) 
			{           
		    Document document = indexSearcher.doc(scoreDoc.doc);
		    
	   	  
		 score = scoreDoc.score;
		 String itemID = document.get("item_id");
		 String item_name = document.get("item_name");
		
		
       private static TopDocs search(String searchText, String p) {   
	    System.out.println("Running search(" + searchText + ")");
	    
	     Connection dbConn = null;
	     PreparedStatement statm = null; 
	     LinkedList<Item> list = new LinkedList<Item>();	     
	     double prev_scr = -2;		    

	    try 
	{   
	        Path path = Paths.get(p);
	        Directory directory = FSDirectory.open(path);       
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        QueryParser queryParser = new QueryParser("searchKey", new SimpleAnalyzer());  
	        Query query = queryParser.parse(searchText);
	        TopDocs topDocs = indexSearcher.search(query,10000);
		   
				
			
		       
		dbConn = DbManager.getConnection(true);
	        
	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) 
		{           
		    Document document = indexSearcher.doc(scoreDoc.doc);
		    
	   	  
		 String item_id = document.get("item_id");
		 String item_name = document.get("item_name");
		 double score = scoreDoc.score;
		 double current_price;
		


		String SQLquery = "SELECT current_price FROM auction WHERE item_id = " + item_id;		 
		statm = dbConn.prepareStatement(SQLquery);
		ResultSet result = statm.executeQuery();
		result.next();
		current_price = result.getInt("current_price");
			
		if(prev_scr == score)
		{
			
				Item item1 = new Item(item_id, item_name, score, current_price);			
       			list.add(item1);
		}
		
		else
		{
			if(prev_scr !=-2)
			{

				Collections.sort(list, new Searcher());
				for(int i=0; i<list.size(); i++)
				{
				    System.out.println(list.get(i));
				}
				list.clear();
				System.out.println("item_id: " + document.get("item_id") 
				       + ", score: " + scoreDoc.score + " [" + document.get("item_name") +"]");	 
				
			}
			else
			{
			    Item item1 = new Item(item_id, item_name, score, current_price);	
			    list.add(item1);	

			}
			
		}

		prev_scr = score;	
			
	        }

		if(prev_scr !=-2)
		{
			Collections.sort(list, new Searcher());
			for(int i=0; i<list.size(); i++)
			{
			    System.out.println(list.get(i));
			}
			list.clear();
			
		}

	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        statm.close();
		dbConn.close();
	        return topDocs;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
        }


		
		/*String SQLquery = "SELECT current_price FROM auction WHERE itemID = " + itemID;		 
		

		 
		String selectFromPoly = "SELECT itemID " +
									"FROM  item_coordPoint " +
									"WHERE MBRContains" +
									"(GeomFromText('Polygon((" +
									String.valueOf(x1 + " " + latitude) + "," +
									String.valueOf(x2 + " " + latitude) + "," +
									String.valueOf(y1 + " " + longitude) + "," +
									String.valueOf(y2 + " " + longitude ) + "," +
									String.valueOf(x1 + " " + latitude) + "))')" +
									", coords) and itemID= ?";



	String calDinstance =  "SELECT X(coords),Y(coords)," +
			"((ACOS( SIN(X(coords)*PI()/180) *" +
			"SIN(?*PI()/180) + COS(X(coords)*PI()/180)* " +
			"COS(?*PI()/180) * COS((Y(coords)-(?" +
			"))*PI()/180))*180/PI())*60*1.1515) AS distance FROM  item_coordPoint WHERE itemID= ?";
	

		 
				PreparedStatement isWithinBounds = dbConnection.prepareStatement(selectFromPoly);
				isWithinBounds.setString(1,itemID);
				ResultSet searchSpatial = isWithinBounds.executeQuery();
				if(searchSpatial.next() != false)
				{
					isWithinBounds.close();

			
				statm = dbConnection.prepareStatement(SQLquery);
				statm.setString(1,itemID);

				ResultSet res = statm.executeQuery();
				res.next();

				current_price = res.getDouble("current_price");
				 
				 
				PreparedStatement retrieveDis = dbConnection.prepareStatement(calDinstance);
				retrieveDis.setString(1,String.valueOf(latitude));
				retrieveDis.setString(2,String.valueOf(latitude));
				retrieveDis.setString(3,String.valueOf(longitude));
				retrieveDis.setString(4,itemID);

				ResultSet distanceRes = retrieveDis.executeQuery();
			 
				 
					dist = distanceRes.getDouble("distance");
				 


				if(prev_scr == score)
				{
			
				Item item1 = new Item(itemID, item_name, score, current_price);			
       			list.add(item1);
				}
		
			else
			{
				if(prev_scr !=-2)
				{

					Collections.sort(list, new Searcher());
					for(int i=0; i<list.size(); i++)
					{
						System.out.println(list.get(i));
					}
					list.clear();
				
				}
				else
				{
					Item item1 = new Item(itemID, item_name, score, current_price);	
					list.add(item1);	

				}
			
		}

		prev_scr = score;	
			

			}
}


	if(prev_scr !=-2)
		{
			Collections.sort(list, new Searcher());
			for(int i=0; i<list.size(); i++)
			{
			    System.out.println(list.get(i));
			}
			list.clear();
			
		}

}

 
			
	 
	        statm.close();
			dbConnection.close();
	        return topDocs;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null ;
	    }

}*/


 private static TopDocs spatial2(String searchText, String p, double longtitude, double latitude, double width) {   
	   
 
	 
	 
	 double Alat = latitude - (width)/110.574;
	 double Alon = longtitude - (width)/(111.320*Math.cos(Math.toRadians(Alat)));
	 
	 double Blat = latitude + (width)/110.574;
	 double Blon = longtitude - (width)/(111.320*Math.cos(Math.toRadians(Blat)));
	 
	 double Clat = latitude + (width)/110.574;
	 double Clon = longtitude + (width)/(111.320*Math.cos(Math.toRadians(Clat)));
	 
	 double Dlat = latitude - (width)/110.574;
	 double Dlon = longtitude + (width)/(111.320*Math.cos(Math.toRadians(Dlat)));


	   System.out.println(Alat + " " + Alon);
         System.out.println(Blat + " " + Blon);
        System.out.println(Clat + " " + Clon);
        System.out.println(Dlat + " " + Dlon);
                


		 System.out.println("Running search(" + searchText + ")");
	    
	     Connection dbConnection = null;
	     PreparedStatement statm = null; 
	     LinkedList<Item> list = new LinkedList<Item>();	     
	     double prev_scr = -2;	
		 double dist = 0;	    
 
	    try 
		{   
	        Path path = Paths.get(p);
	        Directory directory = FSDirectory.open(path);       
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        QueryParser queryParser = new QueryParser("searchKey", new SimpleAnalyzer());  
	        Query query = queryParser.parse(searchText);
	        TopDocs topDocs = indexSearcher.search(query,10000);
		   
		       
		    dbConnection = DbManager.getConnection(true);


		   double noHits=0;
	        
	     System.out.println("Number of Hits: " + topDocs.totalHits);		
	    for (ScoreDoc scoreDoc : topDocs.scoreDocs) 
		{          
		        Document document = indexSearcher.doc(scoreDoc.doc);
		    
	   	 	 
		        String itemID = document.get("item_id");
		        String item_name = document.get("item_name");
		        double score = scoreDoc.score;
		        double current_price;
		
	
	           String SQLquery = "SELECT current_price FROM auction WHERE item_id = " + itemID;		 
		
			
		       String selectFromPoly = "SELECT item_id " +
									"FROM  item_coordinates_point " +
									"WHERE MBRContains" +
									"(GeomFromText('Polygon((" +
									String.valueOf(Alon + " " + Alat) + "," +
									String.valueOf(Blon + " " + Blat) + "," +
									String.valueOf(Clon + " " + Clat) + "," +
									String.valueOf(Dlon + " " + Dlat ) + "," +
									String.valueOf(Alon + " " + Alat) + "))')" +
									", coordinates) and item_id= ?";



	            String calDinstance =  "SELECT X(coordinates),Y(coordinates)," +
			        "((ACOS( SIN(X(coordinates)*PI()/180) *" +
			        "SIN(?*PI()/180) + COS(X(coordinates)*PI()/180)* " +
			        "COS(?*PI()/180) * COS((Y(coordinates)-(?" +
			        "))*PI()/180))*180/PI())*60*1.1515) AS distance FROM  item_coordinates_point WHERE item_id= ?";
	

		 	
				PreparedStatement isWithinBounds = dbConnection.prepareStatement(selectFromPoly);
				isWithinBounds.setString(1,itemID);
				ResultSet searchSpatial = isWithinBounds.executeQuery();
				
				if(!searchSpatial.next() )
				{
					//System.out.println("InsideContinue " + itemID);
					continue;

				}
			
				
				isWithinBounds.close();

					

				statm = dbConnection.prepareStatement(SQLquery);
				//statm.setString(1,itemID);
				//System.out.println("Exit");
			 	//System.exit(0);
				


				ResultSet res = statm.executeQuery();
				if(!res.next())
					{ continue;}

				current_price = res.getDouble("current_price");
				 
				
				PreparedStatement retrieveDis = dbConnection.prepareStatement(calDinstance);
				retrieveDis.setString(1,String.valueOf(latitude));
				retrieveDis.setString(2,String.valueOf(latitude));
				retrieveDis.setString(3,String.valueOf(longitude));
				retrieveDis.setString(4,itemID);

				ResultSet distanceRes = retrieveDis.executeQuery();
			 
		        if (distanceRes.next()== false)
		        {
		            continue;
		        }
				dist = distanceRes.getDouble("distance");
			    if ( (dist * 1.609344) > width)
			    {
			        continue;
			    }
			        
				noHits +=1;		

				if(prev_scr == score)
				{
			
				Item item1 = new Item(itemID, item_name, score, current_price);			
       			list.add(item1);
			}
		
			else
			{
				if(prev_scr !=-2)
				{

					Collections.sort(list, new Searcher());
					for(int i=0; i<list.size(); i++)
					{
						System.out.println(list.get(i));
					}
					list.clear();
				
				}
				else
				{
					Item item1 = new Item(itemID, item_name, score, current_price);	
					list.add(item1);	

				}
			
		

		prev_scr = score;	
			

			}
}


	if(prev_scr !=-2)
		{
			Collections.sort(list, new Searcher());
			for(int i=0; i<list.size(); i++)
			{
			    System.out.println(list.get(i));
			}
			list.clear();
			
		}


	        System.out.println("Number of Hits: " + noHits);
	        statm.close();
		    dbConnection.close();
	        return topDocs;
	    } catch (Exception e) {
	       // e.printStackTrace();
	        return null;
	    }
       }





  @Override
  public int compare(Item it1, Item it2)
    {

		if(basicSearch)
		{
		    if (it1.currentPrice > it2.currentPrice)
		        return 1;
		    else if (it1.currentPrice == it2.currentPrice)
		        return 0;
		    else
		    return -1;
		}

	 else
		{
			if( it1.distance > it2.distance)
				return 1;
			else if(it1.distance == it2.distance)
			{
				if(it1.currentPrice > it2.currentPrice)
					return 1;
				else if(it1.currentPrice == it2.currentPrice)
					return 0;
				else
					return -1;
			}
			else
				return -1;
		}

    } 

}//Class

class Item
    {
        String itemID;
        String itemName;
        double score;
        double currentPrice;
		double distance;

		 public Item(String itemid, String item_name, double score, double current_price, double distance)
        {
            this.itemID =itemid;
            this.itemName = item_name;
            this.score = score;
            this.currentPrice = current_price;
			this.distance = distance;
        } 


        public Item(String itemid, String item_name, double score, double current_price)
        {
            this.itemID =itemid;
            this.itemName = item_name;
            this.score = score;
            this.currentPrice = current_price;
        }

	public String toString()
	{
		String res = "item_id: " + itemID   + ", score: " + score + " [" + itemName +"]";	 

		return res;
	}

    }	
    
