package pfe.terrain.gen.algo;

public class KeyTypeMismatch extends Exception {
    public KeyTypeMismatch(String got, String expected) {
        super("Key Type mismatch, expected : " + expected + ", got : " + got);
    }
}
