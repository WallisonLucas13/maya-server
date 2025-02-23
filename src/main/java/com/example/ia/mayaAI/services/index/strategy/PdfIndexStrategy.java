package com.example.ia.mayaAI.services.index.strategy;

import com.example.ia.mayaAI.services.index.IndexOperation;
import com.example.ia.mayaAI.services.index.VectorStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class PdfIndexStrategy implements IndexOperation {

    private final VectorStoreService vectorStoreService;

    public PdfIndexStrategy(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @Override
    public String applyFileIndex(MultipartFile file, String messageSearch) {
        try {
            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build())
                    .build();

            var pdfReader = new PagePdfDocumentReader(file.getResource(), config);
            var textSplitter = new TokenTextSplitter().apply(pdfReader.get());
            vectorStoreService.getVectorStore().add(textSplitter);

            var embeddingResult = vectorStoreService.getVectorStore()
                    .similaritySearch(messageSearch);
            this.vectorStoreService.clearVectorStore();

            return vectorStoreService.collectFromEmbeddingResult(embeddingResult);
        } catch (Exception e) {
            log.error("Error while indexing and searching DOCX files", e);
            return "";
        }
    }
}
