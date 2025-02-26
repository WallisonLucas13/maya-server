package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.services.index.IndexOperation;
import com.example.ia.mayaAI.services.index.VectorStoreService;
import com.example.ia.mayaAI.services.index.strategy.DocxIndexStrategy;
import com.example.ia.mayaAI.services.index.strategy.PdfIndexStrategy;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IndexService {
    private final Map<String, IndexOperation> indexOperationMap;
    private final VectorStoreService vectorStoreService;
    private static final String PDF_FILE_TYPE = "application/pdf";
    private static final String DOCX_FILE_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public IndexService(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
        this.indexOperationMap = Map.of(
                PDF_FILE_TYPE, new PdfIndexStrategy(vectorStoreService),
                DOCX_FILE_TYPE, new DocxIndexStrategy(vectorStoreService)
        );
    }

    public String applyFileIndex(MultipartFile file, String messageSearch){
        return indexOperationMap.get(file.getContentType())
                .applyFileIndex(file, messageSearch);
    }

    public String applyWebPageIndex(List<String> webPages, String messageSearch) {
        try {
            webPages.forEach(page -> {
                String textContent = Jsoup.parse(page).text();
                Document document = new Document(textContent);
                var textSplitter = new TokenTextSplitter().apply(List.of(document));
                vectorStoreService.getVectorStore().add(textSplitter);
            });

            var embeddingResult = vectorStoreService.getVectorStore()
                    .similaritySearch(messageSearch);
            vectorStoreService.clearVectorStore();
            return vectorStoreService.collectFromEmbeddingResult(embeddingResult);
        } catch (Exception e) {
            log.error("Error while indexing and searching web pages");
            return "";
        }
    }
}
