package com.devopsuniversity.auth.infrastructure.web;

import com.devopsuniversity.auth.application.port.in.AuthResult;
import com.devopsuniversity.auth.application.port.in.LoginUseCase;
import com.devopsuniversity.auth.application.port.in.LogoutUseCase;
import com.devopsuniversity.auth.application.port.in.RegisterAccountUseCase;
import com.devopsuniversity.auth.infrastructure.web.dto.AuthResponse;
import com.devopsuniversity.auth.infrastructure.web.dto.CredentialsRequest;
import com.devopsuniversity.player.infrastructure.web.PlayerWebMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador HTTP de autenticación: registro, inicio y cierre de sesión.
 * Solo traduce HTTP ⇄ casos de uso.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterAccountUseCase register;
    private final LoginUseCase login;
    private final LogoutUseCase logout;

    public AuthController(RegisterAccountUseCase register, LoginUseCase login, LogoutUseCase logout) {
        this.register = register;
        this.login = login;
        this.logout = logout;
    }

    /** Crea la cuenta y deja la sesión iniciada. 201, o 409 si el usuario ya existe. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody CredentialsRequest request) {
        return toResponse(register.register(new RegisterAccountUseCase.Command(request.username(), request.password())));
    }

    /** 200 con el token, o 401 si el usuario o la contraseña no coinciden. */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody CredentialsRequest request) {
        return toResponse(login.login(new LoginUseCase.Command(request.username(), request.password())));
    }

    /** Cierra la sesión del token enviado. Siempre 204 (idempotente). */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        logout.logout(BearerToken.from(request));
    }

    private static AuthResponse toResponse(AuthResult result) {
        return new AuthResponse(result.token(), result.expiresAt(), PlayerWebMapper.toResponse(result.player()));
    }
}
