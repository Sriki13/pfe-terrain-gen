package pfe.terrain.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.storage.CompoStorage;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CompositionStorageTest {

    private CompoStorage storage;

    @Before
    public void init(){
        this.storage = new CompoStorage();
        this.storage.clear();
    }

    @Test
    public void addTest(){
        assertEquals(0,this.storage.getCompositions().size());
        Composition compo = new Composition();

        this.storage.addComposition(compo);
        assertEquals(1,this.storage.getCompositions().size());
        assertEquals(compo,this.storage.getCompositions().get(0));
    }

    @Test
    public void removeTest() throws Exception{
        Composition composition = new Composition();
        this.storage.addComposition(composition);
        assertEquals(1,this.storage.getCompositions().size());

        this.storage.removeComposition(new Composition("salut",new ArrayList<>(),"{}"));
        assertEquals(1,this.storage.getCompositions().size());

        this.storage.removeComposition(composition);
        assertEquals(0,this.storage.getCompositions().size());


    }
}
