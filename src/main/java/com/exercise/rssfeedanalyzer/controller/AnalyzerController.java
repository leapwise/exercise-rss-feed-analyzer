package com.exercise.rssfeedanalyzer.controller;

import com.exercise.rssfeedanalyzer.model.dto.TopicDTO;
import com.exercise.rssfeedanalyzer.service.AnalyzerService;
import com.rometools.rome.io.FeedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class AnalyzerController {

    @Autowired
    AnalyzerService analyzerService;

    @PostMapping("/analyse/new")
    public ResponseEntity<String> analyseNew(@RequestBody List<String> rssUrls) throws FeedException, IOException {
        if (rssUrls.size() >= 2) {
            String uniqueIdentifier = analyzerService.analyseData(rssUrls);
            return new ResponseEntity<>(uniqueIdentifier, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("It is necessary to send at least two links", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/frequency/{id}")
    public ResponseEntity<List<TopicDTO>> frequency(@PathVariable("id") String uniqueIdentifier) {
        List<TopicDTO> topThreeResults = analyzerService.getTopThreeResults(uniqueIdentifier);
        return new ResponseEntity<>(topThreeResults, HttpStatus.OK);
    }
}