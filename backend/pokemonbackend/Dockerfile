# Imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el jar generado
COPY target/*.jar app.jar

# Exponemos el puerto (informativo)
EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "app.jar"]
