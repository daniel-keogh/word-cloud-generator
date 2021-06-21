package ie.gmit.sw;

public class Word implements Comparable<Word> {
    private final String word;
    private final int frequency;

    public Word(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return frequency + "  " + word;
    }

    @Override
    public int compareTo(Word word) {
        return -Integer.compare(frequency, word.frequency);
    }
}
