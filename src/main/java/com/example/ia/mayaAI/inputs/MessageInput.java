package com.example.ia.mayaAI.inputs;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInput {

    private String message;
}
