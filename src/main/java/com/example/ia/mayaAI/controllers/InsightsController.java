package com.example.ia.mayaAI.controllers;

import com.example.ia.mayaAI.responses.insights.TotalMessagesResponse;
import com.example.ia.mayaAI.services.insights.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboards")
public class InsightsController {

    @Autowired
    private InsightService insightService;

    @GetMapping("/total-messages")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TotalMessagesResponse>> getTotalMessages() {
        return ResponseEntity.ok(insightService.getTotalMessagesByUsers());
    }
}
