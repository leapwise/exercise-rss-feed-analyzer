package com.lgusar.rssfeedanalyzer2.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Keyword {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String keyword;

    public Keyword(String keyword) {
        this.keyword = keyword;
    }
}
