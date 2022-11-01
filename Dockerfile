
# 1st stage, build the app
FROM maven:3.8.6-amazoncorretto-18 as build

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .


ADD src src
RUN mvn package -DskipTests

# 2nd stage, build the runtime image
FROM amazoncorretto:18-al2-jdk
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/checklist.jar ./
COPY --from=build /helidon/target/libs ./libs

CMD java --enable-preview -jar checklist.jar

EXPOSE 8080
