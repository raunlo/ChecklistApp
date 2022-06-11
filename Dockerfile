
# 1st stage, build the app
FROM maven:3.8.5-amazoncorretto-17 as build

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN mvn package -DskipTests

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests
RUN echo "done!"

# 2nd stage, build the runtime image
FROM openjdk:17-alpine
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/checklist.jar ./
COPY --from=build /helidon/target/libs ./libs

ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar checklist.jar"]

EXPOSE ${PORT}
