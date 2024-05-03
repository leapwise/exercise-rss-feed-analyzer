package com.exercise.rssfeedanalyzer.service;

import com.exercise.rssfeedanalyzer.model.dto.TopicDTO;
import com.rometools.rome.io.FeedException;

import java.io.IOException;
import java.util.List;

public interface AnalyzerService {

    String analyseData(List<String> rssUrls) throws IOException, FeedException;

    List<TopicDTO> getTopThreeResults(String uniqueIdentifier);

}

