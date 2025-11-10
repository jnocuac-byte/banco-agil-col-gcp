# Etapa 1: Construcción con Maven y JDK 17
# Usamos una imagen oficial de Eclipse Temurin con Maven preinstalado.
FROM maven:3.9-eclipse-temurin-17 AS builder

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar solo el pom.xml para descargar dependencias y aprovechar el caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código fuente y construir la aplicación
COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Ejecución con un JRE 17 ligero
# Usamos una imagen JRE optimizada para producción.
FROM eclipse-temurin:17-jre-jammy

# Copiar el archivo .jar construido desde la etapa anterior
COPY --from=builder /app/target/auth-service-*.jar /app.jar

# Exponer el puerto en el que corre la aplicación y ejecutarla
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]