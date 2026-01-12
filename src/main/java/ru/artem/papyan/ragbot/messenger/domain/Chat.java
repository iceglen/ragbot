package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Chat(
        @JsonProperty(value = "id", required = true) long id,
        @JsonProperty(value = "type", required = true) String type,
        @JsonProperty("title") String title,
        @JsonProperty("username") String username,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("is_forum") Boolean isForum
) {
}
