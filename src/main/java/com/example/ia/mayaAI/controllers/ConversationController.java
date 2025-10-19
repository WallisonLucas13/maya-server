package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.responses.ConversationModelResponse;
import com.example.ia.mayaAI.services.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationModel>> getAllConversationsByUser(@RequestParam String username) {
        return ResponseEntity.ok(conversationService.getAllConversationsByUser(username));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationModelResponse> getConversationDetailsById(@PathVariable String conversationId){
        return ResponseEntity.ok(conversationService.getConversationById(conversationId));
    }
}
