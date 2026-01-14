package ru.artem.papyan.ragbot.messenger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.domain.UserSearchRequest;
import ru.artem.papyan.ragbot.messenger.domain.Update;
import ru.artem.papyan.ragbot.messenger.domain.Updates;
import ru.artem.papyan.ragbot.processing.RequestProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramWorker {

    private final TelegramApi api;
    private final RequestProcessor processor;

    public void process(Updates updates) {
        if (!updates.isOk()) {
            return;
        }

        for (Update update : updates.result()) {
            String userRequest = update.message().text();

            log.debug("Received user request: {}", userRequest);

            if (userRequest != null && !userRequest.isBlank()) {
                String response = processor.processRequest(
                        new UserSearchRequest(userRequest)
                );

                long chatId = update.message().chat().id();

                log.debug("Sending response to chat {}", chatId);

                api.sendMessage(chatId, response);
            }
        }
    }
}
