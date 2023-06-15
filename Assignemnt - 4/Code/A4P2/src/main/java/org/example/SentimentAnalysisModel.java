package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SentimentAnalysisModel
{
    public enum POLARITY
    {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }

    final int id;
    final String content;
    final HashMap<String, Integer> contentFrequencyCount;
    final Set<String> matchedWords;
    POLARITY polarity;
    int positiveScore;
    int negativeScore;

    public SentimentAnalysisModel(final int id, final String content)
    {
        this.id = id;
        this.content = content;
        this.contentFrequencyCount = new HashMap<>();
        this.matchedWords = new HashSet<>();
        this.polarity = POLARITY.NEUTRAL;
        this.positiveScore = 0;
        this.negativeScore = 0;
    }

    public int getId()
    {
        return this.id;
    }

    public String getContent()
    {
        return  this.content;
    }

    public HashMap<String, Integer> getContentFrequencyCount()
    {
        return this.contentFrequencyCount;
    }

    public Set<String> getMatchedWords()
    {
        return this.matchedWords;
    }

    public POLARITY getPolarity()
    {
        return this.polarity;
    }

    public int getPositiveScore()
    {
        return this.positiveScore;
    }

    public int getNegativeScore()
    {
        return this.negativeScore;
    }

    private void prepareFrequencyCount()
    {
        final String[] frequencyCountArray = this.content.split(" ");
        for (final String word : frequencyCountArray)
        {
            final String wordLC = word.toLowerCase();
            if (this.contentFrequencyCount.containsKey(wordLC))
            {
                this.contentFrequencyCount.put(wordLC, this.contentFrequencyCount.get(wordLC) + 1);
            }
            else
            {
                this.contentFrequencyCount.put(wordLC, 1);
            }
        }
    }

    public void calculatePolarity(final Set<String> negativeWords, final Set<String> positiveWords)
    {
        prepareFrequencyCount();
        final Set<Map.Entry<String, Integer>> contentFrequencyCountEntries = this.contentFrequencyCount.entrySet();
        for(final Map.Entry<String, Integer> contentWordMap : contentFrequencyCountEntries)
        {
            final String contentWord = contentWordMap.getKey();
            if(negativeWords.contains(contentWord))
            {
                this.negativeScore += contentWordMap.getValue();
                this.matchedWords.add(contentWord);
            } else if (positiveWords.contains(contentWord))
            {
                this.positiveScore += contentWordMap.getValue();
                this.matchedWords.add(contentWord);
            }
        }
        final int finalScore = (-this.negativeScore) + (this.positiveScore);
        if(finalScore > 0)
        {
            this.polarity = POLARITY.POSITIVE;
        } else if (finalScore < 0)
        {
            this.polarity = POLARITY.NEGATIVE;
        } else
        {
            this.polarity = POLARITY.NEUTRAL;
        }
    }

}
