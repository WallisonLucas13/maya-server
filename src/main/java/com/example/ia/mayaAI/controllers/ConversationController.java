package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/mayaAI")
@CrossOrigin("*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/mensagem")
    public ResponseEntity<MessageModel> sendMessage(
            @RequestBody MessageInput input,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "conversationId", required = false) UUID conversationId){
        return ResponseEntity.ok(conversationService.sendMessage(conversationId, username, input));
    }

    @GetMapping("/conversa")
    public ResponseEntity<ConversationModel> getConversation(
            @RequestParam(value = "conversationId", required = false) UUID conversationId,
            @RequestParam(value = "username") String username
    ){
        return ResponseEntity.ok(conversationService
                .getConversationById(conversationId, username));
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ConversationResponse>> getConversations(
            @RequestParam(value = "username") String username
    ){
        return ResponseEntity.ok(conversationService.getConversations(username));
    }
}
