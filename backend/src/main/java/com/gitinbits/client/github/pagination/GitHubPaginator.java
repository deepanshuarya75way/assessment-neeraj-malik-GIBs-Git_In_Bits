package com.gitinbits.client.github.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Reusable GitHub pagination engine.
 *
 * <p>GitHub paginates list endpoints by default at 30 items per page.
 * It signals additional pages via the {@code Link} response header:
 * <pre>
 *   Link: &lt;https://api.github.com/orgs/my-org/repos?page=2&gt;; rel="next",
 *         &lt;https://api.github.com/orgs/my-org/repos?page=5&gt;; rel="last"
 * </pre>
 *
 * <p>This class transparently follows the {@code rel="next"} chain until
 * all pages are exhausted, returning a single aggregated list.
 *
 * <p>All paginated GitHub client methods must route through
 * {@link #fetchAll(RestClient, URI, String, ParameterizedTypeReference)}
 * to ensure consistent page handling across the entire API surface.
 */
@Component
public class GitHubPaginator {

    private static final Logger log = LoggerFactory.getLogger(GitHubPaginator.class);
    private static final int DEFAULT_PAGE_SIZE = 100;

    /**
     * Fetches all pages of a GitHub list endpoint and returns the aggregated result.
     *
     * @param restClient   the pre-configured GitHub RestClient
     * @param initialUri   absolute URI of the first page (including query params)
     * @param bearerToken  OAuth2 access token for Authorization header
     * @param responseType type reference for deserializing list items
     * @param <T>          type of individual list items
     * @return complete list of all items across all pages; empty list if body is null/empty
     */
    public <T> List<T> fetchAll(
            RestClient restClient,
            URI initialUri,
            String bearerToken,
            ParameterizedTypeReference<List<T>> responseType) {

        // Enforce maximum page size for the initial request
        URI optimizedUri = UriComponentsBuilder.fromUri(initialUri)
                .replaceQueryParam("per_page", DEFAULT_PAGE_SIZE)
                .build(true)
                .toUri();

        List<T> allItems = new ArrayList<>();
        URI nextUri = optimizedUri;
        int pageNumber = 0;
        int MAX_PAGES = 5;

        while (nextUri != null && pageNumber < MAX_PAGES) {
            pageNumber++;
            log.debug("Fetching page {} (per_page={}) → {}", pageNumber, DEFAULT_PAGE_SIZE, nextUri);

            ResponseEntity<List<T>> response = restClient.get()
                    .uri(nextUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .toEntity(responseType);

            List<T> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                allItems.addAll(body);
                log.debug("Page {} returned {} items (running total: {})",
                        pageNumber, body.size(), allItems.size());
            } else {
                log.debug("Page {} returned no items — stopping pagination", pageNumber);
                break;
            }

            nextUri = extractNextUri(response.getHeaders().getFirst(HttpHeaders.LINK));
        }

        log.debug("Pagination complete: {} page(s), {} total items from {}",
                pageNumber, allItems.size(), optimizedUri);
        return allItems;
    }

    /**
     * Parses GitHub's {@code Link} header and extracts the {@code rel="next"} URL.
     *
     * <p>Header format:
     * {@code <https://...?page=2>; rel="next", <https://...?page=5>; rel="last"}
     *
     * @param linkHeader raw value of the Link header, or null if absent
     * @return absolute URI of the next page, or null if there is no next page
     */
    private URI extractNextUri(String linkHeader) {
        if (linkHeader == null || linkHeader.isBlank()) {
            return null;
        }

        for (String part : linkHeader.split(",")) {
            String trimmed = part.trim();
            String[] segments = trimmed.split(";", 2);
            if (segments.length == 2 && segments[1].trim().equals("rel=\"next\"")) {
                String urlPart = segments[0].trim();
                if (urlPart.startsWith("<") && urlPart.endsWith(">")) {
                    String url = urlPart.substring(1, urlPart.length() - 1);
                    log.debug("Next page URL extracted from Link header: {}", url);
                    return URI.create(url);
                }
            }
        }

        return null; // No rel="next" found; this was the last page
    }
}
