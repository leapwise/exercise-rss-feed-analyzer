package com.lgusar.rssfeedanalyzer2.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NewsEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String url;
    @ManyToOne()
    private AnalysisResult analysisResult;

    public NewsEntry(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
