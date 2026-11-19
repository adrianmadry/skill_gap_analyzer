package com.skillgap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillgap.dto.external.AddSkillsRequestDto;
import com.skillgap.dto.external.AdminAddSkillsRequestDto;
import com.skillgap.dto.response.UserSkillsUpdateResponseDto;
import com.skillgap.entity.AppUser;
import com.skillgap.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for browsing and managing user profiles")
public class UserProfileController {

    private final UserService userService;

    @Operation(summary = "Add list of skills to specified user", 
               description = "Allows adding skills to specified user")
    @PostMapping("/skills")
    public ResponseEntity<UserSkillsUpdateResponseDto> addSkillsForAnyUser(
        @Valid 
        @RequestBody
        AdminAddSkillsRequestDto request
    ) {
        return ResponseEntity.ok(userService.addSkills(request.userSkills(), request.userId()));
    }

    @Operation(summary = "Add list of skills to my profile", 
               description = "Adds skills to the currently authenticated user's profile")
    @PostMapping("/me/skills")
    public ResponseEntity<UserSkillsUpdateResponseDto> addMySkills(
        @Valid 
        @RequestBody
        AddSkillsRequestDto request,
        @AuthenticationPrincipal AppUser user
    ) {
        return ResponseEntity.ok(userService.addSkills(request.userSkills(), user.getId()));
    }




}
