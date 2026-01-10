package ru.artem.papyan.ragbot.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FandomPagesResponseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shouldDeserializeJson() throws Exception {
        String json = """
                {
                  "batchcomplete": "",
                  "continue": {
                    "apcontinue": "11",
                    "continue": "-||"
                  },
                  "query": {
                    "allpages": [
                      {
                        "pageid": 2086,
                        "ns": 0,
                        "title": "\\"Foxface\\""
                      },
                      {
                        "pageid": 3448,
                        "ns": 0,
                        "title": "1"
                      },
                      {
                        "pageid": 3457,
                        "ns": 0,
                        "title": "10"
                      },
                      {
                        "pageid": 539250,
                        "ns": 0,
                        "title": "10th Hunger Games"
                      },
                      {
                        "pageid": 539510,
                        "ns": 0,
                        "title": "10th Hunger Games (The Ballad Of Songbirds And Snakes)"
                      }
                    ]
                  }
                }
                """;

        FandomPagesResponse response = MAPPER.readValue(json, FandomPagesResponse.class);

        assertNotNull(response);
        assertEquals("", response.batchComplete());

        FandomPagesResponse.Continue continueField = response.continueField();
        assertNotNull(continueField);
        assertEquals("11", continueField.apContinue());
        assertEquals("-||", continueField.continueField());

        FandomPagesResponse.Query query = response.query();
        assertNotNull(query);
        List<FandomPagesResponse.PageEntry> allPages = query.allPages();
        assertNotNull(allPages);
        assertEquals(5, allPages.size());

        FandomPagesResponse.PageEntry firstPage = allPages.get(0);
        assertEquals(2086L, firstPage.pageId());
        assertEquals(0, firstPage.ns());
        assertEquals("\"Foxface\"", firstPage.title());

        FandomPagesResponse.PageEntry secondPage = allPages.get(1);
        assertEquals(3448L, secondPage.pageId());
        assertEquals(0, secondPage.ns());
        assertEquals("1", secondPage.title());
    }
}