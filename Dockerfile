# Use multi-stage builds to separate the build environment from the runtime environment

# Stage 1: Build Environment
FROM mcr.microsoft.com/vscode/devcontainers/java:0-17 as builder

# Install Maven
RUN su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven 3.8.1"

# Install Node.js
ARG NODE_VERSION="lts/*"
RUN if [ "${NODE_VERSION}" != "none" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# Copy your source code into the image
COPY . /workspace
WORKDIR /workspace

# Build your Spring Boot Application
RUN mvn clean package

# Stage 2: Production Environment
FROM openjdk:17-jdk-slim

# Copy the built jar file from the builder stage
COPY --from=builder /workspace/target/*.war /app/app.war

# Set the working directory
WORKDIR /app

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.war"]
