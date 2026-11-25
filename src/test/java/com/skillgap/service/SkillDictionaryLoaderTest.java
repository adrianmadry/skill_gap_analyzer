package com.skillgap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgap.dao.SkillAliasRepository;
import com.skillgap.dao.SkillRepository;

@ExtendWith(MockitoExtension.class)
public class SkillDictionaryLoaderTest {

    private SkillDictionaryLoader skillDictionaryLoader;
    private ObjectMapper objectMapper;

    @Mock 
    private SkillAliasRepository skillAliasRepository;

    @Mock
    private SkillRepository skillRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        skillDictionaryLoader = new SkillDictionaryLoader(objectMapper, skillAliasRepository, skillRepository);
    }

    @Test
    @DisplayName("Should load dictionary from file")
    void shouldLoadDictionary() {
        // When
        skillDictionaryLoader.init();

        // Then
        Map<String, String> dict = skillDictionaryLoader.getDictionary();

        assertThat(dict).isNotEmpty();
    }

    @Test
    @DisplayName("Should correctly load and normalize skills from JSON file")
    void shouldLoadAndNormalizeDictionary() {
        // When
        Map<String, String> result = skillDictionaryLoader.loadAndNormalize();

        // Then
        assertThat(result).containsEntry("java 17", "Java")
                            .containsEntry("jdk", "Java")
                            .containsEntry("java", "Java")
                            .containsEntry("py", "Python")
                            .containsEntry("python", "Python");
    }

    @Test
    @DisplayName("Should normalize aliases to lowercase")
    void shouldNormalizeAliasToLowercase() {
        // When
        Map<String, String> result = skillDictionaryLoader.loadAndNormalize();

        // Then
        assertThat(result).containsKey("python3");
        assertThat(result).doesNotContainKey("PYTHON3");                          
    }

    @Test
    @DisplayName("Should handle case when file to import is missing")
    void shouldHandleMissingFile() throws Exception {
        // Given
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        SkillDictionaryLoader loaderWithMissingFile = new SkillDictionaryLoader(mockMapper, skillAliasRepository, skillRepository);

        when(mockMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                            .thenThrow(new IllegalArgumentException("there is no file to import"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loaderWithMissingFile.init();
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to read JSON file");
        assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("Should handle case while to import is empty")
    void shouldHandleEmptyDictionary() throws Exception {
        // Given
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        SkillDictionaryLoader loaderWithEmptyFile= new SkillDictionaryLoader(mockMapper, skillAliasRepository, skillRepository);

        when(mockMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                            .thenReturn(new HashMap());
        
        // When
        loaderWithEmptyFile.init();

        // Then
        Map<String, String> dict = loaderWithEmptyFile.getDictionary();

        assertThat(dict).isEmpty();
    }
}
