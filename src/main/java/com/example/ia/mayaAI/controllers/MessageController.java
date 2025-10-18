package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.requests.SimpleMessageRequest;
import com.example.ia.mayaAI.responses.SimpleMessageResponse;
import com.example.ia.mayaAI.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversation")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<SimpleMessageResponse> postMessage(
            @RequestHeader("username") String username,
            @RequestBody SimpleMessageRequest request,
            @RequestParam(value = "sessionId", required = false) String sessionId
    ){
        return ResponseEntity.ok(messageService
                .postMessage(username, request, sessionId));
    };

}
