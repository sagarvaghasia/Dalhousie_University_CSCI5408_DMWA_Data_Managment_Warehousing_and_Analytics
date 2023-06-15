package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args)
    {
        try
        {
            NewsExtraction newsExtraction = new NewsExtraction();
            newsExtraction.extractNewsData();

            System.setProperty("java.net.preferIPv4Stack", "true");
            Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

            NewsFiltration newsFiltration = new NewsFiltration();
            newsFiltration.filterNewsData();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


}