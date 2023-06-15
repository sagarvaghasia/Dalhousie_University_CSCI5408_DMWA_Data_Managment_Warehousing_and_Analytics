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

public class NewsFiltration
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
    public static String MONGO_CONNECTION_URI;

    static
    {
        SEPARATOR_REPLACEMENT= "}@@@@@@@@@{";
        SEPARATOR_SPLIT = "@@@@@@@@@";
        EMPTY_STRING = "";
        DIRECTORY_NAME = "./newsData/";
        MONGO_USER_NAME = "root";
        MONGO_PASSWORD = "root";
        MONGO_HOST_NAME = "cluster0.8oqjs.mongodb.net";
        DATABASE_NAME = "myMongoNews";
        COLLECTION_NAME = "mongoNews";
        MONGO_CONNECTION_URI = "mongodb+srv://" + MONGO_USER_NAME + ":" + MONGO_PASSWORD + "@" + MONGO_HOST_NAME + "/" + DATABASE_NAME + "?retryWrites=true&w=majority";
    }

    private File[] readAllFileNames()
    {
        File newsDataFolder = new File(DIRECTORY_NAME);
        return newsDataFolder.listFiles();
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

    public void readAndFilterArticles(File[] allNewsFiles, List<Document> mongoNewsDocuments)
    {
        int totalArticlesRead = 0;
        for(File newsFile : allNewsFiles)
        {
            try
            {
                String newsFileContent = Files.readString(Paths.get(newsFile.getPath())).trim();
                String[] filteredArticlesList = filterArticleContent(newsFileContent).split(SEPARATOR_SPLIT);
                for(String article : filteredArticlesList)
                {
                    Document articleDocument = Document.parse(article);
                    mongoNewsDocuments.add(articleDocument);
                    totalArticlesRead++;
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Total articles read and filtered - " + totalArticlesRead);
    }

    public void filterNewsData()
    {
        File[] allNewsFiles = readAllFileNames();
        if(allNewsFiles != null && allNewsFiles.length != 0)
        {
            List<Document> mongoNewsDocuments = new ArrayList<>();
            System.out.println("Filtering all articles...");
            readAndFilterArticles(allNewsFiles, mongoNewsDocuments);
            try(MongoClient mongoNewsCluster0Client = MongoClients.create(MONGO_CONNECTION_URI))
            {
                MongoCollection<Document> mongoNewsCollection = mongoNewsCluster0Client.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
                mongoNewsCollection.insertMany(mongoNewsDocuments);
                System.out.println("Articles stored successfully.");
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }
}
