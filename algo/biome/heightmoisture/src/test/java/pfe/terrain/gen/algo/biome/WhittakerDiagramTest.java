package pfe.terrain.gen.algo.biome;

import org.junit.Test;
import pfe.terrain.gen.algo.Biome;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WhittakerDiagramTest {

    @Test
    public void verifyOkDiagram() {
        WhittakerDiagram whittakerDiagram = new WhittakerDiagram(WhittakerDiagram.WCLASSIC, 0.9);
        Biome shouldBeTed = whittakerDiagram.getBiome(0.25, 0.7);
        assertThat(shouldBeTed, equalTo(Biome.TEMPERATE_DESERT));
        Biome shouldBeSnow = whittakerDiagram.getBiome(1.0, 1.0);
        assertThat(shouldBeSnow, equalTo(Biome.SNOW));
        Biome shouldBeMAN = whittakerDiagram.getBiome(0.83, 0.01);
        assertThat(shouldBeMAN, equalTo(Biome.MANGROVE));
    }

//    @Test
//    public void createDiagram() {
//        WhittakerDiagram whittakerDiagram = new WhittakerDiagram(diagName, 0.9);
//    }
}
