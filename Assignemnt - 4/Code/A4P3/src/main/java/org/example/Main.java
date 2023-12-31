package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        System.out.println("Semantic analysis is in progress...");
        final SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
        final SemanticAnalysisModel semanticAnalysisModel = semanticAnalysis.fetchMongoDocuments();

        if (semanticAnalysisModel == null)
        {
            System.exit(0);
        }

        final String outputFile = "src" + File.separator + "main" + File.separator + "java" + File.separator + "org" + File.separator + "example" + File.separator + "problem3Output.txt";
        final int totalDocuments = semanticAnalysisModel.getTotalDocuments();
        final List<SemanticAnalysisModel.TFIDF> tfidfList = semanticAnalysisModel.getTfidfList();

        try (final FileWriter fileWriter = new FileWriter(outputFile, true)) {
            fileWriter.append("TF-IDF (Term Frequency - Inverse Document Frequency").append("\n");
            fileWriter.append("===================================================").append("\n");
            fileWriter.append("Total Document: ").append(String.valueOf(totalDocuments)).append("\n");
            fileWriter.append(String.format("%-15s%-10s%-30s%-20s%n",
                    "Search Query", "df", "N/df", "Log10(N/df)"));
            for (final SemanticAnalysisModel.TFIDF tfidf : tfidfList) {
                fileWriter.append(String.format("%-15s%-10s%-30s%-20s%n",
                        tfidf.getSearchQuery(),
                        tfidf.getDocumentContainingTerm(),
                        totalDocuments + "/" + tfidf.getDocumentContainingTerm() + "=" + (tfidf.getTotalDocumentsByDocumentContainingTerm()),
                        tfidf.getLog10NTotalDocumentsByDocumentContainingTerm()));
            }

            fileWriter.append("\n\n");
            fileWriter.append("Calculation of term ").append(semanticAnalysisModel.getTerm()).append("\n");
            fileWriter.append("==========================").append("\n");
            fileWriter.append("Term: ").append(semanticAnalysisModel.getTerm()).append("\n");
            fileWriter.append(String.format("%-40s%-20s%-20s%n",
                    semanticAnalysisModel.getTerm() + " appeared in " + semanticAnalysisModel.getTermWordFreqList().size() + " documents",
                    "Total Words(m)",
                    "Frequency(f)"));
            for (final SemanticAnalysisModel.TermWordFreq termWordFreq : semanticAnalysisModel.getTermWordFreqList()) {
                fileWriter.append(String.format("%-40s%-20s%-20s%n",
                        termWordFreq.getArticleName(),
                        termWordFreq.getTotalWordsCount(),
                        termWordFreq.getFrequency()));
            }

            fileWriter.append("\n\n");
            fileWriter.append("News article with highest relative frequency").append("\n");
            fileWriter.append("============================================").append("\n");
            int highestRelFreqArticleIndex = -1;
            float highestRelFreq = -1;
            for (int i = 0; i < semanticAnalysisModel.getTermWordFreqList().size(); ++i) {
                final SemanticAnalysisModel.TermWordFreq termWordFreq = semanticAnalysisModel.getTermWordFreqList().get(i);
                if (highestRelFreqArticleIndex == -1) {
                    highestRelFreqArticleIndex = i;
                    highestRelFreq = ((float) termWordFreq.getFrequency() / termWordFreq.getTotalWordsCount());
                } else {
                    float currentRelFreq = ((float) termWordFreq.getFrequency() / termWordFreq.getTotalWordsCount());
                    if (currentRelFreq > highestRelFreq) {
                        highestRelFreqArticleIndex = i;
                        highestRelFreq = currentRelFreq;
                    }
                }
            }
            fileWriter.append("Content: ").append(semanticAnalysisModel.getTermWordFreqList().get(highestRelFreqArticleIndex).getArticleContent()).append("\n");
            fileWriter.append("f/m: ").append(Float.toString(highestRelFreq));

            // Output printed
            System.out.println("Output in file: " + outputFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}