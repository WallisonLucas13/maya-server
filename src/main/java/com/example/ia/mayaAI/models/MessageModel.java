package com.example.ia.mayaAI.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class MessageModel{

    @JsonProperty("_id")
    private String id;

    private String conversationId;

    private MessageType type;

    private String message;

    private List<String> files;

    private Instant createdAt;

    public void addFilename(String file) {
        if(files == null){
            files = new ArrayList<>();
        }
        this.files.add(file);
    }
}
