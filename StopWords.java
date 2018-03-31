package stopWords;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;

public class StopWords {
    private String result;
    private final String INPUT_FILE="stop.txt";
    ArrayList<String>words ;
    
    //constructor to make some initialization
    public StopWords()
    {
        result="";
        words=new ArrayList<String>();
        init();
        
    }
    //create database connection
    private Iterator<Document> connect()
    {
        // Creating a Mongo client 
        MongoClient mongo = new MongoClient( "localhost" , 27017 ); 

        // Creating Credentials 
        MongoCredential credential; 
        credential = MongoCredential.createCredential("sampleUser", "test", 
        "password".toCharArray()); 

        // Accessing the database 
        MongoDatabase database = mongo.getDatabase("test");   
        // Retrieving a collection
        MongoCollection<Document> collection = database.getCollection("stopWords");
        Iterator<Document> it=collection.find().iterator();
        return it;
    }
    //this function read the stop words from a file and init an array
    private void init ()
    {
         Iterator<Document> it=connect();
        while(it.hasNext())
        {
            words.add(it.next().getString("word"));
        }
        
    }
    //this function test if the str is a stop word or not
    public boolean isStop(String str)
    {
        for(int i=0;i<words.size();i++)
            if((words.get(i)).equals(str)) return true;
          
        return false;
    }
    //this function searches for the stop words in the input string then removes it
    public String remove(String str)
    {
        StringBuilder builder = new StringBuilder("");
        String[] splited = str.split("\\s+");
        int len=splited.length;
        for(int i =0;i<len;i++)
        {
            if(!(isStop(splited[i])) )
            {
                if(i!=(len-1))
                builder.append(splited[i]+" "); 
            else
               builder.append(splited[i]); 
            }
           
        }
        result=builder.toString();
        return result;
    }

}
