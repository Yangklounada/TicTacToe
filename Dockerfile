FROM gradle:9.5.0-jdk25-alpine AS build
WORKDIR /tictactoe
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /tictactoe/build/libs/TicTacToe-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]