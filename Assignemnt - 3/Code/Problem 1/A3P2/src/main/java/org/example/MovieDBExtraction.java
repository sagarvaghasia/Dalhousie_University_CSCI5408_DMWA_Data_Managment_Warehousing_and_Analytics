package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;


public class MovieDBExtraction
{
    private static final String DIRECTORY_NAME;
    private static final String MOVIES_API_URL;
    private static final String MOVIES_API_KEY;
    private static final int TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY;
    private static final String[] CATEGORY_KEYWORDS;

    private static  int TOTAL_ARTICLES_PERMITTED_IN_EACH_FILE;

    static
    {
        DIRECTORY_NAME = "./moviesData/";
        MOVIES_API_URL = "https://www.omdbapi.com/";
        MOVIES_API_KEY = "6211d063";
        TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY = 100;
        TOTAL_ARTICLES_PERMITTED_IN_EACH_FILE = 3;
        CATEGORY_KEYWORDS = new String[] {"Canada", "University", "Moncton", "Halifax",
                                            "Toronto", "Vancouver", "Alberta", "Niagara"};
    }
    private String fetchMovies(String category)
    {
        try
        {
             String queryCategory = category.replaceAll(" ", "%20");
             String moviesAPIURL = MOVIES_API_URL + "?apikey="+ MOVIES_API_KEY +"&s=" + queryCategory + "&language=en&pagesize=" + TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY;
             HttpRequest httpRequest = HttpRequest
                                    .newBuilder(URI.create(moviesAPIURL))
                                    .header("X-Api-Key", MOVIES_API_KEY)
                                    .timeout(Duration.of(30, ChronoUnit.SECONDS))
                                    .GET()
                                    .build();
             HttpClient httpClient = HttpClient.newBuilder().build();
             HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void storeMovies(String category, String moviesArticleJSON)
    {
         String categoryFileName = category.replaceAll(" ", "").toLowerCase();
         Path path = Paths.get(DIRECTORY_NAME);
        try
        {
            if(!Files.exists(path))
            {
                Files.createDirectory(path);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }
        String moviesDataFileName = DIRECTORY_NAME + categoryFileName + "_" + System.currentTimeMillis() + ".txt";
        try(FileWriter fileWriter = new FileWriter(moviesDataFileName, StandardCharsets.UTF_8))
        {
            fileWriter.write(moviesArticleJSON);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void prepareAndStoreMovies(String category, String newsJSON)
    {
        JSONObject newsJSONObject = new JSONObject(newsJSON);
        JSONArray newsArticlesArray = newsJSONObject.getJSONArray("Search");
        StringBuilder articlesStringBuilder = new StringBuilder();
        int totalArticlesInEachFile = 0;
        for(int currentArticleIndex = 0; currentArticleIndex < newsArticlesArray.length(); ++currentArticleIndex)
        {
            articlesStringBuilder.append(newsArticlesArray.getJSONObject(currentArticleIndex)).append(",");
            totalArticlesInEachFile++;
            if(totalArticlesInEachFile == TOTAL_ARTICLES_PERMITTED_IN_EACH_FILE)
            {
                articlesStringBuilder.setLength(articlesStringBuilder.length() - 1);
                String articlesInEachFileStringBuilder = "[" + articlesStringBuilder + "]";
                storeMovies(category,articlesInEachFileStringBuilder);
                totalArticlesInEachFile = 0;
                articlesStringBuilder.setLength(0);
            }
        }

    }

    public void extractMoviesData()
    {
        for(String category : CATEGORY_KEYWORDS)
        {
            System.out.println("Fetching and Storing movies for category -> " + category);
             String moviesJSON = fetchMovies(category);
            if(moviesJSON != null)
            {
                prepareAndStoreMovies(category,moviesJSON);
            }
        }
    }


}
