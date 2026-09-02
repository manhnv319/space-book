package com.velstrong.bookstore.infrastructure.adapter.in.rest.user;

import com.velstrong.bookstore.application.command.user.*;
import com.velstrong.bookstore.application.response.user.UserResponse;
import com.velstrong.bookstore.domain.port.in.user.*;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase,
                          UpdateProfileUseCase updateProfileUseCase,
                          ChangePasswordUseCase changePasswordUseCase,
                          ForgotPasswordUseCase forgotPasswordUseCase,
                          ResetPasswordUseCase resetPasswordUseCase,
                          VerifyEmailUseCase verifyEmailUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                registerUserUseCase.register(request.toCommand())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(getCurrentUserUseCase.getCurrentUser(currentUserId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestAttribute Long currentUserId,
                                                                    @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                updateProfileUseCase.updateProfile(request.toCommand(currentUserId))));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestAttribute Long currentUserId,
                                                             @Valid @RequestBody ChangePasswordRequest request) {
        changePasswordUseCase.changePassword(request.toCommand(currentUserId));
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordUseCase.forgotPassword(new ForgotPasswordCommand(request.email()));
        return ResponseEntity.ok(ApiResponse.success("If the email belongs to an account, a verification code has been sent", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(new ResetPasswordCommand(request.email(), request.otp(), request.newPassword()));
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        verifyEmailUseCase.verifyEmail(new VerifyEmailCommand(request.email(), request.otp()));
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }
}
