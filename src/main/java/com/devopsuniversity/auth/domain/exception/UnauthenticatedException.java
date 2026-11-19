package com.devopsuniversity.auth.domain.exception;

import com.devopsuniversity.shared.domain.DomainException;

/** La petición no trae una sesión válida (falta el token, es incorrecto o ha caducado). */
public class UnauthenticatedException extends DomainException {

    public UnauthenticatedException() {
        super("UNAUTHENTICATED", "Tu sesión no es válida o ha caducado. Inicia sesión de nuevo");
    }
}
