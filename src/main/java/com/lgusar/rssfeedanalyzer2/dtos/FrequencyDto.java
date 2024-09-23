package com.lgusar.rssfeedanalyzer2.dtos;

import lombok.Data;

import java.util.List;

@Data
public class FrequencyDto {
    private String keyword;
    private Long count;
    private List<String> news;
}
