package pfe.terrain.gen.algo.name.markov;

/**
 * Generates random names based on Markov chains of characters.
 * @author Christopher Siu (cesiu)
 * @version 29 Jun 2016
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarkovNameGenerator {
    Logger logger = Logger.getLogger("Markov");

    // all the lowercase characters this generator will handle
    public static final char[] LOWERCHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'};
    // all the vowels
    public static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u', 'y'};
    // all the consonants
    public static final char[] CONSONANTS = {'b', 'c', 'd', 'f', 'g', 'h', 'j',
            'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};

    // map of lowercase characters to states
    private LinkedHashMap<Character, CharState> states;
    // the special state for the beginning of a name
    private CharState beginState;
    // a random number generator to generate name lengths
    private Random rand;
    // the range of lengths to pick from
    private int max, min;

    /**
     * Constructs a name generator.
     * @param min the minimum length of a generated name, inclusive
     * @param max the maximum length of a generated name, exclusive
     */
    public MarkovNameGenerator(int min, int max) throws IOException
    {
        rand = new Random();
        this.min = min;
        this.max = max;
        initGen();
        this.initFromFile(this.getClass().getClassLoader().getResource("names.txt").getPath());
    }

    /**
     * Constructs a seeded name generator.
     * @param min the minimum length of a generated name, inclusive
     * @param max the maximum length of a generated name, exclusive
     * @param seed the seed for the random number generator
     */
    public MarkovNameGenerator(int min, int max, long seed)
    {
        rand = new Random(seed);
        this.min = min;
        this.max = max;
        initGen();
    }

    public MarkovNameGenerator(int min, int max, String path) throws IOException{
        rand = new Random();
        this.min = min;
        this.max = max;
        this.initFromFile(path);
    }

    /**
     * Initializes a name generator.
     */
    public void initGen()
    {
        // Populate the map with the initial character set.
        states = new LinkedHashMap<Character, CharState>();
        for (char c : LOWERCHARS) {
            states.put(c, new CharState(c, rand));
        }

        LinkedHashMap<CharState, Integer> initState;
        // For every state do:
        for (CharState curState : states.values()) {
            // Create an initial state where consonants and vowels alternate, with
            // an equal chance of picking each next option.
            initState = new LinkedHashMap<CharState, Integer>();

            // If it's a vowel, add all the consonants to its inital state.
            if (Arrays.binarySearch(VOWELS, curState.getChar()) > -1) {
                for (char c : VOWELS) {
                    initState.put(states.get(c), 0);
                }
                for (char c : CONSONANTS) {
                    initState.put(states.get(c), 1);
                }
                // Don't allow two 'y's in a row.
                if (curState.getChar() == 'y') {
                    initState.put(states.get('y'), 0);
                    curState.setTotal(CONSONANTS.length - 1);
                }
                else {
                    curState.setTotal(CONSONANTS.length);
                }
                curState.setMap(initState);
            }
            // Else add all the vowels to its initial state.
            else {
                for (char c : CONSONANTS) {
                    initState.put(states.get(c), 0);
                }
                for (char c : VOWELS) {
                    initState.put(states.get(c), 1);
                }
                curState.setTotal(VOWELS.length);
                curState.setMap(initState);
            }
        }

        // The beginning state has an equal chance of picking any letter.
        beginState = new CharState(' ', rand);
        initState = new LinkedHashMap<CharState, Integer>();
        for (CharState state : states.values()) {
            initState.put(state, 1);
        }
        beginState.setTotal(LOWERCHARS.length);
        beginState.setMap(initState);

    }

    private void initFromFile(String path) throws IOException {

        String names = new String(Files.readAllBytes(Paths.get(path)));
        names = names.replaceAll("\n"," ");
        String[] namesArrays = names.split(" ");

        for (String name : namesArrays){
            this.addName(name);
        }
    }

    /**
     * Adds a name to the chain.
     * @param name the name to be added
     */
    public void addName(String name)
    {
        // Sanitize the string.
        name = name.replaceAll("\\s+", "").toLowerCase();

        for(char character : name.toCharArray()){
            if(!states.containsKey(character)){
                //logger.log(Level.WARNING, "cannot use name : " + name);
                return;
            }
        }

        if(name.length() == 0){
            return;
        }
        // Update the beginning state.
        beginState.addNext(states.get(name.charAt(0)));



        // For every char except the last, tell the corresponding state which
        // char came after it.
        for (int i = 0; i < name.length() - 1; i++) {
            if (!states.containsKey(name.charAt(i))
                    || ! states.containsKey(name.charAt(i + 1))) {
                //logger.log(Level.WARNING, "cannot use name : " + name);
                continue;
            }
            states.get(name.charAt(i)).addNext(states.get(name.charAt(i + 1)));
        }
    }

    /**
     * Resets the frequencies of a letter combination to zero.
     * @param name the name to be removed
     */
    public void removeName(String name)
    {
        // Sanitize the string.
        name = name.replaceAll("\\s+", "").toLowerCase();

        // For every char except the last, tell the corresponding state which
        // char should be reset.
        for (int i = 0; i < name.length() - 1; i++) {
            if (!states.containsKey(name.charAt(i))
                    || ! states.containsKey(name.charAt(i + 1))) {
                throw new RuntimeException("Error: invalid character in sample.");
            }
            states.get(name.charAt(i)).resetNext(states.get(name.charAt(i + 1)));
        }
    }

    /**
     * Generates a name from the chain.
     * @return the generated name
     */
    public String getName()
    {
        // Pick an initial character.
        CharState curState = beginState.next();
        String retStr = "" + curState.getChar();

        // For each remaining spot in the desired name do:
        for (int i = rand.nextInt(max - min) + min; i > 1; i--) {
            // Query the current state for the random next state.
            curState = curState.next();
            retStr += curState.getChar();
        }

        return retStr;
    }

    /**
     * Returns a string representation of all the characters and their states.
     * @return the string
     */
    public String toString()
    {
        String retStr = "";

        for (CharState state : states.values()) {
            retStr += state.toString();
        }

        return retStr;
    }
}
