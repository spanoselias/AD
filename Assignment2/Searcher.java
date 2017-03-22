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

public class Searcher implements Comparator<Item> 
{
   
        public Searcher() {}
      	    
	    static boolean basicSearch;	
   

	    public static void main(String[] args) throws Exception 
        {
		   String usage = "java Searcher";

        //It is check if it is the basic search that will be executed
		if(args.length == 1)
        {
			basicSearch = true; 
			search(args[0], "indexes");
		}
        
        //It is check if it is the spatial search that will be executed  
		else if(args.length == 7)
        {
			basicSearch = false;
			
			if( args[1].equals("-x") && args[3].equals("-y") && args[5].equals("-w") )
            {

				double latitude,longitude,width;
			
				latitude = Double.valueOf(args[2]);
				longitude = Double.valueOf(args[4]);
				width = Double.valueOf(args[6]);	

			 	spatialSearch(args[0], "indexes", longitude, latitude,width);			 				 		
		    }

         }
        }//Main
        
        private static TopDocs search(String searchText, String p) 
        {   

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
			
				Item item1 = new Item(item_id, item_name, score, current_price,true);			
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
				Item item1 = new Item(item_id, item_name, score, current_price,true);	
		        list.add(item1);			
				
			}
			else
			{
			    Item item1 = new Item(item_id, item_name, score, current_price,true);	
			    list.add(item1);	

			}
			
		}

		prev_scr = score;
	    
        }

          //We handle the last case. For example, if all the last item's score are equal, we sort 
         //them here
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
	        
	        return null;
	    }
}
 
 private static TopDocs spatialSearch(String searchText, String p, double longtitude, double latitude, double width) 
 {	   
   
	 
        BoxPoints newBoxPoint = new BoxPoints();
        newBoxPoint.calculateBoxPoints(latitude,longtitude,width);

	                  
		
	    System.out.println("Running search(" + searchText + ")");
	    
	     Connection dbConnection = null;
	     PreparedStatement statm = null; 
	     LinkedList<Item> list = new LinkedList<Item>();	     
	     double prev_scr = -2;	
		 float dist = 0;	    
 		 int noHits=0;
	    
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
		    
	   	 	 
		        String itemID = document.get("item_id");
		        String item_name = document.get("item_name");
		        double score = scoreDoc.score;
		        double current_price;
				
                //The below statement is created the sql query for polygon		  
				String selectPoly = getDinstanceQuery(newBoxPoint.lonLeftDown, newBoxPoint.latLeftDown, newBoxPoint.lonLeftUp, newBoxPoint.latLeftUp, newBoxPoint.lonRightUp, newBoxPoint.latRightUp, newBoxPoint.lonRightDown, newBoxPoint.latRightUp );
			
            	PreparedStatement isWithinBounds = dbConnection.prepareStatement(selectPoly);
				isWithinBounds.setString(1,itemID);
				ResultSet searchSpatial = isWithinBounds.executeQuery();
				
				if(!searchSpatial.next() )
				{					 
					continue;

				}			
				
				isWithinBounds.close();


                //This query is executed in order to ibtain the current_price for the 
                //specific item id
				statm = dbConnection.prepareStatement( getQueryPrice(itemID));
			
				ResultSet res = statm.executeQuery();
				if(!res.next())
					{ continue;}

                //It is retrieve the current price from the results
				current_price = res.getDouble("current_price");
				 
			               
                ResultSet distanceRes =   getCalDinstace(dbConnection, itemID, longtitude,  latitude );
			  
		        if (distanceRes.next()== false)
		        {
		            continue;
		        }
			   
				dist = distanceRes.getFloat("distance");
			    if ( (dist)  > width)
			    {
			        continue;
			    }
			        
		
            //While the score is equal, we insert the item in the array list in order to sort it
            //with decreasing order for the distance, and in case where both are equal, we sort it 
            //with price		
			if(prev_scr == score)
			{
		
			    Item item1 = new Item(itemID, item_name, score, current_price,dist);			
       			list.add(item1);
			}
		
			else
			{

                //In case where we found a different score, we sort the previous same scores.
				if(prev_scr !=-2)
				{

					Collections.sort(list, new Searcher());
					for(int i=0; i<list.size(); i++)
					{
						System.out.println(list.get(i));
					}
					list.clear();
					Item item1 = new Item(itemID, item_name, score, current_price,dist);	
					list.add(item1);
				
				}
				else
				{
					Item item1 = new Item(itemID, item_name, score, current_price,dist);	
					list.add(item1);	

				}
			
		}
		noHits +=1;		

		prev_scr = score;		
		
}



    //We handle the last case. For example, if all the last item's score are equal, we sort 
   //them here
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


public static String getQueryPrice(String itemidIn)
{
    String priceQuery = "SELECT current_price FROM auction WHERE item_id = " + itemidIn;
    
    return priceQuery;

}

