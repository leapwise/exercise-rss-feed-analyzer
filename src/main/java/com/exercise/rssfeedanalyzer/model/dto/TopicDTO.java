package com.exercise.rssfeedanalyzer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO {

    private String topic;
    private List<TopicDetailsDTO> topicDetails;
}
