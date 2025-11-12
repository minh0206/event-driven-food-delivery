# --- STAGE 1.1: Backend Builder ---
FROM eclipse-temurin:25-jdk-jammy AS backend-builder
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
COPY service-discovery /app/service-discovery
# Build the entire project
RUN ./mvnw clean package -DskipTests

# --- STAGE 1.2: Frontend Builder ---
FROM node:22-alpine AS frontend-builder
WORKDIR /app
COPY frontend /app
RUN npm install -g pnpm && pnpm install && pnpm build


# --- STAGE 2: Target User Service ---
FROM eclipse-temurin:25-jre-jammy AS user-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/user-service/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8081
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 3: Target Restaurant Service ---
FROM eclipse-temurin:25-jre-jammy AS restaurant-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/restaurant-service/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8082
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 4: Target Order Service ---
FROM eclipse-temurin:25-jre-jammy AS order-service
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/order-service/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8083
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]


# --- STAGE 5: Target Delivery Service ---
FROM eclipse-temurin:25-jre-jammy AS delivery-service

WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/delivery-service/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8084
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]

# --- STAGE 6: Target API Gateway ---
FROM eclipse-temurin:25-jre-jammy AS api-gateway
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/api-gateway/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8080
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]

# --- STAGE 7: Target Service Discovery ---
FROM eclipse-temurin:25-jre-jammy AS service-discovery
WORKDIR /app
RUN mkdir -p /app/lib
COPY --from=backend-builder /app/service-discovery/target/*.jar app.jar
COPY --from=backend-builder /app/shared-module/target/*.jar /app/lib/shared-module.jar
COPY --from=backend-builder /app/security-jwt-lib/target/*.jar /app/lib/security-jwt-lib.jar
EXPOSE 8761
CMD ["java", "-Dloader.path=lib/", "-jar", "app.jar"]

# --- STAGE 8: Target Frontend ---
FROM nginx:alpine AS frontend
COPY --from=frontend-builder /app/apps/customer/dist /usr/share/nginx/html/customer
COPY --from=frontend-builder /app/apps/restaurant/dist /usr/share/nginx/html/restaurant
COPY --from=frontend-builder /app/apps/driver/dist /usr/share/nginx/html/driver
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN nginx -t

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
