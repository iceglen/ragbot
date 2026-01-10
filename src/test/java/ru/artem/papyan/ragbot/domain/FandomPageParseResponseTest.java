package ru.artem.papyan.ragbot.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FandomPageParseResponseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldDeserializeJson() throws Exception {
        String json = """
                {
                  "parse": {
                    "title": "10th Hunger Games",
                    "pageid": 539250,
                    "revid": 967748,
                    "text": {
                      "*": "<div class=\\"mw-content-ltr mw-parser-output\\" lang=\\"en\\" dir=\\"ltr\\"><p>The <b>10th Hunger Games</b> was the first Hunger Games to introduce mentors.</p></div>"
                    },
                    "langlinks": [
                      {
                        "lang": "de",
                        "url": "https://dietributevonpanem.fandom.com/wiki/10._Hungerspiele",
                        "langname": "German",
                        "autonym": "Deutsch",
                        "*": "10. Hungerspiele"
                      }
                    ],
                    "categories": [
                      {
                        "sortkey": "",
                        "*": "Needs_help"
                      },
                      {
                        "sortkey": "",
                        "*": "Games"
                      }
                    ],
                    "links": [
                      {
                        "ns": 0,
                        "exists": "",
                        "*": "11th Hunger Games"
                      }
                    ],
                    "templates": [
                      {
                        "ns": 10,
                        "exists": "",
                        "*": "Template:Games"
                      }
                    ],
                    "images": [
                      "10th_Hunger_Games_Logo.png"
                    ],
                    "externallinks": [
                      "https://www.thewrap.com/hunger-games-ballad-of-songbirds-and-snakes-snow-evil-michael-lesslie-interview/"
                    ],
                    "sections": [
                      {
                        "toclevel": 1,
                        "level": "2",
                        "line": "Tributes",
                        "number": "1",
                        "index": "1",
                        "fromtitle": "10th_Hunger_Games",
                        "byteoffset": 1122,
                        "anchor": "Tributes",
                        "linkAnchor": "Tributes"
                      }
                    ],
                    "showtoc": "",
                    "parsewarnings": [],
                    "displaytitle": "<span class=\\"mw-page-title-main\\">10th Hunger Games</span>",
                    "iwlinks": [],
                    "properties": [
                      {
                        "name": "infoboxes",
                        "*": "[{\\"parser_tag_version\\":5}]"
                      }
                    ]
                  }
                }
                """;

        FandomPageParseResponse response = MAPPER.readValue(json, FandomPageParseResponse.class);

        assertNotNull(response);
        FandomPageParseResponse.Parse parse = response.parse();
        assertNotNull(parse);

        assertEquals("10th Hunger Games", parse.title());
        assertEquals(539250L, parse.pageId());
        assertEquals(967748L, parse.revId());

        FandomPageParseResponse.Text text = parse.text();
        assertNotNull(text);
        assertTrue(text.content().contains("10th Hunger Games"));
        assertTrue(text.content().contains("mentors"));

        List<FandomPageParseResponse.LangLink> langLinks = parse.langLinks();
        assertNotNull(langLinks);
        assertEquals(1, langLinks.size());
        FandomPageParseResponse.LangLink langLink = langLinks.get(0);
        assertEquals("de", langLink.lang());
        assertEquals("https://dietributevonpanem.fandom.com/wiki/10._Hungerspiele", langLink.url());
        assertEquals("German", langLink.langName());
        assertEquals("Deutsch", langLink.autonym());
        assertEquals("10. Hungerspiele", langLink.content());

        List<FandomPageParseResponse.Category> categories = parse.categories();
        assertNotNull(categories);
        assertEquals(2, categories.size());
        FandomPageParseResponse.Category firstCategory = categories.get(0);
        assertEquals("", firstCategory.sortKey());
        assertEquals("Needs_help", firstCategory.content());

        List<FandomPageParseResponse.Link> links = parse.links();
        assertNotNull(links);
        assertEquals(1, links.size());
        FandomPageParseResponse.Link link = links.get(0);
        assertEquals(0, link.ns());
        assertEquals("", link.exists());
        assertEquals("11th Hunger Games", link.content());

        List<FandomPageParseResponse.Template> templates = parse.templates();
        assertNotNull(templates);
        assertEquals(1, templates.size());
        FandomPageParseResponse.Template template = templates.get(0);
        assertEquals(10, template.ns());
        assertEquals("", template.exists());
        assertEquals("Template:Games", template.content());

        List<String> images = parse.images();
        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals("10th_Hunger_Games_Logo.png", images.get(0));

        List<String> externalLinks = parse.externalLinks();
        assertNotNull(externalLinks);
        assertEquals(1, externalLinks.size());
        assertTrue(externalLinks.get(0).contains("thewrap.com"));

        List<FandomPageParseResponse.Section> sections = parse.sections();
        assertNotNull(sections);
        assertEquals(1, sections.size());
        FandomPageParseResponse.Section section = sections.get(0);
        assertEquals(1, section.tocLevel());
        assertEquals("2", section.level());
        assertEquals("Tributes", section.line());
        assertEquals("1", section.number());
        assertEquals("1", section.index());
        assertEquals("10th_Hunger_Games", section.fromTitle());
        assertEquals(1122L, section.byteOffset());
        assertEquals("Tributes", section.anchor());
        assertEquals("Tributes", section.linkAnchor());

        assertEquals("", parse.showToc());
        assertNotNull(parse.parseWarnings());
        assertTrue(parse.parseWarnings().isEmpty());
        assertEquals("<span class=\"mw-page-title-main\">10th Hunger Games</span>", parse.displayTitle());
        assertNotNull(parse.iwLinks());
        assertTrue(parse.iwLinks().isEmpty());

        List<FandomPageParseResponse.Property> properties = parse.properties();
        assertNotNull(properties);
        assertEquals(1, properties.size());
        FandomPageParseResponse.Property property = properties.get(0);
        assertEquals("infoboxes", property.name());
        assertTrue(property.content().contains("parser_tag_version"));
    }
}