package com.lgusar.rssfeedanalyzer2.services;

import com.lgusar.rssfeedanalyzer2.dtos.FrequencyDto;
import com.lgusar.rssfeedanalyzer2.entities.AnalysisResult;
import com.lgusar.rssfeedanalyzer2.entities.NewsEntry;
import com.lgusar.rssfeedanalyzer2.repositories.AnalysisResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FrequencyService {
    private AnalysisResultRepository analysisResultRepository;

    public List<FrequencyDto> frequency(long id) {
        var analysisResults = analysisResultRepository.findAll();

        var pairs = getPairs(analysisResults);

        return getTopKeywords(pairs);
    }

    private List<FrequencyDto> getTopKeywords(List<Pair> pairs) {
        return pairs.stream().map(s -> {
            var frequency = new FrequencyDto();
            frequency.setKeyword(s.keyword);
            frequency.setCount(s.count);
            frequency.setNews(s.news.stream().toList());
            return frequency;
        }).limit(3).toList();
    }

    private List<Pair> getPairs(List<AnalysisResult> analysisResults) {
        var hashmap = new HashMap<String, Pair>();
        for (var result : analysisResults) {
            for (var keyword : result.getKeywords()) {
                var key = keyword.getKeyword();
                if (hashmap.containsKey(key)) {
                    var pair = hashmap.get(key);
                    pair.count++;
                    pair.news.addAll(
                            result.getEntries().stream().map(NewsEntry::getTitle).toList()
                    );
                    hashmap.put(key, pair);
                } else {
                    var pair = new Pair(key, 1L, new HashSet<>());
                    pair.news.addAll(result.getEntries().stream().map(NewsEntry::getTitle).toList());
                    hashmap.put(key, pair);
                }
            }
        }
        return hashmap.values().stream().sorted().toList();
    }

    @AllArgsConstructor
    static class Pair implements Comparable<Pair> {
        String keyword;
        Long count;
        Set<String> news;

        @Override
        public int compareTo(Pair o) {
            return count.compareTo(o.count);
        }
    }
}
