package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        System.out.println("Sentiment analysis is in progress...");
        final List<SentimentAnalysisModel> sentimentAnalysisModelList = new SentimentAnalysis().fetchMongoDocuments();
        final String outputFile = "src" + File.separator + "main" + File.separator + "java" + File.separator + "org" + File.separator + "example" + File.separator + "problem2Output.txt";

        try(final FileWriter fileWriter = new FileWriter(outputFile, true))
        {
            for(SentimentAnalysisModel news : sentimentAnalysisModelList)
            {
                fileWriter.append("News Article Id : ").append(String.valueOf(news.getId())).append("\n");
                fileWriter.append("News Content : ").append(news.getContent()).append("\n");
                fileWriter.append("Word Bag : ").append(String.valueOf(news.getContentFrequencyCount())).append("\n");
                fileWriter.append("Matched Words : ").append(String.valueOf(news.getMatchedWords())).append("\n");
                fileWriter.append("Positive Score : +").append(String.valueOf(news.getPositiveScore())).append("\n");
                fileWriter.append("Negative Score : -").append(String.valueOf(news.getNegativeScore())).append("\n");
                fileWriter.append("Polarity : ").append(String.valueOf(news.getPolarity())).append("\n\n");
            }

            System.out.println("Output in file: " + outputFile);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}