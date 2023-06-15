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


public class NewsExtraction
{
    private static  String DIRECTORY_NAME;
    private static  String NEWS_API_URL;
    private static  String NEWS_API_KEY;
    private static  int TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY;
        private static  int TOTAL_ARTICLES_PERMITTED_IN_EACH_FILE;
    private static  String[] CATEGORY_KEYWORDS;

    static
    {
        DIRECTORY_NAME = "./newsData/";
        NEWS_API_URL = "https://newsapi.org/v2/everything";
        NEWS_API_KEY = "ff8e73590ed8444f8a152b9f5aba7434";
        TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY = 100;
        TOTAL_ARTICLES_PERMITTED_IN_EACH_FILE = 3;
        CATEGORY_KEYWORDS = new String[] {"Canada", "University", "Dalhousie University", "Halifax",
                                            "Canada Education", "Moncton", "Toronto", "Oil", "Inflation"};
    }
    private String fetchNews(String category)
    {
        try
        {
             String queryCategory = category.replaceAll(" ", "%20");
             String newsAPIURL = NEWS_API_URL + "?q=" + queryCategory + "&language=en&pagesize=" + TOTAL_ARTICLES_PERMITTED_IN_SINGLE_CATEGORY;
             HttpRequest httpRequest = HttpRequest
                                    .newBuilder(URI.create(newsAPIURL))
                                    .header("X-Api-Key", NEWS_API_KEY)
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

    private void storeNews(String category,  String newsArticleJSON)
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
        String newsDataFileName = DIRECTORY_NAME + categoryFileName + "_" + System.currentTimeMillis() + ".txt";
        try(FileWriter fileWriter = new FileWriter(newsDataFileName, StandardCharsets.UTF_8))
        {
            fileWriter.write(newsArticleJSON);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void prepareAndStoreNews(String category,  String newsJSON)
    {
        JSONObject newsJSONObject = new JSONObject(newsJSON);
        JSONArray newsArticlesArray = newsJSONObject.getJSONArray("articles");
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
                storeNews(category,articlesInEachFileStringBuilder);
                totalArticlesInEachFile = 0;
                articlesStringBuilder.setLength(0);
            }
        }

    }

    public void extractNewsData()
    {
        for(String category : CATEGORY_KEYWORDS)
        {
            System.out.println("Fetching and Storing news for category -> " + category);
            String newsJSON = fetchNews(category);
            if(newsJSON != null)
            {
                prepareAndStoreNews(category,newsJSON);
            }
        }
    }


}
