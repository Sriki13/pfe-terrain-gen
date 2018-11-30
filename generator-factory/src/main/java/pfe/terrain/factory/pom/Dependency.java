package pfe.terrain.factory.pom;

import com.sun.webkit.dom.NodeImpl;
import org.w3c.dom.Node;

public class Dependency {
    private String groupid;
    private String artifactId;
    private String versionId;

    public Dependency(String artifactId) {
        this();
        this.artifactId = artifactId;

    }

    public Dependency(){
        this.groupid = "pfe.terrain.gen";
        this.versionId = "1.0-SNAPSHOT";
    }

    public Dependency(String groupid, String artifactId, String versionId) {
        this.groupid = groupid;
        this.artifactId = artifactId;
        this.versionId = versionId;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersionId() {
        return versionId;
    }
}
