package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args)
    {
        try
        {
            MovieDBExtraction movieDBExtraction = new MovieDBExtraction();
            movieDBExtraction.extractMoviesData();

            System.setProperty("java.net.preferIPv4Stack", "true");
            Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

            MoviesFiltration moviesFiltration = new MoviesFiltration();
            moviesFiltration.filterMovieData();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


}