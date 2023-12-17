install:
	mvn clean install

compile:
	mvn clean compile

player:
	mvn exec:java -Dexec.mainClass="project.Player" -Dexec.args="./src/main/java/project/config.properties"

viewer:
	mvn exec:java -Dexec.mainClass="project.Viewer" -Dexec.args="./src/main/java/project/config.properties"

admin:
	mvn exec:java -Dexec.mainClass="project.Admin"

javadoc:
	mvn javadoc:javadoc
