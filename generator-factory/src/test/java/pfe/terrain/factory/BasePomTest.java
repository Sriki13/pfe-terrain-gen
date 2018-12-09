package pfe.terrain.factory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.factory.pom.PomConstants;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BasePomTest {

    @Test
    public void addDepTest() {
        BasePom pom = new BasePom();

        Node dep = pom.addDependency(new Dependency("test"));

        NodeList list = pom.getDoc().getElementsByTagName(PomConstants.dependency);

        assertTrue(list.getLength() >= 1);

        List<Node> nodes = new ArrayList<>();

        for (int i = 0; i < list.getLength(); i++) {
            nodes.add(list.item(i));
        }

        assertTrue(nodes.contains(dep));


        Assert.assertNotNull(pom.toString());

        assertTrue(pom.contain(new Dependency("test")));
    }
}
