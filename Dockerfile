# Imagen de producción del backend (Render, Koyeb, Fly.io, VPS...).
#   docker build -t devopsu-api backend
#   docker run -p 8090:8090 -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... devopsu-api

# ── Etapa 1: compilar el .jar con el Maven Wrapper ───────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Primero solo lo necesario para descargar dependencias (se cachea entre builds).
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B -DskipTests package \
 && cp target/*-SNAPSHOT.jar app.jar 2>/dev/null || cp $(ls target/*.jar | grep -v '\.original$' | head -n 1) app.jar

# ── Etapa 2: imagen final, solo con el JRE y el .jar ─────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

# No ejecutar como root.
RUN useradd --system --uid 10001 spring
USER spring

COPY --from=build /app/app.jar app.jar

# Los planes gratis tienen ~512 MB de RAM: limitamos la JVM a un % de la memoria.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"

# Render/Koyeb inyectan PORT; en local se usa SERVER_PORT o 8090.
EXPOSE 8090
CMD ["sh", "-c", "exec java -Dserver.port=${PORT:-${SERVER_PORT:-8090}} -jar app.jar"]
