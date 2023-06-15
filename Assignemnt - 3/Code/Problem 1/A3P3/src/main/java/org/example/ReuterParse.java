package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReuterParse
{

    public static String[] FILE_NAMES = {"reut2-009.sgm", "reut2-014.sgm"};


    public List<Reuter> parseSGM(String fileName)
    {
        List<Reuter> reuters = new ArrayList<>();
        String fileData = "";
        try
        {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                fileData = fileData + scanner.nextLine();
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        fileData = filterReuter(fileData, fileName);

        Pattern patternReuter = Pattern.compile("<REUTERS(.*?)>(.*?)</REUTERS>");

        Matcher matcherReuter = patternReuter.matcher(fileData);

        while(matcherReuter.find())
        {
            String reuter = matcherReuter.group(2);
            Pattern patternText = Pattern.compile("<TEXT(.*?)>(.*?)</TEXT>");
            Matcher matcherText = patternText.matcher(reuter);

            if(matcherText.find())
            {
                String text = matcherText.group(2);
                Pattern patternTitle = Pattern.compile("<TITLE(.*?)>(.*?)</TITLE>");
                Matcher matcherTitle = patternTitle.matcher(text);

                String title = null;
                String body = null;

                if(matcherTitle.find())
                {
                    title = matcherTitle.group(2);
                }

                Pattern patternBody = Pattern.compile("<BODY(.*?)>(.*?)</BODY>");
                Matcher matcherBody = patternBody.matcher(text);

                if(matcherBody.find())
                {
                    body = matcherBody.group(2);
                }

                if(title == null || body == null){
                    continue;
                }
                else
                {
                    Reuter reuterObject = new Reuter(title, body);
                    reuters.add(reuterObject);
                }
            }
        }

        return reuters;
    }

    public String filterReuter(String data, String fileName)
    {

        Pattern urlRegex = Pattern.compile("(https?:\\/\\/|www\\.)(\\([-A-Za-z0-9+&@#\\/%=_|$?!:,.]*\\)|[-A-Za-z0-9+&@#\\/%=_|$?!:,.])*(?:\\([-A-Za-z0-9+&@#\\/%=_|$?!:,.]*\\)|[A-Za-z0-9+&@#\\/%=_|$]*)");
        Matcher matcherUrl = urlRegex.matcher(data);
        data = matcherUrl.replaceAll(" ");

        Pattern patternSpecialWord = Pattern.compile("(&(.*?);)");
        Matcher matcherSpecialWord = patternSpecialWord.matcher(data);
        data = matcherSpecialWord.replaceAll("");

        Pattern patternWhiteSpace = Pattern.compile("\\\\r|\\\\n|\\\\t");
        Matcher matcherWhiteSpace = patternWhiteSpace.matcher(data);
        data = matcherWhiteSpace.replaceAll(" ");

        Pattern patternSpecialCharacter = Pattern.compile("[^0-9a-zA-Z:,{}\"'-\\[\\] %!]");
        Matcher matcherSpecialCharacter = patternSpecialCharacter.matcher(data);
        data = matcherSpecialCharacter.replaceAll(" ");

        Pattern patternEmojis = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
        Matcher matcherEmojis = patternEmojis.matcher(data);
        data = matcherEmojis.replaceAll(" ");

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Filtered_" + fileName.substring(0, fileName.length() - 4) + ".txt"));
            writer.write(data);
            writer.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return data;
    }

    // This method iterate through all filenames
    public HashMap<String, List<Reuter>> parseAllFiles()
    {
        HashMap<String, List<Reuter>> allReuters = new HashMap<>();
        for(String fileName: FILE_NAMES){
            List<Reuter> reuters = this.parseSGM(fileName);
            allReuters.put(fileName, reuters);

            System.out.println(reuters.size());
        }
        return allReuters;
    }
}
