package com.devopsuniversity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API de DevOps University.
 *
 * <p>Arquitectura hexagonal por módulo funcional ({@code player}, y en el futuro
 * {@code achievement}, {@code classroom}...):</p>
 * <ul>
 *   <li>{@code domain}: modelo y reglas de negocio en Java puro + puertos de salida.</li>
 *   <li>{@code application}: casos de uso (puertos de entrada) y su orquestación.</li>
 *   <li>{@code infrastructure}: adaptadores (REST, JPA) que conectan con el mundo exterior.</li>
 * </ul>
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
