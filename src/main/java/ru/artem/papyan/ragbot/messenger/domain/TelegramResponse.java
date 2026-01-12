package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramResponse(
        @JsonProperty("ok") boolean isOk,
        @JsonProperty("result") Map<String, ?> result,
        @JsonProperty("error_code") String errorCode,
        @JsonProperty("description") String description
) {

    public static TelegramResponse empty() {
        return new TelegramResponse(false, null, null, null);
    }

    public static TypeReference<TelegramResponse> typeReference() {
        return new TypeReference<>() {
        };
    }
}
