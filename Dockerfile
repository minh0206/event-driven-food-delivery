# --- STAGE 1: Shared Builder ---
FROM eclipse-temurin:25-jdk-jammy AS builder
ARG VERSION
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY shared-module /app/shared-module
COPY security-jwt-lib /app/security-jwt-lib
COPY api-gateway /app/api-gateway
COPY user-service /app/user-service
COPY restaurant-service /app/restaurant-service
COPY order-service /app/order-service
COPY delivery-service /app/delivery-service
# Build the entire project
RUN ./mvnw clean package -DskipTests


# --- STAGE 2: Target User Service ---
FROM eclipse-temurin:25-jre-jammy AS user-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=builder /app/user-service/target/*.jar app.jar
COPY --from=builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8081
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 3: Target Restaurant Service ---
FROM eclipse-temurin:25-jre-jammy AS restaurant-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=builder /app/restaurant-service/target/*.jar app.jar
COPY --from=builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8082
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 4: Target Order Service ---
FROM eclipse-temurin:25-jre-jammy AS order-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=builder /app/order-service/target/*.jar app.jar
COPY --from=builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8083
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 5: Target Delivery Service ---
FROM eclipse-temurin:25-jre-jammy AS delivery-service

WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=builder /app/delivery-service/target/*.jar app.jar
COPY --from=builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8084
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]

# --- STAGE 6: Target API Gateway ---
FROM eclipse-temurin:25-jre-jammy AS api-gateway
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=builder /app/api-gateway/target/*.jar app.jar
COPY --from=builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8080
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]
