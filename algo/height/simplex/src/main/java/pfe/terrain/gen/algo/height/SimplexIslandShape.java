package pfe.terrain.gen.algo.height;

public enum SimplexIslandShape {

    SQUARE {
        @Override
        public double distance(double nx, double ny) {
            return 2 * Math.max(Math.abs(nx), Math.abs(ny));
        }
    },
    CIRCLE {
        @Override
        public double distance(double nx, double ny) {
            return 2 * Math.sqrt(nx * nx + ny * ny);
        }
    };

    public static SimplexIslandShape getFromString(String str) {
        if (str.toUpperCase().equals(CIRCLE.name())) {
            return CIRCLE;
        } else return SQUARE;
    }

    public abstract double distance(double nx, double ny);

}
