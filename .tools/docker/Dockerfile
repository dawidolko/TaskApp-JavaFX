# TaskApp jest aplikacja okienkowa JavaFX, wiec kontener nie jest tu serwerem
# aplikacji, tylko powtarzalnym srodowiskiem budowania i testow.
# Uruchomienie GUI wymaga przekazania serwera X z hosta — patrz README.md.
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Najpierw sam pom.xml: pobrane zaleznosci zostaja w cache warstwy dopoki
# plik sie nie zmieni, co skraca kolejne budowania.
COPY CODE/App/pom.xml .
RUN mvn -B dependency:go-offline

COPY CODE/App/ .
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

# Biblioteki GTK/X11 wymagane przez JavaFX do narysowania okna.
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
