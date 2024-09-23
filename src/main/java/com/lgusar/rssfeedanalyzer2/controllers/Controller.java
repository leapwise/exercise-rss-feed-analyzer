package com.lgusar.rssfeedanalyzer2.controllers;

import com.lgusar.rssfeedanalyzer2.dtos.AnalyseRequestDto;
import com.lgusar.rssfeedanalyzer2.dtos.FrequencyDto;
import com.lgusar.rssfeedanalyzer2.services.AnalysisService;
import com.lgusar.rssfeedanalyzer2.services.FrequencyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class Controller {
    private AnalysisService analysisService;
    private FrequencyService frequencyService;

    @PostMapping("/analyse/new")
    public List<Long> analyse(@RequestBody AnalyseRequestDto request) {
        // Perhaps check if result is empty
        // or if the method is changed to throw an exception
        // and then send an error
        return analysisService.analyseFeeds(request.getUrls());
    }

    // This request doesn't need a parameter
    @GetMapping("/frequency/{id}")
    public List<FrequencyDto> frequency(@PathVariable Long id) {
        return frequencyService.frequency(id);
    }
}
