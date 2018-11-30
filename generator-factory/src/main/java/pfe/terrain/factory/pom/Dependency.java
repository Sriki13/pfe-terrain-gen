package pfe.terrain.factory.pom;

import com.sun.webkit.dom.NodeImpl;
import org.w3c.dom.Node;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return Objects.equals(groupid, that.groupid) &&
                Objects.equals(artifactId, that.artifactId) &&
                Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupid, artifactId, versionId);
    }
}
