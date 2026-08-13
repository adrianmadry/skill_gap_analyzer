package com.skillgap.service.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.skillgap.integration.common.JobOfferDto;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnapshotJobOffersProviderTest {

    private SnapshotJobOffersProvider provider;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        provider = new SnapshotJobOffersProvider(objectMapper);
    }

    @Nested
    @DisplayName("Tests for fetchAll() method")
    class fetchAllTests {

        @Test
        @DisplayName("Should correctly read and proceed JSON file to return List<JobOfferDto> object")
        void shouldReadAndTransformJson() {
            // When
            List<JobOfferDto> result = provider.fetchAll();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCity()).isEqualTo("Warszawa");
            assertThat(result.get(1).getTitle()).isEqualTo("Python Developer");
        }

        @Test
        @DisplayName("Should throw RuntimeException with custom message when JSON is malformed")
        void shouldHandleMalformedJSON() throws Exception {
            // Given
            // Mock provider with error 
            ObjectMapper mockMapper = mock(ObjectMapper.class);
            SnapshotJobOffersProvider providerWithError = new SnapshotJobOffersProvider(mockMapper);

            when(mockMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                            .thenThrow(new JsonParseException(null, "Unexpected character"));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                providerWithError.fetchAll();
            });

            assertThat(exception.getMessage()).isEqualTo("Failed to read JSON file");
            assertThat(exception.getCause()).isInstanceOf(JsonParseException.class);
        
        }

    }

    @Nested
    @DisplayName("Tests for fetchByCity() method")
    class fetchByCityTests {

        @Test
        @DisplayName("Should be case insensitive for city name")
        void shouldBeCaseInsensitive() {
            // Given
            String lowerC = "warszawa";
            String upperC = "WARSZAWA";

            // When
            List<JobOfferDto> result1 = provider.fetchByCity(lowerC);
            List<JobOfferDto> result2 = provider.fetchByCity(upperC);

            // Then
            assertThat(result1).hasSize(1);
            assertThat(result2).hasSize(1);
            assertThat(result1.get(0).getCity()).isEqualTo("Warszawa");
            assertThat(result2.get(0).getCity()).isEqualTo("Warszawa");
        }

        @Test
        @DisplayName("Should handle case when there are no offers for given city")
        void shouldHandleNoOffersForCity() {
            // Given
            String city = "Gdansk";

            // When
            List<JobOfferDto> result = provider.fetchByCity(city);

            // Then
            assertThat(result).isEmpty();
        
        }

    
    }

}
