package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MoviesFiltration
{
    public static String SEPARATOR_REPLACEMENT;
    public static String SEPARATOR_SPLIT;
    public static String EMPTY_STRING;
    public static String DIRECTORY_NAME;
    public static String MONGO_USER_NAME;
    public static String MONGO_PASSWORD;
    public static String MONGO_HOST_NAME;
    public static String DATABASE_NAME;
    public static String COLLECTION_NAME;
    public static String MONGO_COLLECTION_URI;

    static
    {
        SEPARATOR_REPLACEMENT= "}@@@@@@@@@{";
        SEPARATOR_SPLIT = "@@@@@@@@@";
        EMPTY_STRING = "";
        DIRECTORY_NAME = "./moviesData/";
        MONGO_USER_NAME = "root";
        MONGO_PASSWORD = "root";
        MONGO_HOST_NAME = "cluster0.8oqjs.mongodb.net";
        DATABASE_NAME = "myMongoMovie";
        COLLECTION_NAME = "mongoMovie";
        MONGO_COLLECTION_URI = "mongodb+srv://" + MONGO_USER_NAME + ":" + MONGO_PASSWORD + "@" + MONGO_HOST_NAME + "/" + DATABASE_NAME + "?retryWrites=true&w=majority";
    }

    private File[] readAllFileNames()
    {
        File movieDataFolder = new File(DIRECTORY_NAME);
        return movieDataFolder.listFiles();
    }

    private String filterArticleContent(String articleContent)
    {
        String filteredArticleContent;
        String emojisFilter = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
        String urlToImageFilter = "(\"urlToImage\":(\"http.*?\"|null|\"null\"|\"\"),)";
        String urlFilter = "(\"url\":(\"http.*?\"|null|\"null\"|\"\"),)";
        String authorFilter = "(\"author\":(\"http.*?\"|null|\"null\"|\"\"),)";
        String idFilter = "(,\"id\":(null|\"null\"|\"\"))";
        String generalFilter = "(\\\\[ntr])|( )|(<[^>]*>)";
        String separatorFilter = "},{";

        filteredArticleContent = articleContent.replaceAll(emojisFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(urlToImageFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(urlFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(authorFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(urlFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(idFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.replaceAll(generalFilter, EMPTY_STRING);
        filteredArticleContent = filteredArticleContent.substring(1, filteredArticleContent.length() - 1);
        filteredArticleContent = filteredArticleContent.replace(separatorFilter, SEPARATOR_REPLACEMENT);
        return filteredArticleContent;
    }

    public void readAndFilterArticles(File[] allMoviesFiles, List<Document> mongoMoviesDocuments)
    {
        int totalArticlesRead = 0;
        for(File movieFile : allMoviesFiles)
        {
            try
            {
                String movieFileContent = Files.readString(Paths.get(movieFile.getPath())).trim();
                String[] filteredArticlesList = filterArticleContent(movieFileContent).split(SEPARATOR_SPLIT);
                for(String article : filteredArticlesList)
                {
                    System.out.println(article);
                    Document articleDocument = Document.parse(article);
                    mongoMoviesDocuments.add(articleDocument);
                    totalArticlesRead++;
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Total articles read and filtered - " + totalArticlesRead);
    }

    public void filterMovieData()
    {
        File[] allMoviesFiles = readAllFileNames();
        if(allMoviesFiles != null && allMoviesFiles.length != 0)
        {
            List<Document> mongoMoviesDocuments = new ArrayList<>();
            System.out.println("Filtering all articles...");
            readAndFilterArticles(allMoviesFiles, mongoMoviesDocuments);
            try(MongoClient mongoMoviesCluster0Client = MongoClients.create(MONGO_COLLECTION_URI))
            {
                MongoCollection<Document> mongoMoviesCollection = mongoMoviesCluster0Client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
                mongoMoviesCollection.insertMany(mongoMoviesDocuments);
                System.out.println("Articles stored successfully.");
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }
}
