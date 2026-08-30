# Docker — TaskApp (JavaFX)

Konfiguracja uruchamia baze MySQL zainicjalizowana skryptami z katalogu
`SQL/` oraz kontener aplikacji zbudowany Mavenem (Java 21).

## Wymagania

- Docker Engine 24+ z wtyczka `docker compose`
- Serwer X, jesli chcesz zobaczyc interfejs (patrz nizej)

## Uruchomienie

```bash
cd .tools/docker
docker compose up --build
```

Baza startuje na porcie **3307** (nietypowy port, zeby nie kolidowac
z lokalna instalacja MySQL). Schemat i dane ladowane sa automatycznie
z `SQL/migration.sql` i `SQL/seeder.sql` przy pierwszym starcie.

## Samo budowanie i testy

```bash
docker compose build app
```

## Aplikacja okienkowa a kontener

To jest aplikacja z graficznym interfejsem, wiec kontener **nie serwuje jej
przez przegladarke**. Okno musi zostac narysowane na serwerze X hosta.

**Linux** — jednorazowo zezwol kontenerowi na dostep do X:

```bash
xhost +local:docker
docker compose up --build
```

**macOS** — wymagany jest XQuartz (`brew install --cask xquartz`), z wlaczona
opcja *Allow connections from network clients*:

```bash
xhost + 127.0.0.1
DISPLAY=host.docker.internal:0 docker compose up --build
```

**Windows** — uzyj serwera X (VcXsrv lub X410) i ustaw `DISPLAY` na adres IP
hosta.

Jesli zalezy Ci wylacznie na sprawdzeniu, ze projekt sie kompiluje, samo
`docker compose build` wystarczy — nie wymaga serwera X.

## Zatrzymanie

```bash
docker compose down      # zachowuje dane
docker compose down -v   # usuwa takze wolumen bazy
```
