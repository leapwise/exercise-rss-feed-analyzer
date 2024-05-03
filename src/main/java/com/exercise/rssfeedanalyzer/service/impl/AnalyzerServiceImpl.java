package com.exercise.rssfeedanalyzer.service.impl;

import com.exercise.rssfeedanalyzer.model.dto.TopicDTO;
import com.exercise.rssfeedanalyzer.model.dto.TopicDetailsDTO;
import com.exercise.rssfeedanalyzer.model.entity.TopicEntity;
import com.exercise.rssfeedanalyzer.repository.AnalyzerRepository;
import com.exercise.rssfeedanalyzer.service.AnalyzerService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyzerServiceImpl implements AnalyzerService {

    @Autowired
    AnalyzerRepository analyzerRepository;

    public String analyseData(List<String> rssUrls) throws IOException, FeedException {

        String uniqueIdentifier = UUID.randomUUID().toString();

        for (String rssUrl : rssUrls) {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(rssUrl)));
            List<SyndEntry> entries = feed.getEntries();

            for (SyndEntry entry : entries) {
                TopicEntity topicEntity = new TopicEntity();
                Set<String> topicNames = getTopicNames(entry.getTitle());

                topicEntity.setUniqueIdentifier(uniqueIdentifier);
                topicEntity.setLink(entry.getLink());
                topicEntity.setTitle(entry.getTitle());
                topicEntity.setTopics(topicNames);

                analyzerRepository.save(topicEntity);
            }
        }

        return uniqueIdentifier;
    }

    public List<TopicDTO> getTopThreeResults(String uniqueIdentifier) {
        List<TopicEntity> allResults = analyzerRepository.findByUniqueIdentifier(uniqueIdentifier);
        List<TopicDTO> topics = new ArrayList<>();

        Map<String, Integer> topicCounts = new HashMap<>();
        for (TopicEntity entity : allResults) {
            for (String topic : entity.getTopics()) {
                if (topicCounts.containsKey(topic)) {
                    topicCounts.put(topic, topicCounts.get(topic) + 1);
                } else {
                    topicCounts.put(topic, 1);
                }
            }
        }

        List<Map.Entry<String, Integer>> topThreeTopics = topicCounts.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(3)
                .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : topThreeTopics) {
            String topicName = entry.getKey();
            List<TopicDetailsDTO> details = new ArrayList<>();

            for (TopicEntity entity : allResults) {
                if (entity.getTopics().contains(topicName)) {
                    details.add(new TopicDetailsDTO(entity.getTitle(), entity.getLink()));
                }
            }

            topics.add(new TopicDTO(topicName, details));
        }

        return topics;
    }

    private Set<String> getTopicNames(String title) {

        Set<String> topicNames = new HashSet<>();

        String modifiedTitle = title.toLowerCase();
        modifiedTitle = StringUtils.stripAccents(modifiedTitle).replaceAll("[^a-z0-9 ]", "");

        Set<String> stopwords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("src/main/resources/stopwords.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                stopwords.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File stopwords.txt not found");
        }

        for (String stopword : stopwords) {
            modifiedTitle = modifiedTitle.replaceAll("\\b" + stopword + "\\b", "");
        }

        Collections.addAll(topicNames, modifiedTitle.trim().split(" +"));

        return topicNames;
    }
}