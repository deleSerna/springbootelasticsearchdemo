FROM maven:3.6.3-openjdk-11

WORKDIR /usr/src/app

COPY . /usr/src/app
RUN mvn package -Dmaven.test.skip=true

ENV PORT 5000
EXPOSE $PORT
CMD [ "sh", "-c", "mvn -Dserver.port=${PORT} spring-boot:run" ]
