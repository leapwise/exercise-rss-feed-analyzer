package com.lgusar.rssfeedanalyzer2.repositories;

import com.lgusar.rssfeedanalyzer2.entities.NewsEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsEntryRepository extends JpaRepository<NewsEntry, Long> {
}
