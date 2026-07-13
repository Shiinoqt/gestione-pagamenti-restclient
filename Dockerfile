FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar gestionepagamento.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","gestionepagamento.jar"]
