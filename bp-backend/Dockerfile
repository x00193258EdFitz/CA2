#FROM payara/micro:5.2022.2-jdk11

# the Payara Micro Docker image provides a directory 
# from which applications will be deployed on startup
#COPY target/BPRestWS-1.0-SNAPSHOT.war $DEPLOY_DIR

#based on ChatGPT


# Set the base image
FROM maven:3.8.6 AS build


# Set the working directory
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
#RUN mvn dependency:go-offline -B

# Copy the project source code
COPY src ./src
copy config.properties ./src/main/resources/config.properties

# Build the application
RUN mvn install

# Set the base image for the runtime container
#FROM tomcat:9.0.53-jdk11-openjdk-slim
FROM payara/micro:5.2022.2-jdk11


# Set environment variables
ENV PAYARA_DEFAULT_ENV=true


COPY --from=build /app/target/BPRestWS-1.0-SNAPSHOT.war $DEPLOY_DIR
# Copy the built WAR file from the build container to the webapps directory in Tomcat
#COPY --from=build /app/target/BPRestWS-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

