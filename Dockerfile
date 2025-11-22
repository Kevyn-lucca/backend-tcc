# ============================================================
# 1) Etapa de build (compila o WAR com Ant)
# ============================================================
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instala o Ant e git
RUN apt-get update && \
    apt-get install -y ant git && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Clona o repositório
RUN git clone https://github.com/Kevyn-lucca/backend-tcc.git ./

# Roda o build do NetBeans
RUN ant clean && ant -f build.xml

# ============================================================
# 2) Etapa final: Tomcat otimizado
# ============================================================
FROM tomcat:9.0-jdk17

# Limpa apps padrão do Tomcat e remove documentação desnecessária
RUN rm -rf /usr/local/tomcat/webapps/* && \
    rm -rf /usr/local/tomcat/webapps.dist/* && \
    rm -rf /usr/local/tomcat/server/webapps/*

# Copia o WAR gerado
COPY --from=build /app/dist/*.war /usr/local/tomcat/webapps/ROOT.war

# Configurações para produção
ENV CATALINA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true"

# Expõe a porta (Render usa variável de ambiente PORT)
EXPOSE 8080

# Health check simples
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

CMD ["catalina.sh", "run"]