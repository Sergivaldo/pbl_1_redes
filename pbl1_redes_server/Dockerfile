FROM maven:3.8-jdk-11
RUN mkdir /pbl1_redes
COPY . /pbl1_redes
WORKDIR /pbl1_redes
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/pbl1_redes-1.0-SNAPSHOT-jar-with-dependencies.jar"]