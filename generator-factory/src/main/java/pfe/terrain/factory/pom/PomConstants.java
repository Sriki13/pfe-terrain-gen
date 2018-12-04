package pfe.terrain.factory.pom;

public class PomConstants {
    public static String artifId = "artifactId";
    public static String version = "version";
    public static String groupId = "groupId";
    public static String dependency = "dependency";
    public static String dependencies = "dependencies";


    public static final String base = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <groupId>pfe.terrain.gen</groupId>\n" +
            "    <artifactId>compositionCustomNoRivers</artifactId>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "\n" +
            "    <properties>\n" +
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "        <maven.compiler.source>1.8</maven.compiler.source>\n" +
            "        <maven.compiler.target>1.8</maven.compiler.target>\n" +
            "        <repo.addr>"+ System.getProperty("repository.address") +"</repo.addr>\n" +
            "    </properties>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>pfe.terrain.gen</groupId>\n" +
            "            <artifactId>generator-service</artifactId>\n" +
            "            <version>1.0-SNAPSHOT</version>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "\n" +
            "    <build>\n" +
            "        <resources>\n" +
            "            <resource>\n" +
            "                <directory>./</directory>\n" +
            "            </resource>\n" +
            "        </resources>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.codehaus.mojo</groupId>\n" +
            "                <artifactId>exec-maven-plugin</artifactId>\n" +
            "                <version>1.2.1</version>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>exec</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                    <execution>\n" +
            "                        <id>graph</id>\n" +
            "                        <configuration>\n" +
            "                            <executable>java</executable>\n" +
            "                            <mainClass>pfe.terrain.generatorService.GraphMain</mainClass>\n" +
            "                        </configuration>\n" +
            "                    </execution>\n" +
            "                    <execution>\n" +
            "                        <id>mapGen</id>\n" +
            "                        <configuration>\n" +
            "                            <executable>java</executable>\n" +
            "                            <mainClass>pfe.terrain.generatorService.GenMap</mainClass>\n" +
            "                        </configuration>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "                <configuration>\n" +
            "                    <executable>java</executable>\n" +
            "                    <mainClass>pfe.terrain.generatorService.Main</mainClass>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "\n" +
            "\n" +
            "    <repositories>\n" +
            "        <repository>\n" +
            "            <id>snapshots</id>\n" +
            "            <url>${repo.addr}</url>\n" +
            "            <releases>\n" +
            "                <enabled>false</enabled>\n" +
            "            </releases>\n" +
            "        </repository>\n" +
            "    </repositories>\n" +
            "    <pluginRepositories>\n" +
            "        <pluginRepository>\n" +
            "            <id>snapshots</id>\n" +
            "            <url>${repo.addr}</url>\n" +
            "            <releases>\n" +
            "                <enabled>false</enabled>\n" +
            "            </releases>\n" +
            "        </pluginRepository>\n" +
            "    </pluginRepositories>\n" +
            "\n" +
            "    <distributionManagement>\n" +
            "        <repository>\n" +
            "            <id>central</id>\n" +
            "            <name>cc8829f5f5de-releases</name>\n" +
            "            <url>${repo.addr}</url>\n" +
            "        </repository>\n" +
            "        <snapshotRepository>\n" +
            "            <id>snapshots</id>\n" +
            "            <name>cc8829f5f5de-snapshots</name>\n" +
            "            <url>${repo.addr}</url>\n" +
            "        </snapshotRepository>\n" +
            "    </distributionManagement>\n" +
            "</project>\n";
}
