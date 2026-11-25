package com.skillgap.integration.justjoinit;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for the JustJoinIT API paginated response.
 * Encapsulates the job offers payload and cursor-based pagination metadata.
 *
 * <p>Expected response structure:
 * <pre>
 * {
 * "data": [ { "guid": "...", "title": "..." } ],
 * "meta": { "totalItems": 100, "next": { "cursor": 10, "itemsCount": 10 } }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JustJoinItResponseWrapper(
    List<JustJoinOfferDto> data,
    Meta meta
) {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
        Integer from,
        Integer totalItems,
        Prev prev,
        Next next
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Prev(
        Integer cursor,
        Integer itemsCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Next(
        Integer cursor,
        Integer itemsCount
    ) {}
}
