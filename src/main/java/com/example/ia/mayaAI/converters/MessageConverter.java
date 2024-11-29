package com.example.ia.mayaAI.converters;

import com.example.ia.mayaAI.inputs.MessageInput;
import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.utils.UuidGenerator;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class MessageConverter {

    public static MessageModel inputToUserMessage(MessageInput input) {
        return Optional.ofNullable(input)
                .map(i -> {
                    MessageModel model = new MessageModel();
                    model.setType(MessageType.USER);
                    model.setMessage(i.getMessage());
                    model.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());
                    model.setId(UuidGenerator.generate().toString());
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
                    model.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime());
                    model.setId(UuidGenerator.generate().toString());
                    return model;
                })
                .orElse(new MessageModel());
    }
}
