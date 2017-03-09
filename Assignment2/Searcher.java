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

public class Searcher implements Comparator<Item> {
    public class Searcher {

        public Searcher() {}
        static double latitude,longitude,width;
	    static Point[] box = null;
	    static boolean basicSearch;
	    public static void main(String[] args) throws Exception {
		   String usage = "java Searcher";

		if(args.length == 1){
			//basicSearch = true;
			search(args[0], "indexes");
		}
		else if(args.length == 7){
			// check if we have 6 arguments
			if( args[1].equals("-x") && args[3].equals("-y") && args[5].equals("-w") ){

				latitude = Double.valueOf(args[4]);
				longitude = Double.valueOf(args[2]);
				width = Double.valueOf(args[6]);
				}
           
        
        private static TopDocs search(String searchText, String p) {   
	    System.out.println("Running search(" + searchText + ")");
	    Connection dbConn = null;
	    
	    try {   
	        Path path = Paths.get(p);
	        Directory directory = FSDirectory.open(path);       
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	        QueryParser queryParser = new QueryParser("line", new SimpleAnalyzer());  
	        Query query = queryParser.parse(searchText);
	        TopDocs topDocs = indexSearcher.search(query,10000);
	        
	        // create a connection to the database
			dbConn = DbManager.getConnection(true);
	        
	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
		    Document document = indexSearcher.doc(scoreDoc.doc);
		    System.out.println("item_id: " + document.get("doc_id") 
				       + ", score: " + scoreDoc.score + " [" + document.get("line") +"]");
		    String item_id = document.get("doc_id");
			String item_name = document.get("line");
			float score = scoreDoc.score;
			float current_price;
			
			String SQLquery = "SELECT current_price FROM auction WHERE item_id = "+item_id;
			// get the current price for the specific item
			State = dbConn.State(SQLquery);
			ResultSet result = State.executeQuery();
			result.next();
			current_price = result.getInt("current_price");
			
			// create an item and add to list
			Item item = new Item(item_id,item_name,score,current_price);
			itemList.add(item);
	        }
	        System.out.println("Number of Hits: " + topDocs.totalHits);
	        State.close();
			dbConn.close();
	        return topDocs;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
        }
    }
