package pfe.terrain.factory.pom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static pfe.terrain.factory.pom.PomConstants.artifId;
import static pfe.terrain.factory.pom.PomConstants.base;

public class BasePom {

    private Document doc;
    private List<Dependency> dependencies;

    public BasePom(){
        this.dependencies = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(base.getBytes());
            this.doc = builder.parse(stream);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Document getDoc() {
        return doc;
    }

    public Node addDependency(Dependency dependency){
        this.dependencies.add(dependency);
        Node dependencies = doc.getDocumentElement().getElementsByTagName(PomConstants.dependencies).item(0);

        Element dep = this.doc.createElement(PomConstants.dependency);

        Element artif = this.doc.createElement(artifId);
        artif.appendChild(this.doc.createTextNode(dependency.getArtifactId()));

        Element version = this.doc.createElement(PomConstants.version);
        version.appendChild(this.doc.createTextNode(dependency.getVersionId()));

        Element groupId = this.doc.createElement(PomConstants.groupId);
        groupId.appendChild(this.doc.createTextNode(dependency.getGroupid()));

        dep.appendChild(artif);
        dep.appendChild(version);
        dep.appendChild(groupId);

        dependencies.appendChild(dep);

        return dep;


    }

    public boolean contain(Dependency dependency){
        return this.dependencies.contains(dependency);
    }

    @Override
    public String toString() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            StreamResult stream = new StreamResult();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            stream.setOutputStream(byteStream);

            transformer.transform(new DOMSource(this.doc), stream);

            return new String(byteStream.toByteArray());
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }
}
