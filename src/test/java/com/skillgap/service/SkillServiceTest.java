package com.skillgap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skillgap.dao.SkillRepository;
import com.skillgap.entity.Skill;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Nested
    @DisplayName("Tests for getFromDbOrCreate method")
    class GetFromDbOrCreateTests {

        @Test
        @DisplayName("Should return empty set when input also empty")
        void shouldReturnEmptySet() {
            // Given
            Set<String> skillNames = new HashSet<>();

            // When
            Set<Skill> result = skillService.getFromDbOrCreate(skillNames);

            // Then
            assertThat(result).isEmpty();
        }   

        @Test
        @DisplayName("Should save only missing skills and return combined set when some skills exist in DB")
        void shouldSaveOnlyMissingSkillsToDb() {
            // GIVEN
            String exsistingName = "Java";
            String newName1 = "python";
            String newName2 = "ruby";
            Set<String> skillNames = Set.of(exsistingName, newName1, newName2);

            Skill existingSkill = new Skill();
            existingSkill.setName(exsistingName);

            // Mock repo behaviour
            when(skillRepository.findAllByNameInIgnoreCase(skillNames))
                        .thenReturn(List.of(existingSkill));

            // Mock saveAll 
            when(skillRepository.saveAll(anyList()))
                        .thenAnswer(invocation -> invocation.getArgument(0));
                        
            // WHEN
            Set<Skill> result = skillService.getFromDbOrCreate(skillNames);

            // Get List that was passed to saveAll method
            ArgumentCaptor<List<Skill>> listCaptor = ArgumentCaptor.forClass(List.class);
            verify(skillRepository).saveAll(listCaptor.capture());
            List<Skill> savedSkills = listCaptor.getValue();

            // THEN
            assertThat(result).hasSize(3);
            assertThat(result.stream().map(Skill::getName).map(String::toLowerCase))
                                        .containsExactlyInAnyOrder("java", "python", "ruby");

            assertThat(savedSkills).hasSize(2);
            assertThat(savedSkills.stream().map(Skill::getName))
                                        .containsExactlyInAnyOrder("python", "ruby");
        }   
    

        @Test
        @DisplayName("All skills should be saved to DB - list passed as an argument contains only new skills")
        void shouldSaveAllSkillsToDb() {
            // GIVEN
            String newName1 = "Java";
            String newName2 = "python";
            String newName3 = "ruby";
            Set<String> skillNames = Set.of(newName1, newName2, newName3);

            // Mock repo behaviour
            when(skillRepository.findAllByNameInIgnoreCase(skillNames))
                        .thenReturn(new ArrayList<>());

            // Mock saveAll 
            when(skillRepository.saveAll(anyList()))
                        .thenAnswer(invocation -> invocation.getArgument(0));
                        
            // WHEN
            Set<Skill> result = skillService.getFromDbOrCreate(skillNames);

            // Get List that was passed to saveAll method
            ArgumentCaptor<List<Skill>> listCaptor = ArgumentCaptor.forClass(List.class);
            verify(skillRepository).saveAll(listCaptor.capture());
            List<Skill> savedSkills = listCaptor.getValue();

            // THEN
            assertThat(result).hasSize(3);
            assertThat(result.stream().map(Skill::getName).map(String::toLowerCase))
                                        .containsExactlyInAnyOrder("java", "python", "ruby");

            assertThat(savedSkills).hasSize(3);
            assertThat(savedSkills.stream().map(Skill::getName).map(String::toLowerCase))
                                        .containsExactlyInAnyOrder("java", "python", "ruby");
        }   

        @Test
        @DisplayName("None of skills should be saved to DB - list passed as an argument contains only exisitng skills")
        void shouldNotSaveAnySkillsToDb() {
            // GIVEN
            String exsistingName1 = "Java";
            String exsistingName2 = "python";
            String exsistingName3 = "ruby";
            Set<String> skillNames = Set.of(exsistingName1, exsistingName2, exsistingName3);

            Skill existingSkill1 = new Skill();
            existingSkill1.setName(exsistingName1);
            Skill existingSkill2 = new Skill();
            existingSkill2.setName(exsistingName2);
            Skill existingSkill3 = new Skill();
            existingSkill3.setName(exsistingName3);

            // Mock repo behaviour
            when(skillRepository.findAllByNameInIgnoreCase(skillNames))
                        .thenReturn(List.of(existingSkill1, existingSkill2, existingSkill3));
    
            // WHEN
            Set<Skill> result = skillService.getFromDbOrCreate(skillNames);

            // THEN
            assertThat(result).hasSize(3);
            assertThat(result.stream().map(Skill::getName).map(String::toLowerCase))
                                        .containsExactlyInAnyOrder("java", "python", "ruby");

            verify(skillRepository, never()).saveAll(anyList());
            
        }   

        @Test
        @DisplayName("Should not create duplicate skill when existing name differs only by case")
        void shouldBeCaseInsensitiveForSkillName() {
            // GIVEN
            Set<String> skillNames = Set.of("JAVA");

            // Mock repo behaviour
            Skill skillInDb = new Skill();
            skillInDb.setName("java");
            when(skillRepository.findAllByNameInIgnoreCase(skillNames))
                        .thenReturn(List.of(skillInDb));
    
            // WHEN
            Set<Skill> result = skillService.getFromDbOrCreate(skillNames);

            // THEN
            assertThat(result).hasSize(1);
            assertThat(result.stream().map(Skill::getName).map(String::toLowerCase))
                                        .containsExactlyInAnyOrder("java");

            verify(skillRepository, never()).saveAll(anyList());
            
        }   



    }
}


