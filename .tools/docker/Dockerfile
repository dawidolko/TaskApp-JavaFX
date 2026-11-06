# TaskApp is a JavaFX desktop application, so the container is not an application
# server here, just a reproducible build and test environment.
# Running the GUI requires forwarding the host's X server - see README.md.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# pom.xml alone first: downloaded dependencies stay in the layer cache until the
# file changes, which shortens subsequent builds.
COPY CODE/App/pom.xml .
RUN mvn -B dependency:go-offline

COPY CODE/App/ .
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

# GTK/X11 libraries JavaFX needs to draw the window.
RUN apt-get update && apt-get install -y --no-install-recommends \
        libgtk-3-0 \
        libxtst6 \
        libxrender1 \
        libxi6 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/ ./target/

RUN useradd --create-home --uid 1000 appuser && chown -R appuser:appuser /app
USER appuser

ENV DISPLAY=:0

CMD ["sh", "-c", "java -jar $(ls target/*.jar | grep -v original | head -1)"]
