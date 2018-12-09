package pfe.terrain.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.gen.algo.constraints.context.MapContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

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
    public void depTest() throws Exception{
        this.compo = new Composition("salut",
                Arrays.asList(new Algorithm("wow"),
                        new Algorithm("test")),"{}");

        assertEquals("salut",this.compo.getName());
        assertTrue(this.compo.getContext().getProperties().isEmpty());

        assertEquals(2,this.compo.getPom().getDependencies().size());
        
        assertTrue(this.compo.getPom().contain(new Dependency("wow")));
        assertTrue(this.compo.getPom().contain(new Dependency("test")));
    }

    @Test
    public void equalTest() throws Exception{
        Composition composition = new Composition("salut",new ArrayList<>(),"{}");
        Composition compo2 = new Composition("salut",new ArrayList<>(),"{}");

        assertEquals(composition,compo2);
        assertEquals(composition.hashCode(),compo2.hashCode());
    }

    @Test
    public void notEqual() throws Exception{
        Composition composition = new Composition("salut",new ArrayList<>(),"{}");
        Composition compo2 = new Composition("azeaze",new ArrayList<>(),"{}");

        assertNotSame(composition,compo2);
    }
}
