package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.MessageModel;
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

    public Prompt promptGenerate(List<MessageModel> messages, String username) {
        String initialInstructions = String
                .format(this.loadPrompt("maya-common"), username, "");

        String conversationContext = messages
                .stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        String fullContext = String.format("%s%s", initialInstructions, conversationContext);
        return new Prompt(fullContext);
    }

    public Prompt promptWithLinksGenerate(
            List<MessageModel> messages,
            String username,
            String linksContextResume
    ) {
        String initialInstructions = String
                .format(
                        this.loadPrompt("maya-common"),
                        username,
                        this.promptToContextByLinks(linksContextResume)
                );

        String conversationContext = messages
                .stream()
                .map(message -> message.getType() + ":\n" + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        String fullContext = String.format("%s%s", initialInstructions, conversationContext);
        return new Prompt(fullContext);
    }

    public Prompt promptHtmlInterpreterGenerate(LinkedHashMap<String, String> linksContextMap){
        String html = linksContextMap
                .entrySet()
                .stream()
                .map(entry ->
                        "[LINK]: " + entry.getKey() + "\n[HTML CONTENT]:\n" + entry.getValue() + "\n[FIM]"
                )
                .reduce("", (a, b) -> a + "\n" + b);

        String interpreter = String.format(this.loadPrompt("html-interpreter"), html);
        return new Prompt(interpreter);
    }

    private String promptToContextByLinks(String linksContextResume) {
        String prompt = """
                \nAlém desse contexto da conversa, sempre que o usuário envolver links em suas perguntas,
                    vou colocar logo abaixo o conteúdo de cada link para que você
                    consiga ler e responder o usuário de forma mais precisa, portanto não precisa se preocupar
                    em buscar páginas na internet para responder o usuário em relação a links.
                    Sempre que lhe for solicitado algum trabalho na web em relação a links,
                    evite dizer que você não pode fazer isso, pois estou te dando o contexto.
                    Caso o conteúdo da página seja algum tipo de erro como 404 etc...
                    Apenas ignore sem reclamar pro usuário.
                    Segue abaixo o conteúdo dos links que o usuário enviou:
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
