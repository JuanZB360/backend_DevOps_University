package com.devopsuniversity.auth.domain.exception;

import com.devopsuniversity.shared.domain.DomainException;

/** El usuario o la contraseña no cumplen las reglas de formato (al registrarse). */
public class InvalidCredentialsFormatException extends DomainException {

    public InvalidCredentialsFormatException(String message) {
        super("INVALID_CREDENTIALS_FORMAT", message);
    }
}
