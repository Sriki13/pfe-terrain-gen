package pfe.terrain.gen.algo.exception;

public class InvalidAlgorithmParameters extends RuntimeException {

    public InvalidAlgorithmParameters(String message) {
        super(message);
    }

    public InvalidAlgorithmParameters(String param, String got, String min, String max) {
        super("Invalid parameter used for param " + param + "\n" +
                "Value must be between " + min + " and " + max + " but was " + got);
    }

}
