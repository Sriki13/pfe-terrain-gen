import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.storage.CompoStorage;

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
}
