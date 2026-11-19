package com.devopsuniversity.auth.infrastructure.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un parámetro {@code PlayerId} de un controlador como "el jugador de
 * la sesión". Si la petición no trae un token válido se responde 401 sin
 * llegar a ejecutar el método.
 *
 * <pre>{@code
 * @GetMapping("/me")
 * PlayerResponse me(@CurrentPlayer PlayerId playerId) { ... }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentPlayer {
}
