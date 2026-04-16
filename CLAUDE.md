# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Keycloak SPI (Service Provider Interface) extension that adds a password policy checking passwords against the Have I Been Pwned API using k-Anonymity (only SHA-1 prefix sent). Built as a single JAR deployed to Keycloak's `providers/` directory.

## Build & Development Commands

- **Build**: `mvn package` or `make build`
- **Clean**: `mvn clean`
- **Start local Keycloak** (builds first): `make up` — runs on `http://localhost:8080` (admin/admin)
- **Stop**: `make down`
- **Rebuild + restart**: `make restart`
- **Logs**: `make logs`
- **Disable SSL for local dev**: `make disable-ssl`

Remote debug port is exposed on 8787 via docker-compose.

## Architecture

This is a Keycloak `PasswordPolicyProvider` SPI with two classes:

- **`HaveIBeenPwnedPasswordPolicyProviderFactory`** — SPI factory, registered via `META-INF/services/org.keycloak.policy.PasswordPolicyProviderFactory`. Policy ID is `haveIBeenPwned`. Config type is integer (threshold for max allowed breach occurrences, default 0).
- **`HaveIBeenPwnedPasswordPolicyProvider`** — Calls the HIBP Range API (`/range/{sha1-prefix}`), parses the response to find the password's suffix, and returns a `PolicyError` if the breach count exceeds the configured threshold. On API failure, the password is allowed through (fail-open).

Error messages are in `src/main/resources/theme-resources/messages/` (English and Polish).

## Key Details

- Java 17, Keycloak 26.6.1 (`keycloak-parent` BOM for dependency management)
- No test framework is currently configured
- Releases are triggered by pushing a `v*` tag to `main` — GitHub Actions builds and publishes the JAR
- The docker-compose mounts the built JAR directly into the Keycloak container's providers directory
