package com.exercise.rssfeedanalyzer.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uniqueIdentifier;

    @Column(length = 2000)
    private String link;

    @Column(length = 2000)
    private String title;

    @Column(length = 2000)
    private Set<String> topics;

}
