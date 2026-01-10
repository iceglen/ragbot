package ru.artem.papyan.ragbot.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import ru.artem.papyan.ragbot.config.EnvConfig;
import ru.artem.papyan.ragbot.domain.FandomPageParseResponse;
import ru.artem.papyan.ragbot.domain.FandomPagesResponse;
import ru.artem.papyan.ragbot.domain.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FandomScrapingService {
    private static final String FANDOM_API_URL_TEMPLATE = "https://%s.fandom.com/api.php";
    private static final String FETCH_PARAM_ACTION = "query";
    private static final String FETCH_PARAM_LIST = "allpages";
    private static final int FETCH_PARAM_APLIMIT = 500;
    private static final String PARAM_FORMAT = "json";
    private static final String PARSE_PARAM_ACTION = "parse";
    private static final String PARSE_PARAM_PAGEID = "pageid";

    private final EnvConfig envConfig;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    /**
     * Fetches all page IDs from the configured Fandom wiki.
     * Makes sequential API requests to the MediaWiki API, handling pagination via continue tokens.
     * Excludes pages whose titles contain any of the configured exclusion substrings (case-insensitive).
     *
     * @return list of page IDs as strings
     * @throws RuntimeException if any HTTP or parsing error occurs
     */
    public List<Pair<Long, String>> fetchAllPageIds() {
        String baseUrl = String.format(FANDOM_API_URL_TEMPLATE, envConfig.getFandomId());
        List<Pair<Long, String>> pageIds = new ArrayList<>();
        String apContinue = null;
        int requestCount = 0;

        do {
            requestCount++;
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder()
                    .addQueryParameter("action", FETCH_PARAM_ACTION)
                    .addQueryParameter("list", FETCH_PARAM_LIST)
                    .addQueryParameter("aplimit", String.valueOf(FETCH_PARAM_APLIMIT))
                    .addQueryParameter("format", PARAM_FORMAT);
            if (apContinue != null) {
                urlBuilder.addQueryParameter("apcontinue", apContinue);
            }
            HttpUrl url = urlBuilder.build();
            log.debug("Fetching page IDs from {} (request #{})", url, requestCount);

            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("failed to fetch pages: {} {}", response.code(), response.message());
                    return Collections.emptyList();
                }
                ResponseBody body = response.body();
                if (body == null) {
                    log.warn("empty response body");
                    return Collections.emptyList();
                }
                String responseString = body.string();
                FandomPagesResponse apiResponse = objectMapper.readValue(responseString, FandomPagesResponse.class);

                List<Pair<Long, String>> extracted = extractPageIds(apiResponse);
                pageIds.addAll(extracted);

                apContinue = extractContinueToken(apiResponse);
            } catch (Exception e) {
                throw new RuntimeException("Error while fetching page IDs", e);
            }
        } while (apContinue != null);

        log.info("Fetched {} page IDs after {} requests", pageIds.size(), requestCount);
        return pageIds.stream().distinct().toList();
    }

    /**
     * Fetches parsed page content from the configured Fandom wiki.
     * Makes a single API request to the MediaWiki parse action.
     *
     * @param pageId the page ID as a string
     * @return parsed page response, or null if the page cannot be parsed or HTTP error occurs
     * @throws RuntimeException if any JSON parsing error occurs
     */
    public FandomPageParseResponse parsePage(Long pageId) {
        String baseUrl = String.format(FANDOM_API_URL_TEMPLATE, envConfig.getFandomId());
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addQueryParameter("action", PARSE_PARAM_ACTION)
                .addQueryParameter(PARSE_PARAM_PAGEID, pageId.toString())
                .addQueryParameter("format", PARAM_FORMAT)
                .build();
        log.debug("Parsing page {} from {}", pageId, url);

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Failed to parse page {}: {} {}", pageId, response.code(), response.message());
                return null;
            }
            ResponseBody body = response.body();
            if (body == null) {
                log.warn("Empty response body for page {}", pageId);
                return null;
            }
            String responseString = body.string();
            FandomPageParseResponse parsed = objectMapper.readValue(responseString, FandomPageParseResponse.class);
            if (parsed.parse() == null) {
                log.warn("Parse field missing for page {}", pageId);
                return null;
            }
            return parsed;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing page " + pageId, e);
        }
    }

    private String extractContinueToken(FandomPagesResponse apiResponse) {
        FandomPagesResponse.Continue continueField = apiResponse.continueField();
        return (continueField != null && continueField.apContinue() != null) ? continueField.apContinue() : null;
    }

    private List<Pair<Long, String>> extractPageIds(FandomPagesResponse apiResponse) {
        List<Pair<Long, String>> result = new ArrayList<>();

        FandomPagesResponse.Query query = apiResponse.query();
        if (query != null && query.allPages() != null) {
            for (FandomPagesResponse.PageEntry entry : query.allPages()) {
                if (entry.title() == null || isTitleExcluded(entry.title())) {
                    continue;
                }

                result.add(Pair.of(entry.pageId(), entry.title()));
            }
        }

        return result;
    }

    private boolean isTitleExcluded(String title) {
        String lowerTitle = title.toLowerCase();
        List<String> exclusions = envConfig.getExclusions();
        if (exclusions == null || exclusions.isEmpty()) {
            return false;
        }

        for (String exclusion : exclusions) {
            if (lowerTitle.contains(exclusion.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
