package ie.gmit.sw;

public class Word implements Comparable<Word> {

	private String word;
	private int frequency;
	
	public Word() {}
	
	public Word(String word, Integer frequency) {
		setWord(word);
		setFrequency(frequency);
	}

	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	@Override
	public String toString() {
		return "Word [word=" + word + " frequency=" + frequency + "]";
	}

	// A PriorityQueue assigns priority based on the implementation of Comparable.
	@Override
	public int compareTo(Word word) {
		return word.frequency - this.frequency;
	}
}
