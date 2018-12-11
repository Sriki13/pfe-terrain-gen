package pfe.terrain.factory;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.compatibility.Compatibility;
import pfe.terrain.factory.compatibility.SimpleCompatibility;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.storage.CompatibilityStorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static pfe.terrain.factory.compatibility.SimpleCompatibility.COMPATIBLE_BEWARE;
import static pfe.terrain.factory.compatibility.SimpleCompatibility.UNCOMPATIBLE;
import static pfe.terrain.factory.compatibility.SimpleCompatibility.UNKNOWN;

public class AlgorithmSimpleCompatibilityStorageTest {

    private CompatibilityStorage storage;

    private Algorithm a;
    private Algorithm b;

    @Before
    public void init(){
        this.storage = new CompatibilityStorage();
        this.storage.clear();

        this.a = new Algorithm("a");
        this.b = new Algorithm("b");
    }

    @Test
    public void addTest(){
        assertEquals(SimpleCompatibility.UNKNOWN,this.storage.getCompatibility(a,b));
    }

    @Test
    public void addCompatibilityTest(){
        Compatibility compat = this.storage.putCompatibility(a,b, COMPATIBLE_BEWARE);
        assertEquals(compat,this.storage.getCompatibility(b,a));
        assertEquals(compat, COMPATIBLE_BEWARE);
    }

    @Test
    public void overrideTest(){
        this.storage.putCompatibility(a,b, COMPATIBLE_BEWARE);
        this.storage.putCompatibility(b,a, SimpleCompatibility.UNCOMPATIBLE);

        assertEquals(SimpleCompatibility.UNCOMPATIBLE,this.storage.getCompatibility(a,b));
    }

    @Test
    public void getCompatTest(){
        assertNull(SimpleCompatibility.compatibilityFromId(100));

        assertEquals(COMPATIBLE_BEWARE,SimpleCompatibility.compatibilityFromId(COMPATIBLE_BEWARE.getId()));
        assertEquals(UNKNOWN,SimpleCompatibility.compatibilityFromId(UNKNOWN.getId()));
        assertEquals(UNCOMPATIBLE,SimpleCompatibility.compatibilityFromId(UNCOMPATIBLE.getId()));
    }


}