public static String getDinstanceQuery(double lonLeftDown,double latLeftDown, double lonLeftUp, double latLeftUp, double lonRightUp, double latRightUp, double lonRightDown, double latRightDown )
{
    String selectFromPoly = "SELECT item_id " +
									"FROM  item_coordPoint " +
									"WHERE MBRContains" +
									"(GeomFromText('Polygon((" +
									String.valueOf(lonLeftDown   + " " + latLeftDown) + "," +
									String.valueOf(lonRightDown + " " + latRightDown) + "," +
									String.valueOf(lonRightUp + " " + latRightUp) + "," +
									String.valueOf(lonLeftUp + " " + latLeftUp ) + "," +
									String.valueOf(lonLeftDown + " " + latLeftDown) + "))')" +
									", coords) and item_id= ?";			
    
    return selectFromPoly;

}


//This sql query compare two points and return the difference between the two points in km
public static ResultSet getCalDinstace(Connection newConn, String itemidIn, double longtitude, double latitude )
{

     String calDinstance = "SELECT X(coords),Y(coords)," +
			        "((ACOS( SIN(X(coords)*PI()/180) *" +
			        "SIN(?*PI()/180) + COS(X(coords)*PI()/180)* " +
			        "COS(?*PI()/180) * COS((Y(coords)-(?" +
			        "))*PI()/180))*180/PI())*60*1.1515 / 0.62145) AS distance FROM item_coordPoint WHERE item_id= ?";
   


     ResultSet distanceRes=null;
     try 
		{ 

				PreparedStatement retrieveDisIn = newConn.prepareStatement(calDinstance);
				retrieveDisIn.setString(1, String.valueOf(longtitude) );
				retrieveDisIn.setString(2, String.valueOf(longtitude) );
				retrieveDisIn.setString(3, String.valueOf(latitude ) );
				retrieveDisIn.setString(4,itemidIn);

			    distanceRes = retrieveDisIn.executeQuery();
        }        
	         
	     catch (Exception e) 
        {
	       
	        return null;
	    }
              
        return distanceRes;
}

}//Class

class Item
    {
        String itemID;
        String itemName;
        double score;
        double currentPrice;
        boolean isBasic = false;          
  
	    double distance;

		 public Item(String itemid, String item_name, double score, double current_price, double distance)
        {
            this.itemID =itemid;
            this.itemName = item_name;
            this.score = score;
            this.currentPrice = current_price;
			this.distance = distance;
        } 


        public Item(String itemid, String item_name, double score, double current_price, boolean isBasicIn)
        {
            this.itemID =itemid;
            this.itemName = item_name;
            this.score = score;
            this.currentPrice = current_price;
            this.isBasic=isBasicIn;
        }

    	        
    public String toString()
	{
		String res ="";

        if(isBasic)
		{             
			 res =  itemID   +  ", " + itemName +" " + ", score: " + score + ", price: " + currentPrice;	
        }
        else
        {

		   String roundDis = String.format("%.3f", distance); 
           res =  itemID   +  ", " + itemName +" " + ", score: " + score + ", dist: " + roundDis + ", price: " + currentPrice;	
        }    

		return res;
	 }

  }	

class BoxPoints
{
     double latLeftDown = 0.0 ;
	 double lonLeftDown = 0.0 ;
	 
	 double latLeftUp = 0.0 ;
	 double lonLeftUp = 0.0 ;
	 
	 double latRightUp = 0.0 ;
	 double lonRightUp=  0.0 ;
	 
	 double latRightDown = 0.0 ;
	 double lonRightDown = 0.0 ;

    //**
    public void calculateBoxPoints(double latitude,double longtitude, double width )
    {
    
         this.latLeftDown = latitude - (width)/110.574;
	     this.lonLeftDown = longtitude - (width)/(111.320*Math.cos(Math.toRadians(latLeftDown)));
	 
	     this.latLeftUp = latitude + (width)/110.574;
	     this.lonLeftUp = longtitude - (width)/(111.320*Math.cos(Math.toRadians(latLeftUp)));
	 
	     this.latRightUp = latitude + (width)/110.574;
	     this.lonRightUp = longtitude + (width)/(111.320*Math.cos(Math.toRadians(latRightUp)));
	 
	     this.latRightDown = latitude - (width)/110.574;
	     this.lonRightDown = longtitude + (width)/(111.320*Math.cos(Math.toRadians(latRightDown)));
	     
	     if (this.latLeftDown < -90.0){
	        this.latLeftDown = -90.0;
	     }
	     if (this.latLeftUp > 90.0){
	        this.latLeftUp = 90.0;
	     }
	     if (this.latRightUp > 90.0){
	        this.latRightUp = 90.0;
	     }
	     if (this.latRightDown < -90.0){
	        this.latRightDown = -90.0;
	     }
	     
	     if (this.lonLeftDown < -180.0){
	        this.lonLeftDown = -180.0;
	     }
	     if (this.lonLeftUp < -180.0){
	        this.lonLeftUp = -180.0;
	     }
	     if (this.lonRightUp > 180.0){
	        this.lonRightUp = 180.0;
	     }
	     if (this.lonRightDown > 180.0){
	        this.lonRightDown = 180.0;
	     }

    }

}

    
