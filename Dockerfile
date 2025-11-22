FROM eclipse-temurin:17-jdk

# Instala o Ant
RUN apt-get update && \
    apt-get install -y ant && \
    apt-get clean

# Copia tudo para dentro do container
COPY . /app
WORKDIR /app

# Build do NetBeans (gera o .jar em /dist)
RUN ant clean && ant -f build.xml

# Ajuste o nome do JAR aqui se for diferente:
CMD ["java", "-jar", "dist/projetotcc.jar"]
