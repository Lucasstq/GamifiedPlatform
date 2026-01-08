package dev.gamified.GamifiedPlatform.controller;

import dev.gamified.GamifiedPlatform.config.annotations.CanDeleteProfile;
import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.CanWriteProfile;
import dev.gamified.GamifiedPlatform.dtos.request.user.DeleteUserRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.PublicUserProfileResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.services.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UpdateUserService updateUser;
    private final UserByIdService getUserById;
    private final UserByUsernameService getUserByUsername;
    private final UserChangePasswordService changePasswordService;
    private final DeleteUserService deleteUser;
    private final GetPublicUserProfileService getPublicUserProfileService;

    @PutMapping("/{id}")
    @CanWriteProfile
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(updateUser.execute(id, request));
    }

    @GetMapping("/{id}")
    @CanReadUsers
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(getUserById.execute(id));
    }

    @GetMapping("/search")
    @CanReadUsers
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(getUserByUsername.execute(username));
    }

    @GetMapping("/{id}/profile")
    @CanReadUsers
    public ResponseEntity<PublicUserProfileResponse> getPublicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(getPublicUserProfileService.execute(id));
    }

    @PatchMapping("/{id}/change-password")
    @CanWriteProfile
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserChangePasswordRequest request) {
        changePasswordService.execute(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    @CanDeleteProfile
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @Valid @RequestBody DeleteUserRequest request) {
        deleteUser.execute(id, request.password());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
