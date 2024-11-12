# Maya Server

## Visão Geral
Bem-vindo ao repositório **maya-server**! Este projeto é uma API desenvolvida inteiramente em Java, projetada para possibilitar a interação de usuários com o Chat Bot chamado Maya. O objetivo desta API é fornecer endpoints de conversação com a Maya, a fim de sanar dúvidas gerais e responder perguntas específicas que os usuários possam ter.

## Funções da API
Este projeto inclui diversos endpoints que permitem:
- Enviar mensagens
- Listar mensagens de uma conversa
- Listar todas as conversas de um usuário com o bot chamado Maya

Todas as mensagens e conversas são salvas em um banco de dados MySQL. O histórico da conversa é recuperado e inserido no prompt a cada nova mensagem do usuário. Dessa forma, o GPT-4 sempre possui o contexto adequado para maximizar a qualidade de suas respostas. Também é gerado no mesmo prompt um título para a conversa com base nos assuntos mais relevantes do histórico.

## Tecnologias Utilizadas
- **Java**: A linguagem principal usada para desenvolver este projeto.
- **Spring Boot**
- **Biblioteca da OpenAI**
- **Spring Security e JWT**
- **Spring Data JPA**
- **MySQL**
- **Azure Application Web Resource para deploy**
- **Azure Database for MySQL**
- **GitHub Actions para CI/CD**

## Instruções de Configuração
1. **Clone o repositório:**
   ```sh
   git clone https://github.com/WallisonLucas13/maya-server
   ```
2. **Navegue até o diretório do projeto:**
   ```sh
   cd maya-server
   ```
3. **Construa o projeto:** Foi utilizado Maven, mas escolha conforme sua necessidade.

## Exemplos de Uso

### Funcionalidade - Enviar Mensagem
- **Endpoint:** `/api/mensagem`
- **Método:** POST
- **Descrição:** Envia uma mensagem para a Maya.
- **Header:** `Authorization: Token JWT gerado ao realizar login ou se registrar.`
  
**Payload:**
```json
{
  "message": "Oii"
}
```

**Resposta:**
```json
{
  "conversationId": "9255c886-713a-4cf4-bc09-415627b715f7",
  "id": "1beaa2cd-fe6f-4662-ad4a-8b0a1d43ce12",
  "type": "SYSTEM",
  "message": "Oi, Wallison! Como posso ajudar você hoje?",
  "createdAt": "2024-11-12T22:32:29.469337066"
}
```

### Funcionalidade - Obter dados de uma conversa com a Maya
- **Endpoint:** `/api/conversa?conversationId={{conversationId}}`
- **Método:** GET
- **Descrição:** Busca todos os dados de uma conversa, incluindo as mensagens.
- **Header:** `Authorization: Token JWT gerado ao realizar login ou se registrar.`

**Resposta:**
```json
{
  "id": "9255c886-713a-4cf4-bc09-415627b715f7",
  "username": "Wallison",
  "title": "Saudações e Assistência",
  "messages": [
    {
      "conversationId": "9255c886-713a-4cf4-bc09-415627b715f7",
      "id": "30f5f5c0-c1f9-4a8e-b1df-af1181638264",
      "type": "USER",
      "message": "Oii",
      "createdAt": "2024-11-12T22:32:23.761059"
    },
    {
      "conversationId": "9255c886-713a-4cf4-bc09-415627b715f7",
      "id": "1beaa2cd-fe6f-4662-ad4a-8b0a1d43ce12",
      "type": "SYSTEM",
      "message": "Oi, Wallison! Como posso ajudar você hoje?",
      "createdAt": "2024-11-12T22:32:29.469337"
    }
  ],
  "createdAt": "2024-11-12T22:32:23.145538"
}
```

### Funcionalidade - Obter todas as conversas de um usuário
- **Endpoint:** `/api/conversas`
- **Método:** GET
- **Descrição:** Busca uma lista de conversas com a última mensagem enviada pelo usuário em cada uma delas.
- **Header:** `Authorization: Token JWT gerado ao realizar login ou se registrar.`

**Resposta:** Lista de conversas ordenadas pela data da última mensagem enviada em cada uma delas.

## Outras Funcionalidades
A API também conta com métodos de autenticação, onde o usuário realiza o cadastro ou faz login e recebe um token JWT válido por 2 horas. Esse token deve ser passado em todas as requisições relacionadas à regra de negócio.
