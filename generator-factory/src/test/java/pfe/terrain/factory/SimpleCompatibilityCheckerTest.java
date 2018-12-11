package pfe.terrain.factory;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.compatibility.Compatibility;
import pfe.terrain.factory.compatibility.CompatibilityChecker;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.CompatibilityException;
import pfe.terrain.factory.storage.CompatibilityStorage;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class SimpleCompatibilityCheckerTest {
    private CompatibilityStorage storage;

    private class TestCompat implements Compatibility{
        public Map<Algorithm,Set<Algorithm>> map;

        public TestCompat(){
            map = new HashMap<>();
        }

        @Override
        public void check(Algorithm a, Algorithm b) throws CompatibilityException {
            if(!this.map.containsKey(a)) this.map.put(a,new HashSet<>());
            if(!this.map.containsKey(b)) this.map.put(b,new HashSet<>());

            this.map.get(a).add(b);
            this.map.get(b).add(a);
        }
    }

    @Before
    public void init(){
        this.storage = new CompatibilityStorage();
        this.storage.clear();
    }


    @Test
    public void test() throws Exception{
        Algorithm a = new Algorithm(new NotExecutableContract("a","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"a");
        Algorithm b = new Algorithm(new NotExecutableContract("b","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"b");
        Algorithm c = new Algorithm(new NotExecutableContract("c","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"c");
        Algorithm d = new Algorithm(new NotExecutableContract("d","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"d");
        Algorithm e = new Algorithm(new NotExecutableContract("e","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"e");
        Algorithm f = new Algorithm(new NotExecutableContract("f","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"f");
        Algorithm g = new Algorithm(new NotExecutableContract("g","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"g");
        Algorithm h = new Algorithm(new NotExecutableContract("h","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"h");
        Algorithm i = new Algorithm(new NotExecutableContract("i","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"i");
        Algorithm j = new Algorithm(new NotExecutableContract("j","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"j");
        Algorithm k = new Algorithm(new NotExecutableContract("k","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"k");
        Algorithm l = new Algorithm(new NotExecutableContract("l","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"l");
        Algorithm m = new Algorithm(new NotExecutableContract("m","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"m");
        Algorithm n = new Algorithm(new NotExecutableContract("n","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"n");
        Algorithm o = new Algorithm(new NotExecutableContract("o","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"o");
        Algorithm p = new Algorithm(new NotExecutableContract("p","",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())),"p");

        List<Algorithm> algorithmList = Arrays.asList(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p);


        TestCompat compatibility = new TestCompat();
        for(Algorithm one : algorithmList){
            for (Algorithm two : algorithmList){
                this.storage.putCompatibility(one,two,compatibility);
            }
        }

        CompatibilityChecker checker = new CompatibilityChecker(algorithmList);
        checker.check();

        for(Algorithm key : compatibility.map.keySet()){
            assertEquals(15,compatibility.map.get(key).size());
        }
    }
}
