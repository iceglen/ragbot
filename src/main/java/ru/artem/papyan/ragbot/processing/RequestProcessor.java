package ru.artem.papyan.ragbot.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.domain.UserSearchRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestProcessor {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private static final String SYSTEM_PROMPT = """
            You're an assistant who thinks first and then answers. Always write down your steps.
            """;

    public String processRequest(UserSearchRequest request) {
        ChatResponse response = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .user(request.query())
                .call()
                .chatResponse();

        return response.getResult().getOutput().getText();
    }
}
