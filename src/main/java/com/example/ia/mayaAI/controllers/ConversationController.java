package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.responses.preview.ConversationPreviewResponse;
import com.example.ia.mayaAI.responses.principal.ConversationResponse;
import com.example.ia.mayaAI.responses.principal.MessageResponse;
import com.example.ia.mayaAI.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/conversas/preview")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ConversationPreviewResponse>> getConversationsPreview(){
        return ResponseEntity.ok(conversationService.getConversationsPreview());
    }

    @GetMapping("/conversa/preview")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ConversationPreviewResponse> getConversationPreview(
            @RequestParam(value = "conversationId") String conversationId
    ){
        return ResponseEntity.ok(conversationService.getConversationPreviewById(conversationId));
    }

    @GetMapping("/conversa")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ConversationResponse> getConversation(
            @RequestParam(value = "conversationId", required = false) String conversationId
    ){
        return ResponseEntity.ok(conversationService
                .getConversationById(conversationId));
    }

    @PostMapping("/mensagem")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestBody MessageInput input,
            @RequestParam(value = "conversationId", required = false, defaultValue = "") String conversationId){

        return ResponseEntity.ok(conversationService
                .sendMessage(conversationId, input));
    }

    @PostMapping("/files/mensagem")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> sendMessageWithFile(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("input") MessageInput input,
            @RequestParam(value = "conversationId", required = false, defaultValue = "") String conversationId){

        return ResponseEntity.ok(conversationService
                .sendMessageWithFile(conversationId, input, files));
    }

    @DeleteMapping("/conversas")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteConversationsByLoggedUsername(){
        long deletedConversationsCount = conversationService.deleteConversationsByUsername();
        String response = "Total de conversas apagadas: %s";
        return ResponseEntity.ok(String.format(response, deletedConversationsCount));
    }
}
