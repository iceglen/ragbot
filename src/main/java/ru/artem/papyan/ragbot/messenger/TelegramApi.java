package ru.artem.papyan.ragbot.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.artem.papyan.ragbot.messenger.domain.TelegramResponse;
import ru.artem.papyan.ragbot.messenger.domain.Updates;

@Slf4j
public final class TelegramApi {

    private static TelegramApi instance;

    public TelegramApi(String accessToken, int updateLimit, int updateTimeout) {
        this.baseUrl = String.format("https://api.telegram.org/bot%s", accessToken);
        this.updateLimit = updateLimit;
        this.updateTimeout = updateTimeout;
    }

    public static TelegramApi getInstance(String accessToken, int updateLimit, int updateTimeout) {
        if (instance == null) {
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalStateException("ACCESS_TOKEN environment variable is not set or empty");
            }

            instance = new TelegramApi(accessToken, updateLimit, updateTimeout);
        }

        return instance;
    }

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final String baseUrl;

    private final int updateLimit;
    private final int updateTimeout;

    private Updates getUpdates(long offset) {
        var urlBuilder = new StringBuilder(baseUrl);

        urlBuilder.append("/getUpdates?");
        urlBuilder.append("limit=").append(updateLimit);
        urlBuilder.append("&timeout=").append(updateTimeout);

        if (offset > 0) {
            urlBuilder.append("&offset=").append(offset);
        }

        urlBuilder.append("&allowed_updates=").append("[\"message\"]");

        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            Updates updates = mapper.readValue(body, Updates.responseTypeReference);
            return updates.isOk() ? updates : Updates.emptyUpdates();
        } catch (Exception e) {
            log.error("failed to fetch updates: {}", e.getMessage());
            return Updates.emptyUpdates();
        }
    }

    public void sendMessage(long userId, long chatId, String message) {
        String quotedMessage = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "%0A");

        String urlBuilder = baseUrl + "/sendMessage?" +
                "chat_id=" + chatId +
                "&parse_mode=HTML" +
                "&text=" + quotedMessage;

        Request request = new Request.Builder()
                .url(urlBuilder)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            TelegramResponse msgResp = mapper.readValue(body, TelegramResponse.typeReference());
            log.info("message sent: {}", msgResp);
        } catch (Exception e) {
            log.error("error while sending message: {}", e.getMessage());
        }
    }
}
