package com.skillgap.service.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.skillgap.integration.common.JobOfferDto;
import com.skillgap.integration.justjoinit.JustJoinItOfferMapper;
import com.skillgap.integration.justjoinit.JustJoinItResponseWrapper;
import com.skillgap.integration.justjoinit.JustJoinOfferDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JustJoinItOffersProvider implements JobOffersProvider {

    private final RestClient restClient;
    private final JustJoinItOfferMapper mapper;

    public JustJoinItOffersProvider(
                @Qualifier("justJoinItRestClient") RestClient justJoinItRestclient,
                JustJoinItOfferMapper justJoinItOfferMapper) {
        this.restClient = justJoinItRestclient;
        this.mapper = justJoinItOfferMapper;
    }

    @Override
    public List<JobOfferDto> fetchAll() {

        log.info("Starting to fetch all offers from JustJoinIt ...");

        List<JustJoinOfferDto> allOffers = new ArrayList<>();
        Integer nextCursor = null;

        do {
            final Integer currentCursor = nextCursor;

            try {
                log.debug("Sending request to JustJoinIt for cursor: {}", currentCursor);

                JustJoinItResponseWrapper response = getSingleBatch(currentCursor);
                
                if (!isResponseValid(response)) {
                    log.warn("Invalid or empty response received. Stopping fetch.");
                    break;
                }

                allOffers.addAll(response.data());

                nextCursor = calculateNextCursor(currentCursor, response);

                Thread.sleep(300);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Fetching stopped.");
                break;
            } catch (Exception e) {
                log.error("Error during fetching offers from JustJoinIt: {}", e.getMessage(), e);
                break; 
            }

        } while (nextCursor != null);

        log.info("Successfully fetched {} offers from JustJoinIt provider", allOffers.size());

        return mapper.toCommonDtoList(allOffers);

    }

    private Integer calculateNextCursor(Integer currentCursor, JustJoinItResponseWrapper response) {
        
        if (response.meta() == null || response.meta().next() == null) {
            return null;
        }
        
        Integer nextCursor = response.meta().next().cursor();
        Integer totalItems = response.meta().totalItems();

        if (!isNextCursorValid(nextCursor, currentCursor, totalItems)) {
            return null;
        }

        return nextCursor;
    }

    private boolean isNextCursorValid(Integer nextCursor, Integer currentCursor, Integer totalItems) {
        
        if (totalItems != null && nextCursor >= totalItems) {
            log.info("Next cursor ({}) reaches or exceeds totalItems ({}). Stopping the fetch process.", 
                                nextCursor, totalItems);
            return false;
        }
        
        if (totalItems != null && nextCursor.equals(currentCursor)) {
            log.info("Cursor value has not changed ({}). Stopping fetch to prevent an infinite loop.", 
                                nextCursor);
            return false;
        }

        return true;
    }

    private boolean isResponseValid(JustJoinItResponseWrapper response) {
        if (response == null || response.data() == null || response.data().isEmpty()) {
            return false;
        }
        return true;
    }

    private JustJoinItResponseWrapper getSingleBatch(Integer currentCursor) {

        return restClient.get()
                        .uri(uriBuilder -> {
                            if (currentCursor != null) {
                                uriBuilder.queryParam("from", currentCursor);
                            }
                            log.debug("Sending request for endpoint: {}", uriBuilder.build());
                            return uriBuilder.build();
                        })
                        .retrieve()
                        .body(JustJoinItResponseWrapper.class);
    }

    @Override
    public List<JobOfferDto> fetchByCity(String city) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchByCity'");
    }

}
