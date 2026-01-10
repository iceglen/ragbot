package ru.artem.papyan.ragbot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FandomPagesResponse(
        @JsonProperty("batchcomplete")
        String batchComplete,
        @JsonProperty("continue")
        Continue continueField,
        @JsonProperty("query")
        Query query
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Continue(
            @JsonProperty("apcontinue")
            String apContinue,
            @JsonProperty("continue")
            String continueField
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Query(
            @JsonProperty("allpages")
            List<PageEntry> allPages
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PageEntry(
            @JsonProperty("pageid")
            Long pageId,
            @JsonProperty("ns")
            Integer ns,
            @JsonProperty("title")
            String title
    ) {
    }
}