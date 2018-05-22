To create docker image: ./gradlew buildDocker
To run docker image: docker run --name mail-server -p 8090:8090 --network=harakasmtpserver_mail-network -e profile=container mail-server
to save docker image: docker save -o mail-server-image mail-server
to load docker image: docker load -i mail-server-image