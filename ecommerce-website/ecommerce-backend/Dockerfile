# Use an OpenJDK base image 
FROM openjdk:17-jdk-alpine 

# Set environment variables 
ENV APP_HOME=/app 
WORKDIR $APP_HOME 

# Copy the built JAR file into the container 
COPY target/ecommerce-backend-0.0.1-SNAPSHOT.jar app.jar 

# Expose port (Spring Boot default is 8080) 
EXPOSE 8080 

# Run the JAR file 
ENTRYPOINT ["java", "-jar", "app.jar"]