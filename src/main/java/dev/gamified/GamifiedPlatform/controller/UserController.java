package dev.gamified.GamifiedPlatform.controller;

import dev.gamified.GamifiedPlatform.dtos.request.DeleteUserRequest;
import dev.gamified.GamifiedPlatform.dtos.request.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.dtos.request.UserUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.services.user.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(updateUser.execute(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(getUserById.execute(id));
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(getUserByUsername.execute(username));
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserChangePasswordRequest request) {
        changePasswordService.execute(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_profile:delete')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @Valid @RequestBody DeleteUserRequest request) {
        deleteUser.execute(id, request.password());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
