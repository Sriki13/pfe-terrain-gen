import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.CompositionAlreadyExistException;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.factory.storage.CompoStorage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ControllerTest {

    private ServiceController controller;

    private class Lister extends ArtifactoryAlgoLister{
        @Override
        public List<Algorithm> getAlgo() throws CannotReachRepoException, IOException {
            return Arrays.asList(new Algorithm("salut"),new Algorithm("test"));
        }
    }

    private class FailLister extends ArtifactoryAlgoLister{
        @Override
        public List<Algorithm> getAlgo() throws CannotReachRepoException, IOException {
            throw new CannotReachRepoException();
        }
    }

    @Before
    public void init(){
        new CompoStorage().clear();
        this.controller = new ServiceController(new Lister());
    }

    @Test
    public void pomTest() throws Exception{
        BasePom pom = this.controller.getGenerator(Arrays.asList("salut"));

        assertTrue(pom.contain(new Dependency("salut")));
    }

    @Test (expected = NoSuchAlgorithmException.class)
    public void missingAlgoForGenTest() throws Exception{
        this.controller.getGenerator(Arrays.asList("azeazea"));
    }

    @Test
    public void getAlgoListTest() throws Exception{
        List<Algorithm> algos = this.controller.getAlgoList();

        assertTrue(algos.contains(new Algorithm("salut")));
        assertTrue(algos.contains(new Algorithm("test")));
    }

    @Test (expected = CannotReachRepoException.class)
    public void failListTest() throws Exception{
        new ServiceController(new FailLister()).getAlgoList();
    }

    @Test
    public void getCompoTest(){
        assertEquals(0,this.controller.getCompositions().size());

        Composition compo = new Composition();

        new CompoStorage().addComposition(compo);

        assertEquals(1,this.controller.getCompositions().size());

        assertEquals(compo,this.controller.getCompositions().get(0));
    }

    @Test
    public void addCompoTest() throws Exception{
        Composition compo = this.controller.addComposition("test",Arrays.asList("salut"),"context");

        CompoStorage storage = new CompoStorage();

        assertEquals(1,storage.getCompositions().size());

        assertEquals(compo,storage.getCompositions().get(0));

        assertTrue(this.controller.getCompositions().contains(compo));
    }

    @Test(expected = NoSuchAlgorithmException.class)
    public void missingAlgoTest() throws Exception{
        this.controller.addComposition("test",Arrays.asList("qsdsqd"),"context");
    }

    @Test(expected = CompositionAlreadyExistException.class)
    public void alreadyExistingCompoException() throws Exception{
        Composition compo = this.controller.addComposition("test",Arrays.asList("salut"),"context");
        this.controller.addComposition("test",Arrays.asList("salut"),"context");

    }
}
