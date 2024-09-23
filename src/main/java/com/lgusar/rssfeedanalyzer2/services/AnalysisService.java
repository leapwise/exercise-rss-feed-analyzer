package com.lgusar.rssfeedanalyzer2.services;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import com.lgusar.rssfeedanalyzer2.dtos.NewsEntryDto;
import com.lgusar.rssfeedanalyzer2.entities.AnalysisResult;
import com.lgusar.rssfeedanalyzer2.entities.Keyword;
import com.lgusar.rssfeedanalyzer2.entities.KeywordRepository;
import com.lgusar.rssfeedanalyzer2.entities.NewsEntry;
import com.lgusar.rssfeedanalyzer2.exceptions.IncorrectSizeOfListOfFeedsException;
import com.lgusar.rssfeedanalyzer2.repositories.AnalysisResultRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class AnalysisService {
    private RssReader reader;
    private KeywordExtractionService keywordExtractionService;

    private AnalysisResultRepository analysisResultRepository;
    private KeywordRepository keywordRepository;

    /**
     * Finds common news between RSS feeds
     *
     * @param urls urls from where to fetch the RSS feeds
     * @return List of Ids of saved {@link AnalysisResult} entries
     */
    public List<Long> analyseFeeds(List<String> urls) {
        // Get feeds
        List<Feed> feeds = new ArrayList<>();
        urls.forEach(url -> {
            var items = getRssFeed(url);
            var feed = parseRssFeed(items);
            feeds.add(feed);
        });

        // If there isn't enough news feeds, then we have nothing to compare
        // Could throw an exception perhaps to return an error code of some kind through API
        if (feeds.size() < 2) return new ArrayList<>();

        // Check for similarities between feeds
        HashMap<String, List<NewsEntryDto>> similarNews;

        try {
            similarNews = findSimilarNews(feeds);
        } catch (IncorrectSizeOfListOfFeedsException e) {
            e.printStackTrace();

            return new ArrayList<>();
        }

        // Save analysis result
        var results = saveAnalysisResults(similarNews);

        // Return just the Ids of the results
        return results.stream().map(AnalysisResult::getId).toList();
    }

    // Fetches feeds
    // TODO: do this asynchronously
    private List<Item> getRssFeed(String url) {
        try {
            return reader.read(url).toList();
        } catch (IOException e) {
            System.out.printf("Could not fetch feed from url: %s", url);
        }

        return new ArrayList<>();
    }

    private Feed parseRssFeed(List<Item> items) {
        var feed = new Feed();
        feed.entries = new ArrayList<>();
        for (var item : items) {
            var title = item.getTitle();
            var url = item.getLink();

            if (title.isEmpty() || url.isEmpty()) continue;

            var preprocessedTitle = preprocessString(title.get());

            var keywords = keywordExtractionService.extractKeywords(preprocessedTitle);

            var entry = new NewsEntryDto(
                    title.get(),
                    url.get(),
                    keywords
            );

            feed.entries.add(entry);
        }

        return feed;
    }

    private String preprocessString(String s) {
        s = s.toLowerCase();
        return s.replaceAll("\\W", " ");
    }

    // This seems like an O(m * n) problem.
    // There are m feeds and each feed has n news, so how to check
    // for every news from every feed and find similar news entries based on similar keywords?
    // How to compare keywords between n different news such that all news must have at least one same keyword
    // and then keep track of all the feeds and news at the same time?
    // TODO: solve for any m
    private HashMap<String, List<NewsEntryDto>> findSimilarNews(List<Feed> feeds) throws IncorrectSizeOfListOfFeedsException {
        if (feeds.size() != 2) throw new IncorrectSizeOfListOfFeedsException();
        var map = new HashMap<String, List<NewsEntryDto>>();

        var overlaps = findAllOverlapsBetweenTwoFeeds(feeds.get(0), feeds.get(1));

        for (var overlap : overlaps) {
            var key = String.join(",", overlap.keywords());
            map.put(key, overlap.relatedNews);
        }

        // About the return type, I want to return the same keywords between similar news
        // and return the list of those news, so a hashmap seems like a good data structure,
        // even though the type looks cursed
        return map;
    }

    private List<OverlapResult> findAllOverlapsBetweenTwoFeeds(Feed firstFeed, Feed secondFeed) {
        var overlapResults = new ArrayList<OverlapResult>();
        for (var entry : firstFeed.entries) {
            var result = findOneOverlap(entry, secondFeed);
            result.ifPresent(overlapResults::add);
        }
        return overlapResults;
    }

    private Optional<OverlapResult> findOneOverlap(NewsEntryDto firstEntry, Feed secondFeed) {
        for (var secondEntry : secondFeed.entries) {
            var tmp = new HashSet<String>(firstEntry.keywords());
            tmp.retainAll(secondEntry.keywords());
            if (!tmp.isEmpty()) {
                var result = new OverlapResult(tmp, List.of(firstEntry, secondEntry));
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    private List<AnalysisResult> saveAnalysisResults(HashMap<String, List<NewsEntryDto>> results) {
        var result = new ArrayList<AnalysisResult>();

        results.forEach((keywords, newsEntries) -> {
            var analysisResult = new AnalysisResult();
            var entries = new ArrayList<NewsEntry>();

            var keywordList = new ArrayList<Keyword>(
                    Arrays.stream(keywords.split(",")).map(k -> {
                        return keywordRepository
                                .findByKeyword(k)
                                .orElse(new Keyword(k));
                    }).toList());

            analysisResult.setKeywords(keywordList);

            for (var entry : newsEntries) {
                var newsEntry = new NewsEntry(
                        entry.title(),
                        entry.url()
                );
                entries.add(newsEntry);
            }
            analysisResult.setEntries(entries);

            result.add(analysisResult);
        });

        return analysisResultRepository.saveAll(result);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    protected static class Feed {
        List<NewsEntryDto> entries;
    }


    protected static record OverlapResult(HashSet<String> keywords, List<NewsEntryDto> relatedNews) {
    }
}
