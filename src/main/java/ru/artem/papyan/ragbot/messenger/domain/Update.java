package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Update(
        @JsonProperty(value = "update_id", required = true) long updateId,
        @JsonProperty("message") Message message
) {
    public static TypeReference<List<Update>> responseTypeReference = new TypeReference<>() {
    };
}
