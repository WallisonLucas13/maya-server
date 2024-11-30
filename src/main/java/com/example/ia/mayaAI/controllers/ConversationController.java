package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.converters.MessageConverter;
import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.responses.ConversationPreviewResponse;
import com.example.ia.mayaAI.responses.ConversationResponse;
import com.example.ia.mayaAI.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping("/mensagem")
    public ResponseEntity<MessageModel> sendMessage(
            @RequestBody MessageInput input,
            @RequestParam(value = "conversationId", required = false, defaultValue = "") String conversationId){

        MessageModel messageModel = MessageConverter.inputToUserMessage(input);
        return ResponseEntity.ok(conversationService.sendMessage(conversationId, messageModel));
    }

    @GetMapping("/conversa")
    public ResponseEntity<ConversationResponse> getConversation(
            @RequestParam(value = "conversationId", required = false) String conversationId
    ){
        return ResponseEntity.ok(conversationService
                .getConversationById(conversationId));
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ConversationPreviewResponse>> getConversations(){
        return ResponseEntity.ok(conversationService.getConversationsPreview());
    }
}
