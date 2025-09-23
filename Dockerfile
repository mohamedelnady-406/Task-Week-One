# ---- Build Stage ----
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /app
COPY . .
# Build the fat JAR but skip tests
RUN gradle clean shadowJar -x test

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
