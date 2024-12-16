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

    public Prompt promptWithFilesContextGenerate(
            List<MessageModel> messages,
            String username,
            String filesContextResume
    ) {
        String initialInstructions = String
                .format(
                        this.loadPrompt("maya-common"),
                        username,
                        this.promptToContextByFiles(filesContextResume)
                );

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

    private String promptToContextByFiles(String filesContextResume) {
        String prompt =
                """
                \nO usuário também tem opção de anexar arquivos em suas perguntas,
                 Nesse caso, o sistema esta preparado para fazer o embedding do conteúdo dos arquivos,
                 portanto, sempre que o usuário enviar arquivos,
                 o conteúdo dos arquivos será exibido logo abaixo.
                 Você deve responder o usuário da melhor forma possível
                 com base no conteúdo dos arquivos e no contexto da conversa.
                 [CONTEÚDO DOS ARQUIVOS]\n
                 %s
                """;
        return String.format(prompt, filesContextResume);
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
