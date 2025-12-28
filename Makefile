build:
	JAVA_HOME=/opt/openjdk-bin-21 ./mvnw package -X
build_native:
	JAVA_HOME=/opt/graalvm-jdk-21 ./mvnw -Pnative package -X

native_dir:
	mkdir -p target/bin
	cp -r src/main/resources/* target/bin
	cp target/vertx-crud-demo target/bin/
	
dev:
	JAVA_HOME=/opt/openjdk-bin-21 ./mvnw exec:java -X
start:
	JAVA_HOME=/opt/openjdk-bin-21 java -jar target/vertx-crud-demo-1.0.0-SNAPSHOT-fat.jar 
start_native:
	cd ./target/bin && ./vertx-crud-demo

clean:
	JAVA_HOME=/opt/openjdk-bin-21 ./mvnw clean install

docker_up:
	cd ./container && docker compose up
docker_down:
	cd ./container && docker compose down