package com.skillgap.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillgap.integration.common.JobOfferDto;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SkillExtractionServiceTest {
    
    @Mock
    private SkillDictionaryLoader skillDictionaryLoader;

    @InjectMocks
    private SkillExtractionService skillExtractionService;

    @BeforeEach
    void setUp() {
        Map<String, String> testDictionary = Map.of(
                "java", "Java",
                "jdk", "Java",
                "spring", "Spring Boot",
                "springboot", "Spring Boot",
                "js", "JavaScript",
                "ts", "TypeScript",
                "java script", "JavaScript",
                "c#", "C#",
                ".net", ".NET",
                "c++", "C++"
        );
        when(skillDictionaryLoader.getDictionary()).thenReturn(testDictionary);
        skillExtractionService.init();
    }

    @Nested
    @DisplayName("Tests for extractRequiredSkills method")
    class ExtractRequiredSkillsTests{
        
        @Test
        @DisplayName("Should extract skills from JobOfferDto and return it as official name from dictionary")
        void shouldExtractSkillsFromDto() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setRequiredSkills(List.of("JAVA", "spring", "JS"));

            // When
            Set<String> result = skillExtractionService.extractRequiredSkills(dto);
            
            // Then
            assertThat(result)
                            .hasSize(3)
                            .containsExactlyInAnyOrder("Java", "Spring Boot", "JavaScript");
        }

        @Test
        @DisplayName("Should map multiple aliases to the same official name from dictionary")
        void shouldMapMultipleAliasesToOneSkill() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setRequiredSkills(List.of("JAVA", "jdk", "java"));

            // When
            Set<String> result = skillExtractionService.extractRequiredSkills(dto);
            
            // Then
            assertThat(result)
                            .hasSize(1)
                            .containsExactlyInAnyOrder("Java");
        }

        @Test
        @DisplayName("Should track unknown skills")
        void shouldTrackUnknowSkills() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setRequiredSkills(List.of("Kafka", "JAVA", "spring", "kafka", "rest api"));

            // When
            skillExtractionService.extractRequiredSkills(dto);
            Map<String, Integer> unknown_skills = skillExtractionService.getUnknownSkillsCounter();
            
            // Then
            assertThat(unknown_skills.get("kafka")).isEqualTo(2);
            assertThat(unknown_skills).hasSize(2);
            assertThat(unknown_skills).doesNotContainKey("java");
        }
    }

    @Nested
    @DisplayName("Tests for getUnknownSkillsCounter method")
    class GetUnknownSkillsCounterTests {

        @Test
        @DisplayName("Should return unknown skills in sorted by occurences DESC")
        void shouldReturnUnknownSkillsSorted() {
            // Given
            JobOfferDto dto = new JobOfferDto();
            dto.setRequiredSkills(List.of("Python", "rest", "java", "Rest", "python", "Azure", "python"));

            // When
            skillExtractionService.extractRequiredSkills(dto);
            Map<String, Integer> unknown_skills = skillExtractionService.getUnknownSkillsCounter();
            
            // Then
            assertThat(unknown_skills.keySet()).containsExactly("python", "rest", "azure");
        }
    }

    @Nested
    @DisplayName("Tests for extractSkillsFromOfferDescription method")
    class ExtractSkillsFromOfferDescriptionTests {

        @Test
        @DisplayName("Should extract skills from text description")
        void shouldExtractSkillsFromDescription() {
            // Given
            String description = "We are looking for a JAVA developer with experience in Spring and JS. Knowledge of JDK is welcome.";

            // When
            Set<String> result =  skillExtractionService.extractSkillsFromOfferDescription(description);
            
            // Then
            assertThat(result)
                        .hasSize(3)
                        .containsExactlyInAnyOrder("Java", "Spring Boot", "JavaScript");
        }

        @Test
        @DisplayName("Should return empty set for null or blank description")
        void shouldReturnEmptyForNullOrBlank() {
            assertThat(skillExtractionService.extractSkillsFromOfferDescription(null)).isEmpty();
            assertThat(skillExtractionService.extractSkillsFromOfferDescription("  ")).isEmpty();
        }

        @Test
        @DisplayName("Should correctly handle overlapping aliases (testing buildAllSkillsPattern logic")
        void shouldHandleOverlappingAliases() {
            /*  Method should correctly handle case when 
                the alias name contains name of another alias
                ("Java Script" contains "Java")
            */

            // Given
            String description = "You need to know only Java Script to work with Us";

            // When
            Set<String> result =  skillExtractionService.extractSkillsFromOfferDescription(description);
            
            // Then
            assertThat(result)
                        .hasSize(1)
                        .containsExactlyInAnyOrder("JavaScript");
        }

    
    }

    @Nested
    @DisplayName("Tests for Special Characters and Edge Cases")
    class SpecialCharactersTests {

        @Test
        @DisplayName("Should extract skills with special characters like C#, .NET, C++")
        void shouldExtractSpecialCharSkills() {
            // Given
            String description = "Looking for C# and .NET experts. C++ experience is a plus.";

            // When
            Set<String> result =  skillExtractionService.extractSkillsFromOfferDescription(description);
            
            // Then
            assertThat(result)
                        .hasSize(3)
                        .containsExactlyInAnyOrder("C#", ".NET", "C++");
        }

        @Test
        @DisplayName("Should handle complex punctuation around special characters")
        void shouldHandlePunctuationWithSpecialChars() {
            // Given
            String description = "Skills: C#, (C++), [.NET].";

            // When
            Set<String> result =  skillExtractionService.extractSkillsFromOfferDescription(description);
            
            // Then
            assertThat(result)
                        .hasSize(3)
                        .containsExactlyInAnyOrder("C#", ".NET", "C++");
        }


    }
}

