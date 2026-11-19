package com.devopsuniversity.auth.domain.exception;

import com.devopsuniversity.shared.domain.DomainException;

/**
 * Usuario o contraseña incorrectos.
 *
 * <p>El mensaje es el mismo en ambos casos a propósito: así nadie puede
 * averiguar qué usuarios existen probando contraseñas.</p>
 */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Usuario o contraseña incorrectos");
    }
}
