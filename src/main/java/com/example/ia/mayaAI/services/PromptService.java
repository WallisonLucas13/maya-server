package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.ConversationModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class PromptService {
    public static Prompt promptGenerateWithInstructions(ConversationModel conversationModel){
        String initialInstructions = """
                Você é uma Inteligência Artifical chamada Maya
                e está presente em um chatbot para responder perguntas de usuários.
                Estarei sempre enviando um contexto de mensagens anteriores
                para que você possa responder de forma mais precisa.
                Segue abaixo o contexto da conversa, lembrando que User significa
                mensagem do usuário e System significa uma resposta sua anterior
                Aqui está o username do usuário: %s
                Use esse contexto para responder de forma mais precisa.
                Não precisa especificar System na resposta.
                Tente sempre ser gentil, prestativa e tome cuidado com erros ortográficos.
                Além de responder o usuário, você deve me retornar um titulo 
                para que eu possa definir no cabeçalho da conversa, esse titulo
                pode ter no máximo 10 palavras e ele precisa resumir os assuntos mais relevantes
                tratados na conversa, preciso que ele venha formatado da seguinte forma:
                <titulo da conversa>, no fim da resposta sem pular linha,
                para que eu possa extrair. Lembrando que vc terá todo o histórico da conversa para
                ser mais precisa.
                """;
        initialInstructions = String.format(initialInstructions, conversationModel.getUsername());

        String conversationContext = conversationModel
                .getMessages().stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        String fullContext = String.format("%s%s", initialInstructions, conversationContext);
        return new Prompt(fullContext);
    }
}
