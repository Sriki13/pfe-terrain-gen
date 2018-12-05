package pfe.terrain.gen.algo.name.markov;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Represents one character state.
 */
public class CharState
{
    // the character represented by this state
    char curChar;
    // the total number of next states seen
    int total;
    // a map of states to their quantities
    LinkedHashMap<CharState, Integer> nexts;
    // a random number generator for picking the next state
    Random rand;

    /**
     * Constructs a state representing a character.
     * @param curChar the character
     * @param rand a random number generator
     */
    public CharState(char curChar, Random rand)
    {
        this.curChar = curChar;
        this.rand = rand;
    }

    /**
     * Returns the character represented by this state.
     * @return the character
     */
    public char getChar()
    {
        return curChar;
    }

    /**
     * Sets the total number of next states seen.
     * @param total the new total
     */
    public void setTotal(int total)
    {
        this.total = total;
    }

    /**
     * Sets the map of states to quantities.
     * @param nexts the new map
     */
    public void setMap(LinkedHashMap<CharState, Integer> nexts)
    {
        this.nexts = nexts;
    }

    /**
     * Returns a random next state.
     * @return the next state.
     */
    public CharState next()
    {
        // Generate a random number within the total number of states seen.
        int choice = rand.nextInt(total);
        int chance = 0;

        // For each state in the map do:
        for (CharState posNext : nexts.keySet()) {
            // Add the chance of that state.
            chance += nexts.get(posNext);
            // If the choice is within the chance, then return the state.
            if (choice < chance) {
                return posNext;
            }
        }

        // If we got this far, we somehow generated a number too large.
        throw new RuntimeException("Error: '" + curChar + "' invalid choice ("
                + choice + ") for total of " + total + ".");
    }

    /**
     * Indicates that a new next state has been seen.
     * @param newNext the state that was seen
     */
    public void addNext(CharState newNext)
    {
        // Increment that state's quantity and the total.
        nexts.put(newNext, nexts.get(newNext) + 1);
        ++total;
    }

    /**
     * Indicates that a next state should be reset to zero.
     * @param oldNext the state to be reset
     */
    public void resetNext(CharState oldNext)
    {
        // Reset the state's quantity and remove it from the total.
        total -= nexts.get(oldNext);
        nexts.put(oldNext, 0);
    }

    /**
     * Returns a string representation of this state and its nexts.
     * @return the string
     */
    public String toString() {
        String retStr = "" + curChar + ":\n   ";

        for (CharState state : nexts.keySet()) {
            retStr += "[" + state.getChar() + ":" + nexts.get(state) + "]";
        }

        return retStr + "\n";
    }
}