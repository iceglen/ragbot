package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Updates(
        @JsonProperty("ok") boolean isOk,
        @JsonProperty("result") List<Update> result
) {
    public static TypeReference<Updates> responseTypeReference = new TypeReference<>() {
    };

    public static Updates emptyUpdates() {
        return new Updates(false, Collections.emptyList());
    }
}
