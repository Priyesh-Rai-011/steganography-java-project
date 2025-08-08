# Stage 1: Build the application using a specific Maven JDK image
# This stage has all the tools needed to compile and package the Java application.
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml first to leverage Docker's layer caching.
# Dependencies will only be re-downloaded if pom.xml changes.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of your application's source code
COPY src ./src

# Compile the code, run tests, and package it into an executable JAR file.
RUN mvn clean package


# Stage 2: Create the final, ultra-lightweight production image
# We use a minimal JRE (Java Runtime Environment) based on Alpine Linux.
# This image is significantly smaller than a full JDK.
FROM eclipse-temurin:17-jre-alpine

# Set the working directory
WORKDIR /app

# Add a non-root user for better security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the built JAR file from the 'build' stage into this new image.
# This is the key to a small image – no source code or build tools are included.
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
