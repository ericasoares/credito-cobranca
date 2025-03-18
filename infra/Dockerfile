# Usa a imagem do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR para dentro do container
COPY target/microservico-cobranca.jar app.jar

# Executa o microserviço
ENTRYPOINT ["java", "-jar", "app.jar"]
