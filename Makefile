start:
	JAVA_HOME=/opt/openjdk-bin-21 mvn exec:java -X
build:
	JAVA_HOME=/opt/openjdk-bin-21 mvn package -X