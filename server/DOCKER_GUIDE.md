# Scancodes Docker Guide

This folder now has a Docker setup for the Spring Boot backend and Postgres.

## Files added or changed

- `Dockerfile` builds the Spring Boot app into a small Java runtime image.
- `.dockerignore` keeps build output, IDE files, logs, and secrets out of Docker build context.
- `compose.yaml` starts the app plus Postgres.
- `src/main/resources/application.yaml` reads database and public URL settings from environment variables.

## Why it is built this way

- The Dockerfile is multi-stage. The first stage has Maven and a JDK to compile the app. The final stage only has the JRE needed to run it.
- The final container runs as a non-root user named `scancodes`. This is safer than running the Java process as root.
- Postgres uses a named volume called `postgres-data`, so data survives container restarts.
- The app waits for Postgres to be healthy before starting.
- Secrets are environment variables, not hardcoded Java values. The password in `compose.yaml` is only a local development password.

## Run locally with Docker

From the `server` folder:

```powershell
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

Stop containers:

```powershell
docker compose down
```

Stop containers and delete the local Postgres data volume:

```powershell
docker compose down -v
```

## Important environment variables

Docker Compose automatically reads a file named `.env` in this `server` folder.
Create your own from the example:

```powershell
Copy-Item .env.example .env
```

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/scancodes
SPRING_DATASOURCE_USERNAME=scancodes
SPRING_DATASOURCE_PASSWORD=scancodes_dev_password
APP_AUTH_PUBLIC_BASE_URL=http://localhost:8080
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

For production, set `APP_AUTH_PUBLIC_BASE_URL` to your real domain:

```text
APP_AUTH_PUBLIC_BASE_URL=https://scancodes.net
```

That matters because QR codes encode the storefront URL. In production, a QR for Island Lounge should point to:

```text
https://scancodes.net/island-lounge
```

## Build image only

From the `server` folder:

```powershell
docker build -t scancodes-server:local .
```

## Test before building

Run this before pushing:

```powershell
.\mvnw.cmd test
```

The Docker build skips tests because Docker image creation should be fast and repeatable. Tests should still run in local development and CI before the image is published.
