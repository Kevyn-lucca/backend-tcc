# Compila o projeto
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copia tudo
COPY . .

# Compila usando Ant e gera o WAR
RUN apt-get update && apt-get install -y ant && \
    ant clean && ant -f build.xml

# ===================================================================

# Contêiner final com Tomcat
FROM tomcat:9.0-jdk17

# Remove aplicativos padrão do Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o WAR gerado para o Tomcat
COPY --from=build /app/dist/*.war /usr/local/tomcat/webapps/ROOT.war

# Expõe a porta
EXPOSE 8080

# Inicia o Tomcat
CMD ["catalina.sh", "run"]
