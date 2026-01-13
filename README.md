# Keycloak Have I Been Pwned Password Policy

A custom Keycloak password policy that checks if a password has been leaked in any known data breaches using the [Have I Been Pwned](https://haveibeenpwned.com/API/v3#PwnedPasswords) (HIBP) API.

## Features

- **HIBP Check**: Automatically validates user passwords against the Have I Been Pwned database.
- **k-Anonymity**: Uses the HIBP Range API (sending only the first 5 characters of the SHA-1 hash) to ensure the user's password is never actually sent to the external service.
- **Configurable Threshold**: Allows setting a maximum number of allowed occurrences in data breaches before a password is rejected.
- **Multi-language Support**: Includes translations for English and Polish.

## Requirements

- **Java**: 17 or higher
- **Keycloak**: 26.5.0 or higher (compatible versions)
- **Maven**: For building from source

## Build

To build the project and generate the JAR file:

```bash
mvn clean package
```

Alternatively, if you have `make` installed:

```bash
make build
```

 The resulting JAR will be located at `target/keycloak-have-i-been-pwned-password-policy-1.0-SNAPSHOT.jar`.

## Installation

1. Copy the generated `.jar` file to the Keycloak `providers/` directory.
2. Restart Keycloak.
3. In the Keycloak Admin Console:
   - Navigate to **Authentication** -> **Policies** -> **Password Policy**.
   - Click **Add policy**.
   - Select **Have I Been Pwned**.
   - (Optional) Set the **Threshold** value. If set to `0`, the password will be rejected if it appears in even a single breach.

## Local Development

The project includes a `docker-compose.yml` for quick testing.

### Commands

- **Start Keycloak**: `make up` (Runs on `http://localhost:8080`)
- **Stop Keycloak**: `make down`
- **Rebuild and Restart**: `make restart`
- **Disable SSL** (Master Realm): `make disable-ssl` (Useful when accessing via localhost without HTTPS)
- **View Logs**: `make logs`

### Default Credentials

- **Username**: `admin`
- **Password**: `admin`

## License

MIT
