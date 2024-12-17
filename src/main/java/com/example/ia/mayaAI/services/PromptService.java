package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.models.UserModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class PromptService {

    @Value("classpath:prompts.yml")
    private Resource promptsResource;

    public Prompt promptGenerate(
            String username,
            MessageModel userMessage,
            List<MessageModel> messages,
            String filesContextResume,
            String linksContextResume
    ) {

        String basePrompt = String
                .format(this.loadPrompt("maya-common"), username);

        String fullPrompt = "\n" +
                basePrompt +
                this.buildNewUserMessageContextToPrompt(userMessage) +
                (messages.isEmpty()
                        ? ""
                        : this.buildMessagesContextToPrompt(messages)) +
                (filesContextResume.isBlank()
                        ? ""
                        : this.buildFilesContextToPrompt(filesContextResume)) +
                (linksContextResume.isBlank()
                        ? ""
                        : this.buildLinksContextToPrompt(linksContextResume));

        return new Prompt(fullPrompt);
    }

    private String buildNewUserMessageContextToPrompt(MessageModel messageModel) {
        return String
                .format("\n\n[NOVA MENSAGEM DO USUÁRIO]\nUSER: %s", messageModel.getMessage());
    }

    private String buildMessagesContextToPrompt(List<MessageModel> messages) {
        String conversationContext = messages
                .stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        return String.format("\n\n[HISTÓRICO DE MENSAGENS]%s", conversationContext);
    }

    private String buildFilesContextToPrompt(String filesContextResume) {
        String prompt = "\n\n" +
                """
                [PROMPT DE ARQUIVOS]
                O usuário também tem opção de anexar arquivos em suas perguntas,
                Nesse caso, o sistema esta preparado para fazer o embedding do conteúdo dos arquivos,
                portanto, sempre que o usuário enviar arquivos,
                o conteúdo dos arquivos será exibido logo abaixo.
                Você deve responder o usuário da melhor forma possível
                com base no conteúdo dos arquivos e no contexto da conversa.
                
                [CONTEÚDO DOS ARQUIVOS]
                %s
                """;
        return String.format(prompt, filesContextResume);
    }

    private String buildLinksContextToPrompt(String linksContextResume) {
        String prompt = "\n\n" +
                """
                [PROMPT DE LINKS]
                O usuário também tem opção de inserir links em suas perguntas,
                Nesse caso, o sistema esta preparado para fazer o embedding do conteúdo dos links,
                portanto, sempre que o usuário enviar links na pergunta,
                o conteúdo dos links será exibido logo abaixo.
                Você deve responder o usuário da melhor forma possível
                com base no conteúdo dos links e no contexto da conversa.
                
                [CONTEÚDO DOS LINKS]
                %s
                """;
        return String.format(prompt, linksContextResume);
    }

    private String loadPrompt(String key) {
        try (InputStream inputStream = promptsResource.getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            return (String) ((Map<String, Object>) data.get("prompts")).get(key);
        } catch (Exception e) {
            log.error("Failed to load prompt from YAML file", e);
            throw new RuntimeException("Failed to load prompt from YAML file", e);
        }
    }
}
