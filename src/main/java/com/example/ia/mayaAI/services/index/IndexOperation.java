package com.example.ia.mayaAI.services.index;

import org.springframework.web.multipart.MultipartFile;

public interface IndexOperation {

    String applyFileIndex(MultipartFile file, String messageSearch);
}
