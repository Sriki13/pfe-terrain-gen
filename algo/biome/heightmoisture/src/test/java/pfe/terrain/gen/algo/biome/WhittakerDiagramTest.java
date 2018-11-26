package pfe.terrain.gen.algo.biome;

import org.junit.Test;
import pfe.terrain.gen.algo.Biome;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class WhittakerDiagramTest {

    private WhittakerDiagram whittakerDiagram;

    @Test
    public void verifyOkDiagram() {
        whittakerDiagram = new WhittakerDiagram(BiomeStyle.CLASSIC.getWhit(), 0.9);
        Biome shouldBeTed = whittakerDiagram.getBiome(0.25, 0.7);
        assertThat(shouldBeTed, equalTo(Biome.TEMPERATE_DESERT));
        Biome shouldBeSnow = whittakerDiagram.getBiome(1.0, 1.0);
        assertThat(shouldBeSnow, equalTo(Biome.SNOW));
        Biome shouldBeMAN = whittakerDiagram.getBiome(0.83, 0.01);
        assertThat(shouldBeMAN, equalTo(Biome.MANGROVE));
    }

    @Test
    public void createDiagram() {
        String diag =
                "0   0.2 0.5 0.8\n" +
                        "0.66 SHR SHR trS trS\n" +
                        "0.33 SHR SHR SHR trS\n" +
                        "0    SHR SHR BEA BEA";
        whittakerDiagram = new WhittakerDiagram(diag, 0.9);
        Biome shouldBetrS = whittakerDiagram.getBiome(0.8, 0.45);
        assertThat(shouldBetrS, equalTo(Biome.TROPICAL_SEASONAL_FOREST));
    }
}
