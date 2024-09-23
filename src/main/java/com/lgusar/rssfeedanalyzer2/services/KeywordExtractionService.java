package com.lgusar.rssfeedanalyzer2.services;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class KeywordExtractionService {
    private StanfordCoreNLP pipeline;

    /**
     * Extracts keywords from a string
     *
     * @param text text from which to extract keywords
     * @return {@link HashSet<String>} of keywords
     */
    public HashSet<String> extractKeywords(String text) {
        // Extract the tokens
        var tokens = extractTokens(text);

        // Find keywords
        return findKeywords(tokens);
    }

    // TODO: implement keyword extraction algorithm
    // Possible candidates for the algorithm:
    // 1. TF-IDF
    // 2. Word2Vec
    // 3. LDA
    private HashSet<CoreLabel> extractTokens(String text) {
        var document = pipeline.processToCoreDocument(text);

        return new HashSet<>(document.tokens());
    }

    // Extracts keywords based on tokens "part-of-speech" label
    // NN means nouns, so for now keywords == nouns
    // TODO: change algorithm to something more clever
    private HashSet<String> findKeywords(HashSet<CoreLabel> tokens) {
        var result = new HashSet<String>();
        for (var token : tokens) {
            if (token.tag().startsWith("NN")) result.add(token.word());
        }

        return result;
    }
}
