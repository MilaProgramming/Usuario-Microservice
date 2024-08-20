# Usa una imagen base de Java 11
FROM openjdk:17

# Establece el directorio de trabajo en la imagen
WORKDIR /app

# Copia el archivo JAR construido desde el contenedor de construcción
COPY ./target/msvc-usuarios-0.0.1-SNAPSHOT.jar /app/my-microservice.jar

# Establece variables de entorno
ENV PORT 8080
ENV DB_NAME appdb
ENV DB_USER root
ENV DB_PASS 12345

# Define el comando para ejecutar la aplicación
CMD ["sh", "-c", "java $JAVA_OPTS -jar my-microservice.jar --server.port=$APP_PORT"]