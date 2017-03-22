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
	doc.add(new StringField("item_id", doc_id, Field.Store.YES));
	doc.add(new TextField("item_name", item_name,Field.Store.YES));
	
	String text = item_name + " " + categories + " " + description;
	doc.add(new TextField("searchKey", text, Field.Store.NO));
	 
	
	try 
	{ 	
		
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
	    indexWriter = new IndexWriter(directory, config);
	    indexWriter.deleteAll();




		dbConn = DbManager.getConnection(true);	

		String query = "SELECT "
					+ "item.item_id, item_name, description, GROUP_CONCAT(category_name SEPARATOR ' ') as category_name "
					+ "FROM item " + "INNER JOIN has_category ON item.item_id = has_category.item_id "
					+ "GROUP BY item.item_id";
	 

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

	  
	} catch (Exception e) 
	{
	    e.printStackTrace();
	}
    }
}
