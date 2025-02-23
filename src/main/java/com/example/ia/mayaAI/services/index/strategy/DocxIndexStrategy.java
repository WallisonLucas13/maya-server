package com.example.ia.mayaAI.services.index.strategy;

import com.example.ia.mayaAI.services.index.IndexOperation;
import com.example.ia.mayaAI.services.index.VectorStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class DocxIndexStrategy implements IndexOperation {

    private final VectorStoreService vectorStoreService;

    public DocxIndexStrategy(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @Override
    public String applyFileIndex(MultipartFile file, String messageSearch){
        try (XWPFDocument docx = new XWPFDocument(file.getInputStream())) {
            StringBuilder textContent = new StringBuilder();
            for (XWPFParagraph paragraph : docx.getParagraphs()) {
                textContent.append(paragraph.getText()).append("\n");
            }
            Document document = new Document(textContent.toString());
            var textSplitter = new TokenTextSplitter().apply(List.of(document));
            vectorStoreService.getVectorStore().add(textSplitter);

            var embeddingResult = vectorStoreService.getVectorStore()
                    .similaritySearch(messageSearch);
            vectorStoreService.clearVectorStore();
            return vectorStoreService.collectFromEmbeddingResult(embeddingResult);
        }
        catch (Exception e) {
            log.error("Error while indexing and searching DOCX files", e);
            return "";
        }
    }
}
