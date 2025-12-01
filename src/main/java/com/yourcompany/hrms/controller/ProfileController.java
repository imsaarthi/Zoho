package com.yourcompany.hrms.controller;

import com.yourcompany.hrms.dto.ProfilePhotoResponse;
import com.yourcompany.hrms.dto.ProfilePhotoUploadRequest;
import com.yourcompany.hrms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @PostMapping(value = "/{id}/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'HR') or #id == authentication.principal.id")
    public ResponseEntity<ProfilePhotoResponse> uploadProfilePhoto(
            @PathVariable Long id,
            @ModelAttribute @Valid ProfilePhotoUploadRequest request) {
        return ResponseEntity.ok(userService.updateProfilePhoto(id, request.getFile()));
    }
}
