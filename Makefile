.PHONY: build run test clean docker-up docker-down docker-logs

# Build the application
build:
	./mvnw clean package -DskipTests

# Run locally
run:
	./mvnw spring-boot:run

# Run tests
test:
	./mvnw test

# Clean build
clean:
	./mvnw clean

# Docker commands
docker-up:
	docker-compose up --build -d

docker-down:
	docker-compose down

docker-logs:
	docker-compose logs -f

docker-restart: docker-down docker-up

# Database operations
db-shell:
	docker-compose exec mongo mongosh internet-store

db-backup:
	docker-compose exec mongo mongodump --db internet-store --out /backup/$(shell date +%Y%m%d_%H%M%S)

# Development utilities
check-deps:
	./mvnw dependency:tree

format:
	./mvnw spotless:apply

# Health check
health:
	curl -f http://localhost:8080/api/actuator/health || echo "Service is not healthy"