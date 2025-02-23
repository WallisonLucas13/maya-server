package com.example.ia.mayaAI.services.index;

import lombok.Getter;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorStoreService {

    @Getter
    private VectorStore vectorStore;

    @Autowired
    private final EmbeddingModel embeddingModel;

    public VectorStoreService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    public String collectFromEmbeddingResult(List<Document> embeddingResult){
        return embeddingResult.parallelStream()
                .map(result -> clearText(result.getContent()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String clearText(String text){
        return text.replaceAll("\\s+", " ");
    }

    public void clearVectorStore(){
        this.vectorStore = new SimpleVectorStore(embeddingModel);
    }

}
