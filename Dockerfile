FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y ant

# Clona o projeto
RUN git clone https://github.com/Kevyn-lucca/backend-tcc.git ./

# Copia a lib CopyLibs do NetBeans para a pasta do Ant
COPY Libs/org-netbeans-modules-java-j2seproject-copylibstask.jar /usr/share/ant/lib/

# Seta explicitamente a variável do Ant
ENV Libs.CopyLibs.classpath="/usr/share/ant/lib/org-netbeans-modules-java-j2seproject-copylibstask.jar"

# Garante que o Ant reconhece a extensão
RUN ls -l /usr/share/ant/lib/ && ant -diagnostics | grep CopyLibs || true

# Build do NetBeans
RUN ant clean && ant -f build.xml
