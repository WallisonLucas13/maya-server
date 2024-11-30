package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.ConversationModel;
import com.example.ia.mayaAI.models.MessageModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
public class PromptService {
    public static Prompt promptGenerate(List<MessageModel> messages, String username) {
        String initialInstructions = """
                Você é uma Inteligência Artificial chamada Maya,
                presente em um chatbot para responder perguntas dos usuários.
                Vou enviar o contexto de mensagens anteriores para que você possa responder
                de forma mais precisa. Aqui está o username do usuário: %s.
                Use esse contexto para responder de forma precisa e contextual.
                Seja gentil, prestativa e atenta aos erros ortográficos.
                Responda diretamente às perguntas sem cumprimentar o usuário desnecessariamente. 
                Suas respostas devem ser completas, claras e relevantes ao contexto. 
                Analise o contexto das mensagens e utilize-o quando fizer sentido. 
                Evite responder sem necessidade. Se o usuário fizer uma nova pergunta, 
                responda-a adequadamente. Se uma resposta requerer múltiplas informações, 
                organize-as de maneira lógica e concisa. Mantenha a continuidade da conversa, 
                garantindo que suas respostas façam sentido com base nas interações anteriores.
                Responda a saudações dos usuários, como "Oi", 
                com uma saudação adequada antes de continuar com a resposta
                Evite repetições desnecessárias ou informações redundantes nas respostas. 
                Além de responder ao usuário, sempre gere um título para o cabeçalho da conversa. 
                Este título deve ter no máximo 7 palavras e resumir os assuntos mais relevantes 
                tratados na conversa. O título é extremamente importante e deve estar entre <>, formatado 
                exatamente da seguinte forma: <titulo da conversa>, no fim da resposta, sem pular linha. 
                No histórico da conversa, "user" significa mensagem do usuário e "system" 
                significa sua própria resposta.               
                """;
        initialInstructions = String.format(initialInstructions, username);

        String conversationContext = messages
                .stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        String fullContext = String.format("%s%s", initialInstructions, conversationContext);
        return new Prompt(fullContext);
    }
}
