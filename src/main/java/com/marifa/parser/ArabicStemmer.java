package com.marifa.parser;

import utilities.*;

import java.io.IOException;

public class ArabicStemmer {

    private static TrainedTokenizer tokenizer;
    private static RootStemmer rootStemmer;
    private static AraNormalizer normalizer;
    private static DiacriticsRemover diacriticsRemover;
    private static PunctuationsRemover punctuationsRemover;
    private static String[] tokens;
    private static String normalizedText;

    public ArabicStemmer() throws IOException {
        tokenizer = new TrainedTokenizer();
        rootStemmer = new RootStemmer();
        normalizer = new AraNormalizer();
        diacriticsRemover = new DiacriticsRemover();
        punctuationsRemover = new PunctuationsRemover();
    }

    public static String findRoot() {
        String root = "";
        if (tokens.length == 1) {
            return rootStemmer.findRoot(tokens[0]);
        } else {
            for (int i = 0; i < tokens.length; i++) {
                root = root + rootStemmer.findRoot(tokens[i]) + " ";
            }
        }
        return root;
    }

    public void normalizeText(String text) throws IOException {
        if (tokens == null) {
            normalizedText = normalizer.normalize(text);
            normalizedText = diacriticsRemover.removeDiacritics(normalizedText);
            normalizedText = punctuationsRemover.removePunctuations(normalizedText);
            tokens = tokenizer.tokenize(normalizedText);
        } else {
            tokens = null;
            normalizedText = normalizer.normalize(text);
            normalizedText = diacriticsRemover.removeDiacritics(normalizedText);
            normalizedText = punctuationsRemover.removePunctuations(normalizedText);
            tokens = tokenizer.tokenize(normalizedText);
        }
    }
}
