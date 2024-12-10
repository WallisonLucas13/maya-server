package com.example.ia.mayaAI.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Log4j2
@Service
public class LinkFetchService {

    private final RestClient restClient;

    public LinkFetchService() {
        this.restClient = RestClient.create();
    }

    public String fetchContent(String url){
        log.info("Fetching content from url: {}", url);
        return this.restClient.get()
                .uri(URI.create(url))
                .retrieve()
                .body(String.class);
    }
}
