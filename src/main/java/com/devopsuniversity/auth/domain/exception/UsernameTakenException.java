package com.devopsuniversity.auth.domain.exception;

import com.devopsuniversity.auth.domain.model.Username;
import com.devopsuniversity.shared.domain.DomainException;

/** Ya existe una cuenta con ese nombre de usuario. */
public class UsernameTakenException extends DomainException {

    public UsernameTakenException(Username username) {
        super("USERNAME_TAKEN", "El usuario '%s' ya existe. Elige otro o inicia sesión".formatted(username));
    }
}
