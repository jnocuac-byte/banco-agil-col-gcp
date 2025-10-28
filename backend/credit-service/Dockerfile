# 1. ETAPA DE CONSTRUCCIÓN (BUILD STAGE) - ¡ESTO YA FUNCIONÓ!
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn clean package -DskipTests

# 2. ETAPA DE EJECUCIÓN (RUNTIME STAGE)
# Usar la imagen correcta de JRE 21 para producción.
FROM eclipse-temurin:21-jre-alpine 

# Asegura que la aplicación se ejecute como un usuario no root
USER 1000

# Copia el JAR compilado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]