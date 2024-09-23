package com.lgusar.rssfeedanalyzer2;

import com.lgusar.rssfeedanalyzer2.repositories.AnalysisResultRepository;
import com.lgusar.rssfeedanalyzer2.services.AnalysisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TestAnalysisService {
    @Autowired
    private AnalysisService service;
    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    @Test
    @DisplayName("Analyse two feeds")
    void analyseTwoFeeds() {
        var urls = List.of(
                "http://feeds.abcnews.com/abcnews/usheadlines",
                "http://rss.cnn.com/rss/cnn_topstories.rss"
        );

        service.analyseFeeds(urls);
        var entries = analysisResultRepository.findAll();

        Assertions.assertFalse(entries.isEmpty());
    }

    @Test
    @DisplayName("Analyse one feed")
    void analyseOneFeed() {
        var urls = List.of(
                "http://feeds.abcnews.com/abcnews/usheadlines"
        );

        service.analyseFeeds(urls);
        var result = analysisResultRepository.findAll();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("No feeds")
    void noFeeds() {
        service.analyseFeeds(new ArrayList<>());
        var result = analysisResultRepository.findAll();
        Assertions.assertTrue(result.isEmpty());
    }
}
