package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        @JsonProperty(value = "message_id", required = true)
        long messageId,

        @JsonProperty("from")
        User from,

        @JsonProperty("sender_chat")
        Chat senderChat,

        @JsonProperty(value = "date", required = true)
        long date,

        @JsonProperty(value = "chat", required = true)
        Chat chat,

        @JsonProperty("text")
        String text
) {
}
