package pfe.terrain.gen.water;

public class ShapeMatrix {

    private boolean[][] matrix;
    private final int size;

    public ShapeMatrix(String param) {
        String lines[] = param.split("\\r?\\n");
        size = lines.length;
        matrix = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < lines[i].length(); j++) {
                matrix[i][j] = (lines[i].charAt(j) == '1');
            }
        }
    }

    public boolean isWater(int x, int y) {
        return !(matrix[x][y]);
    }

    public int getSize() {
        return size;
    }
}
