package com.velstrong.bookstore.infrastructure.adapter.in.rest.auth;

import com.velstrong.bookstore.application.command.auth.GetTokenCommand;
import com.velstrong.bookstore.application.command.auth.RefreshTokenCommand;
import com.velstrong.bookstore.application.response.auth.TokenResponse;
import com.velstrong.bookstore.domain.port.in.auth.GetTokenUseCase;
import com.velstrong.bookstore.domain.port.in.auth.LogoutUseCase;
import com.velstrong.bookstore.domain.port.in.auth.RefreshTokenUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final GetTokenUseCase getTokenUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(GetTokenUseCase getTokenUseCase, RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase) {
        this.getTokenUseCase = getTokenUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> getToken(@Valid @RequestBody GetTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                getTokenUseCase.getToken(new GetTokenCommand(request.username(), request.password()))));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                refreshTokenUseCase.refresh(new RefreshTokenCommand(request.refreshToken()))));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader,
                                                     @RequestAttribute Long currentUserId) {
        String token = authHeader.replace("Bearer ", "");
        logoutUseCase.logout(token, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
