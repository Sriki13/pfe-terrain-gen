package pfe.terrain.gen.algo;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.reflection.ContractReflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReflectionTest {
    ContractReflection reflection;

    @Before
    public void init(){
        reflection = new ContractReflection();
    }


    @Test
    public void reflectTest(){
        assertTrue(reflection.getContracts().size() == 0);

    }
}
