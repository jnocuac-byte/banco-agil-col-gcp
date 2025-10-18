# Usa una imagen base de Java con el JDK, idealmente ligera
FROM eclipse-temurin:21-jdk-jammy AS build

# Copia el código fuente del proyecto
COPY . /app

# Establece el directorio de trabajo
WORKDIR /app

# Empaqueta la aplicación con Maven
# La opción -DskipTests es opcional, pero acelera la compilación si ya los corriste
RUN ./mvnw clean package -DskipTests

# SEGUNDA ETAPA: Imagen de ejecución (solo con JRE para menor tamaño)
FROM eclipse-temurin:21-jre-jammy

# Argumento para el nombre del archivo JAR generado
ARG JAR_FILE=target/auth-service-*.jar # Asegúrate de que el patrón coincida con tu JAR

# Copia el JAR del paso de compilación
COPY --from=build /app/${JAR_FILE} app.jar

# Define el punto de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]