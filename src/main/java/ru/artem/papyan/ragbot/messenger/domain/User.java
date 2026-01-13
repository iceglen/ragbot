package ru.artem.papyan.ragbot.messenger.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record User(
        @JsonProperty(value = "id", required = true) long id,
        @JsonProperty(value = "is_bot", required = true) boolean isBot,
        @JsonProperty(value = "first_name", required = true) String firstName,
        @JsonProperty(value = "last_name") String lastName,
        @JsonProperty(value = "username") String username,
        @JsonProperty(value = "language_code") String languageCode,
        @JsonProperty(value = "is_premium") Boolean isPremium,
        @JsonProperty(value = "added_to_attachment_menu") Boolean addedToAttachmentMenu,
        @JsonProperty(value = "can_join_groups") Boolean canJoinGroups,
        @JsonProperty(value = "can_read_all_group_messages") Boolean canReadAllGroupMessages,
        @JsonProperty(value = "supports_inline_queries") Boolean supportsInlineQueries,
        @JsonProperty(value = "can_connect_to_business") Boolean canConnectToBusiness,
        @JsonProperty(value = "has_main_web_app") Boolean hasMainWebApp
) {
}
