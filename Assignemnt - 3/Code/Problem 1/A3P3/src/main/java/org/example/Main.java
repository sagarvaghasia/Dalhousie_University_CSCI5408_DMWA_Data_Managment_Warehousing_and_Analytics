package org.example;

import java.util.HashMap;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        String databaseString = "mongodb+srv://root:root@cluster0.8oqjs.mongodb.net/?retryWrites=true&w=majority";
        ReuterParse reuterParse = new ReuterParse();
        HashMap<String, List<Reuter>> allReuters = reuterParse.parseAllFiles();

        ReuterStore reuterStore = new ReuterStore(databaseString);
        reuterStore.storeAll(allReuters);
    }
}
