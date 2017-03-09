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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer {
    public Indexer() {}

    public static IndexWriter indexWriter=null;

    public static void main(String args[]) 
	{
	String usage = "java Indexer";
	rebuildIndexes("indexes");
    }
    public static void insertDoc( String doc_id, String item_name, String description, String categories )
    {
	Document doc = new Document();
	doc.add(new StringField("doc_id", doc_id, Field.Store.YES));
	doc.add(new TextField("line", item_name,Field.Store.YES));
	try 
	{ 		
		String text = item_name + " " + categories + " " + description;
		doc.add(new TextField("content", text, Field.Store.NO));

		indexWriter.addDocument(doc); 

	} 
	catch (Exception e) 
	{ e.printStackTrace(); }

    }
    public static void rebuildIndexes(String indexPath) 
	{
	

	Connection dbConn = null;	
	PreparedStatement statm = null;

	try {


	    Path path = Paths.get(indexPath);
	    System.out.println("Indexing to directory '" + indexPath + "'...\n");
	    Directory directory = FSDirectory.open(path);
	    IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
	    //	    IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	    //IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
	    indexWriter = new IndexWriter(directory, config);
	    indexWriter.deleteAll();




		dbConn = DbManager.getConnection(true);	

		String query = "SELECT "
					+ "item.item_id, item_name, description, GROUP_CONCAT(category_name SEPARATOR ' ') as category_name "
					+ "FROM item " + "INNER JOIN has_category ON item.item_id = has_category.item_id "
					+ "GROUP BY item.item_id";

		/*String query = "SELECT "
					+ "item.item_id, item_name, description, GROUP_CONCAT(category_name SEPARATOR ' ') as category_name "
					+ "FROM item, has_category"					 
					+ "WHERE item.item_id = has_category.item_id "
					+ "GROUP BY item.item_id";*/

		statm = dbConn.prepareStatement(query);
		ResultSet rs = statm.executeQuery();



		while (rs.next()) 
			{

				int item_id = rs.getInt("item.item_id");
				String item_name = rs.getString("item_name");
				String description = rs.getString("description");
				String categories = rs.getString("category_name");
					
				//System.out.println(item_id);
				 
				insertDoc( String.valueOf(item_id), item_name, description, categories );  
				 
			}	     
	  		

	    indexWriter.close();
	    directory.close(); 
				 
		statm.close();
		dbConn.close();

	   
	 /*   insertDoc(i, "1", "The old night keeper keeps the keep in the town");
	    insertDoc(i, "2", "In the big old house in the big old gown.");
	    insertDoc(i, "3", "The house in the town had the big old keep");
	    insertDoc(i, "4", "Where the old night keeper never did sleep.");
	    insertDoc(i, "5", "The night keeper keeps the keep in the night");
	    insertDoc(i, "6", "And keeps in the dark and sleeps in the light.");
	    insertDoc(i, "7", "The house is the house.");
	    insertDoc(i, "8", "The-the");
	    insertDoc(i, "9", "the-the.");
	    insertDoc(i, "10", "the");
	    insertDoc(i, "11", "the the");
	    insertDoc(i, "12", "the the the");
	    insertDoc(i, "13", "the the the the");
	    //	    insertDoc(i, "3", "the-the-the.");
	    //	    insertDoc(i, "4", "the-thethe__the.");
	    //	    insertDoc(i, "5", "The__the");
	    //	    insertDoc(i, "6", "The-the");
	    //	    insertDoc(i, "14", "The a b c");
	    //	    insertDoc(i, "15", "The a b.");
	    //	    insertDoc(i, "16", "The a.");
	    //	    insertDoc(i, "17", "The the the the.");
	    //	    insertDoc(i, "18", "The the the the the the the the the.");*/
	  /*  i.close();
	    directory.close();*/
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
