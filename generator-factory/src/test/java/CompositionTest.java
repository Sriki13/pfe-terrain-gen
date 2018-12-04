import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.pom.Dependency;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CompositionTest {
    private Composition compo;

    @Before
    public void init(){
        this.compo = new Composition();
    }

    @Test
    public void initTest(){
        assertEquals(0,this.compo.getPom().getDependencies().size());
    }

    @Test
    public void depTest(){
        this.compo = new Composition("salut",
                Arrays.asList(new Algorithm("wow"),
                        new Algorithm("test")),"context");

        assertEquals("salut",this.compo.getName());
        assertEquals("context",this.compo.getContext());

        assertEquals(2,this.compo.getPom().getDependencies().size());
        
        assertTrue(this.compo.getPom().contain(new Dependency("wow")));
        assertTrue(this.compo.getPom().contain(new Dependency("test")));
    }

    @Test
    public void equalTest(){
        Composition composition = new Composition("salut",new ArrayList<>(),"context");
        Composition compo2 = new Composition("salut",new ArrayList<>(),"context");

        assertEquals(composition,compo2);
        assertEquals(composition.hashCode(),compo2.hashCode());
    }

    @Test
    public void notEqual(){
        Composition composition = new Composition("salut",new ArrayList<>(),"context");
        Composition compo2 = new Composition("azeaze",new ArrayList<>(),"context");

        assertNotEquals(composition,compo2);
    }
}
