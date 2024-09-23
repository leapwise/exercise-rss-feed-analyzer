package com.lgusar.rssfeedanalyzer2.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AnalyseRequestDto {
    private List<String> urls;
}
