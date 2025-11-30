start:
	JAVA_HOME=/opt/openjdk-bin-17 mvn exec:java -X
build:
	JAVA_HOME=/opt/openjdk-bin-17 mvn package -X