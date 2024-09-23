package com.lgusar.rssfeedanalyzer2.dtos;

import java.util.HashSet;

public record NewsEntryDto(String title, String url, HashSet<String> keywords) {
}
