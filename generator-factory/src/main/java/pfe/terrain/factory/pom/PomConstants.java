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
            "\n" +
            "    <artifactId>compositionJA</artifactId>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "\n" +
            "    <parent>\n" +
            "        <groupId>pfe.terrain.gen</groupId>\n" +
            "        <artifactId>root</artifactId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>pfe.terrain.gen</groupId>\n" +
            "            <artifactId>generator-service</artifactId>\n" +
            "            <version>1.0-SNAPSHOT</version>\n" +
            "        </dependency>\n" +
            "        \n" +
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
            "</project>\n";
}
