# Build stage: compiles the application with the Maven wrapper.
# The cache mount keeps downloaded dependencies between builds.
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 chmod +x mvnw && ./mvnw -q package -DskipTests

# Runtime stage: only the JRE and the jar.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
