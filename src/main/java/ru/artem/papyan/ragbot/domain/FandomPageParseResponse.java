package ru.artem.papyan.ragbot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FandomPageParseResponse(
        @JsonProperty("parse")
        Parse parse
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parse(
            @JsonProperty("title")
            String title,
            @JsonProperty("pageid")
            Long pageId,
            @JsonProperty("revid")
            Long revId,
            @JsonProperty("text")
            Text text,
            @JsonProperty("langlinks")
            List<LangLink> langLinks,
            @JsonProperty("categories")
            List<Category> categories,
            @JsonProperty("links")
            List<Link> links,
            @JsonProperty("templates")
            List<Template> templates,
            @JsonProperty("images")
            List<String> images,
            @JsonProperty("externallinks")
            List<String> externalLinks,
            @JsonProperty("sections")
            List<Section> sections,
            @JsonProperty("showtoc")
            String showToc,
            @JsonProperty("parsewarnings")
            List<String> parseWarnings,
            @JsonProperty("displaytitle")
            String displayTitle,
            @JsonProperty("iwlinks")
            List<IwLink> iwLinks,
            @JsonProperty("properties")
            List<Property> properties
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LangLink(
            @JsonProperty("lang")
            String lang,
            @JsonProperty("url")
            String url,
            @JsonProperty("langname")
            String langName,
            @JsonProperty("autonym")
            String autonym,
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(
            @JsonProperty("sortkey")
            String sortKey,
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Link(
            @JsonProperty("ns")
            Integer ns,
            @JsonProperty("exists")
            String exists,
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Template(
            @JsonProperty("ns")
            Integer ns,
            @JsonProperty("exists")
            String exists,
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Section(
            @JsonProperty("toclevel")
            Integer tocLevel,
            @JsonProperty("level")
            String level,
            @JsonProperty("line")
            String line,
            @JsonProperty("number")
            String number,
            @JsonProperty("index")
            String index,
            @JsonProperty("fromtitle")
            String fromTitle,
            @JsonProperty("byteoffset")
            Long byteOffset,
            @JsonProperty("anchor")
            String anchor,
            @JsonProperty("linkAnchor")
            String linkAnchor
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IwLink(
            // Assuming similar structure to Link, but empty in example
            @JsonProperty("ns")
            Integer ns,
            @JsonProperty("exists")
            String exists,
            @JsonProperty("*")
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Property(
            @JsonProperty("name")
            String name,
            @JsonProperty("*")
            String content
    ) {
    }
}