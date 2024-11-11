package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maya")
@CrossOrigin(origins = "http://localhost:4200")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/mensagem")
    public ResponseEntity<MessageModel> sendMessage(
            @RequestBody MessageInput input,
            @RequestParam(value = "conversationId", required = false) UUID conversationId){
        return ResponseEntity.ok(conversationService.sendMessage(conversationId, input));
    }

    @GetMapping("/conversa")
    public ResponseEntity<ConversationModel> getConversation(
            @RequestParam(value = "conversationId", required = false) UUID conversationId
    ){
        return ResponseEntity.ok(conversationService
                .getConversationById(conversationId));
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ConversationResponse>> getConversations(){
        return ResponseEntity.ok(conversationService.getConversations());
    }
}
