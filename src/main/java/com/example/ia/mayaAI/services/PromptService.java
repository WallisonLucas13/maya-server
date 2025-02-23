package com.example.ia.mayaAI.services;

import com.example.ia.mayaAI.models.MessageModel;
import com.example.ia.mayaAI.models.UserModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IndexService indexService;

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
                (messages.isEmpty()
                        ? ""
                        : this.buildMessagesContextToPrompt(messages)) +
                (filesContextResume.isBlank()
                        ? ""
                        : this.buildFilesContextToPrompt(filesContextResume)) +
                (linksContextResume.isBlank()
                        ? ""
                        : this.buildLinksContextToPrompt(linksContextResume)) +
                this.buildNewUserMessageContextToPrompt(userMessage);

        return new Prompt(fullPrompt);
    }

    private String buildNewUserMessageContextToPrompt(MessageModel messageModel) {
        return String
                .format("\n[NOVA MENSAGEM DO USUÁRIO]\nUSER: %s", messageModel.getMessage());
    }

    private String buildMessagesContextToPrompt(List<MessageModel> messages) {
        String conversationContext = messages
                .stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);

        String tokenizedContext = indexService.tokenize(conversationContext);
        return String.format("\n\n[HISTÓRICO DE MENSAGENS]%s", tokenizedContext);
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
                ### **Importante**:
                - **Você não deve dizer que não é capaz de acessar links, você possui o conteúdo da página, portanto use-o para responder o usuário**
                
                [CONTEÚDO DOS LINKS]
                %s
                """;
        return String.format(prompt, linksContextResume);
    }

    public Prompt buildTitlePromptFromConversationHistory(List<MessageModel> messages) {
        String conversationContext = generateMessagesResume(messages);

        String prompt = """
                Você é uma ferramenta de IA responsável por ler o histórico de mensagens de uma conversa de um usuáio com um chatBot e **definir** um titulo adequado baseado no conteúdo das mensagens.
                Este título DEVE SEMPRE atender aos seguintes requisitos:
                  - **NUNCA**, em hipótese alguma, conter trechos de código, elementos técnicos ou linguagens específicas;
                  - Ter no máximo **7 palavras**;
                  - Resumir os assuntos mais relevantes tratados na conversa;
                  - Focar em palavras simples e descritivas relacionadas ao contexto da conversa;
                  - Ser descritivo o suficiente para que qualquer pessoa possa entender os temas discutidos.
                ### **Importante**:
                No titulo da conversa:
                    - **NÃO** deve conter trechos de código, elementos técnicos ou linguagens específicas;
                    - **NÃO** deve conter informações sensíveis ou confidenciais;
                    - **NÃO** deve conter informações pessoais ou que possam identificar alguém.
                Segue abaixo o histório de mensagens da conversa:
                
                [HISTÓRICO DE MENSAGENS]
                %s
                """;
        return new Prompt(String.format(prompt, conversationContext));
    }

    private String generateMessagesResume(List<MessageModel> messages) {
        return messages
                .stream()
                .map(message -> message.getType() + ": " + message.getMessage())
                .reduce("", (a, b) -> a + "\n" + b);
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
