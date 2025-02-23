package com.example.ia.mayaAI.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IndexService {

    private VectorStore vectorStore;

    private final EmbeddingModel embeddingModel;

    public IndexService(VectorStore vectorStore, EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    public String indexAndSearchPdfFile(List<MultipartFile> files, String messageSearch){
        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build())
                .build();

        files.forEach(file -> {
            var pdfReader = new PagePdfDocumentReader(file.getResource(), config);
            var textSplitter = new TokenTextSplitter().apply(pdfReader.get());
            vectorStore.add(textSplitter);
        });

        var embeddingResult = vectorStore.similaritySearch(messageSearch);
        this.clearVectorStore();
        return collectFromEmbeddingResult(embeddingResult);
    }

    public String indexAndSearchWebPage(List<String> webPages, String messageSearch) {
        try {
            webPages.forEach(page -> {
                String textContent = Jsoup.parse(page).text();
                Document document = new Document(textContent);
                var textSplitter = new TokenTextSplitter().apply(List.of(document));
                vectorStore.add(textSplitter);
            });

            var embeddingResult = vectorStore.similaritySearch(messageSearch);
            this.clearVectorStore();
            return collectFromEmbeddingResult(embeddingResult);
        } catch (Exception e) {
            log.error("Error while indexing and searching web pages");
            return "";
        }
    }

    private String collectFromEmbeddingResult(List<Document> embeddingResult){
        return embeddingResult.parallelStream()
                .map(result -> clearText(result.getContent()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String clearText(String text){
        return text.replaceAll("\\s+", " ");
    }

    private void clearVectorStore(){
        this.vectorStore = new SimpleVectorStore(embeddingModel);
    }
}
