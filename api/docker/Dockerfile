# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar apenas os arquivos de build (pom.xml de cada módulo)
COPY pom.xml .
COPY domain/pom.xml ./domain/
COPY framework/pom.xml ./framework/

# Fazer o download antecipado das dependências (melhor para cache entre builds)
RUN mvn dependency:go-offline

# Agora copiar o código-fonte completo
COPY . .

# Fazer o build final (pulando os testes para produção)
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar o JAR final gerado no submódulo 'framework'
COPY --from=build /app/framework/target/*.jar app.jar

# Expor a porta que a aplicação usa
EXPOSE 8080

# Executar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
