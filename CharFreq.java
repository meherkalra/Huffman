/**
 * creating a class that stores a character and a frequency
 *
 * @author Meher Kalra, Neo Cai, Dartmouth CS10 S22
 */
public class CharFreq implements Comparable<CharFreq> {
    // instance variable that need to be stored: character and frequency
    private Character character;
    private Integer frequency;


    /**
     * constructor
     *
     * @param character
     * @param frequency
     */
    public CharFreq(Character character, Integer frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    /**
     * getter functions
     *
     * @return character and frequency
     */
    public Character getCharacter() {
        return character;
    }

    public Integer getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(CharFreq val) {
        return this.frequency - val.frequency;
    }

    @Override
    public String toString() {
        return character + ":" + frequency;
    }
}

