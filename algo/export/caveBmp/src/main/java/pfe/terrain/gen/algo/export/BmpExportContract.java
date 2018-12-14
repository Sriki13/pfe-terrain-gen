package pfe.terrain.gen.algo.export;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.IntegerType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BmpExportContract extends Contract {

    private static final Key<Void> ALL_KEY = new Key<>("All", Void.class);

    private static final Key<byte[]> EXPORT_BMP_KEY = new Key<>("bmp", byte[].class);
    private static final Key<String> RESPONSE_TYPE = new Key<>(EXPORT_BMP_KEY.getId() + "_response_type", String.class);

    private static final Key<IntegerType> GRADIENT_KEY =
            new SerializableKey<>(FACES_PREFIX + "COLOR", "color", IntegerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(ALL_KEY, GRADIENT_KEY), asKeySet(EXPORT_BMP_KEY, RESPONSE_TYPE));
    }

    @Override
    public String getDescription() {
        return "A BMP exporter for caves.";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        map.putProperty(RESPONSE_TYPE, "application/octet-stream");
        int size = map.getProperty(SIZE);
        Set<Face> faces = map.getProperty(FACES);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Map<Integer, Map<Integer, Face>> faceMatrix = new HashMap<>();
        int spread = (int) Math.ceil(Math.sqrt((size * size) / map.getProperty(FACES).size()));
        for (Face face : faces) {
            int x = (int) Math.round(face.getCenter().x);
            int y = (int) Math.round(face.getCenter().y);
            for (int i = x - spread; i < x + spread; i++) {
                for (int j = y - spread; j < y + spread; j++) {
                    faceMatrix.putIfAbsent(i, new HashMap<>());
                    Map<Integer, Face> column = faceMatrix.get(i);
                    if (column.get(j) == null || column.get(j).getCenter().distance(new Coord(x, y))
                            > face.getCenter().distance(new Coord(x, y))) {
                        column.put(j, face);
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                image.setRGB(i, j, findColor(faceMatrix, size, i, j));
            }
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", outputStream);
            outputStream.flush();
            map.putProperty(EXPORT_BMP_KEY, outputStream.toByteArray());
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int WALL_COLOR = 4210237;

    private int findColor(Map<Integer, Map<Integer, Face>> faceMatrix, int size, double x, double y) {
        Map<Integer, Face> column = faceMatrix.get((int) Math.round(x));
        if (column == null) {
            return WALL_COLOR;
        }
        Face face = column.get((int) Math.round(y));
        if (face == null) {
            return WALL_COLOR;
        }
        return face.getProperty(GRADIENT_KEY).value;
    }

}
