package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.mongodb.client.*;
import org.bson.Document;

public class SentimentAnalysis
{
    public static String MONGO_USER_NAME;
    public static String MONGO_PASSWORD;
    public static String MONGO_HOST_NAME;
    public static String DATABASE_NAME;
    public static String COLLECTION_NAME;
    public static String MONGO_CONNECTION_URI;

    static
    {
        MONGO_USER_NAME = "root";
        MONGO_PASSWORD = "root";
        MONGO_HOST_NAME = "cluster0.8oqjs.mongodb.net";
        DATABASE_NAME = "myMongoNews";
        COLLECTION_NAME = "mongoNews";
        MONGO_CONNECTION_URI = "mongodb+srv://" + MONGO_USER_NAME + ":" + MONGO_PASSWORD + "@" + MONGO_HOST_NAME + "/" + DATABASE_NAME + "?retryWrites=true&w=majority";
    }

    public Set<String> positiveWordsSet()
    {
        // https://gist.github.com/mkulakowski2/4289437
        final String positiveWordsFile = "sentiment_words" + File.separator + "positive_words.txt";
        final Set<String> positiveWordsSet = new HashSet<>();
        try (final FileReader posFileReader = new FileReader(positiveWordsFile);
             final BufferedReader posBufferedReader = new BufferedReader(posFileReader))
        {
            String positiveWord;
            while ((positiveWord = posBufferedReader.readLine()) != null)
            {
                positiveWordsSet.add(positiveWord.toLowerCase());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return positiveWordsSet;
    }

    public Set<String> negativeWordsSet()
    {
        // https://gist.github.com/mkulakowski2/4289441
        final String negativeWordsFile = "sentiment_words" + File.separator + "negative_words.txt";
        final Set<String> negativeWordsSet = new HashSet<>();
        try (final FileReader negFileReader = new FileReader(negativeWordsFile);
             final BufferedReader negBufferedReader = new BufferedReader(negFileReader))
        {
            String negativeWord;
            while ((negativeWord = negBufferedReader.readLine()) != null)
            {
                negativeWordsSet.add(negativeWord.toLowerCase());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return negativeWordsSet;
    }

    public void fetchNewsArticles(final MongoCursor<Document> mongoDocuments,
                                  final List<SentimentAnalysisModel> sentimentAnalysisModelList)
    {
        final Set<String> articlesContentSet = new LinkedHashSet<>();
        while (mongoDocuments.hasNext())
        {
            final String content = mongoDocuments.next().getString("content")
                    .replaceAll("\\[\\d+ chars]", "")
                    .replaceAll("\\\\u\\d{4}", "")
                    .replaceAll("(\")|(\\{)|(})|(--)|(\\\\)|(,)|(')|\\(|\\)|(…)", "")
                    .trim();
            articlesContentSet.add(content);
        }
        for (final String content : articlesContentSet)
        {
            sentimentAnalysisModelList.add(new SentimentAnalysisModel(sentimentAnalysisModelList.size() + 1, content));
        }
    }

    public void performAnalysis(final MongoCursor<Document> mongoDocuments,
                                 final List<SentimentAnalysisModel> sentimentAnalysisModelList)
    {
        fetchNewsArticles(mongoDocuments, sentimentAnalysisModelList);
        final Set<String> positiveSentimentWordsSet = positiveWordsSet();
        final Set<String> negativeSentimentWordsSet = negativeWordsSet();
        sentimentAnalysisModelList.forEach(entry -> entry.calculatePolarity(negativeSentimentWordsSet, positiveSentimentWordsSet));
    }

    public List<SentimentAnalysisModel> fetchMongoDocuments()
    {
        final List<SentimentAnalysisModel> sentimentAnalysisModelList = new ArrayList<>();
        try (final MongoClient mongoClient = MongoClients.create(MONGO_CONNECTION_URI))
        {
            final MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
            final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(COLLECTION_NAME);
            final MongoCursor<Document> mongoDocuments = mongoCollection.find().iterator();
            performAnalysis(mongoDocuments, sentimentAnalysisModelList);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return sentimentAnalysisModelList;
    }


}
