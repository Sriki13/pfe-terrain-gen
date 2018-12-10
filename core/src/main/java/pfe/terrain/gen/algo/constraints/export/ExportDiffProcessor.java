package pfe.terrain.gen.algo.constraints.export;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.island.TerrainMap;

public interface ExportDiffProcessor {

    String processDiff(Contract old, Contract last, TerrainMap terrainMap);

}
