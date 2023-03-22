FROM maven:3.8-jdk-11
RUN mkdir /pbl1_redes
COPY . /pbl1_redes
WORKDIR /pbl1_redes
RUN mvn clean package -DskipTests
CMD ["java", "-jar","target/pbl1_redes_medidor-1.0-jar-with-dependencies.jar"]