# Clone and setup
git clone <repository>
cd internet-store

# Build and run with Docker (Recommended)
make docker-up

# Or run locally (requires local MongoDB)
make build
make run

# Access points:
# Application: http://localhost:8080/api
# Swagger UI: http://localhost:8080/api/swagger-ui.html
# MongoDB: localhost:27017