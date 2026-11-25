package com.skillgap.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.skillgap.dto.external.CreateSkillRequestDto;
import com.skillgap.dto.response.SkillResponseDto;
import com.skillgap.service.SkillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api")
@RequiredArgsConstructor 
@Tag(name = "Skills", description = "Endpoints for browsing and managing skills")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "Get skill details by ID", 
               description = "Returns full details of a single skill identified by its unique database ID")
    @GetMapping("/skills/{id}")
    public ResponseEntity<SkillResponseDto> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    
    @Operation(summary = "Add new skill with aliases to dictionary", 
           description = "Creates a new skill along with its aliases in the database and reloads the in-memory dictionary cache.")
    @PostMapping("/skills")
    public ResponseEntity<Void> addNewSkill(
        @Valid 
        @RequestBody
        CreateSkillRequestDto request
    ) {
        Long createdSkillId = skillService.addNewSkill(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(createdSkillId)
                            .toUri();
        
        return ResponseEntity.created(location).build();
    }
}
