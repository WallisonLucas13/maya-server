package com.example.ia.mayaAI.converters;

import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.utils.UuidGenerator;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class MessageConverter {

    public static MessageModel inputToUserMessage(MessageInput input) {
        return Optional.ofNullable(input)
                .map(i -> {
                    MessageModel model = new MessageModel();
                    model.setType(MessageType.USER);
                    model.setMessage(i.getMessage());
                    model.setCreatedAt(LocalDateTime.now().atOffset(ZoneOffset.of("-03:00")).toLocalDateTime());
                    model.setId(UuidGenerator.generate());
                    return model;
                })
                .orElse(new MessageModel());
    }

    public static MessageModel inputToAiMessage(MessageInput input) {
        return Optional.ofNullable(input)
                .map(i -> {
                    MessageModel model = new MessageModel();
                    model.setType(MessageType.SYSTEM);
                    model.setMessage(i.getMessage());
                    model.setCreatedAt(LocalDateTime.now().atOffset(ZoneOffset.of("-03:00")).toLocalDateTime());
                    model.setId(UuidGenerator.generate());
                    return model;
                })
                .orElse(new MessageModel());
    }
}
