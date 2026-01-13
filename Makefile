.PHONY: build up down restart clean logs disable-ssl

# Build the project using Maven
build:
	mvn package

# Start Keycloak using Docker Compose
up: build
	docker-compose up -d

# Stop Keycloak
down:
	docker-compose down

# Restart the project (rebuild and restart containers)
restart: down build up

# Clean the project
clean:
	mvn clean
	docker-compose down -v

# Show logs
logs:
	docker-compose logs -f

# Disable SSL requirement (useful for local development behind VPN/Proxy)
disable-ssl:
	@echo "Waiting for Keycloak to start..."
	@until docker-compose exec keycloak /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin > /dev/null 2>&1; do \
		echo "Still waiting for Keycloak..."; \
		sleep 2; \
	done
	docker-compose exec keycloak /opt/keycloak/bin/kcadm.sh update realms/master -s sslRequired=NONE
	@echo "SSL requirement disabled for 'master' realm."
