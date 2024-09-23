package com.lgusar.rssfeedanalyzer2.config;

import com.apptasticsoftware.rssreader.RssReader;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class Config {
    @Bean
    public RssReader rssReader() {
        return new RssReader();
    }

    @Bean
    public StanfordCoreNLP pipeline() {
        var props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

        return new StanfordCoreNLP(props);
    }
}
