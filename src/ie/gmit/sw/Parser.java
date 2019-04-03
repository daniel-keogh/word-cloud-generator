package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Parser {

	private static final String IGNORE_FILE_NAME = "ignorewords.txt";
	
	private static Scanner console = new Scanner(System.in);
	
	private Set<String> ignoreSet = new HashSet<>();
	private Map<String, Integer> map = new HashMap<>();
	private Queue<Word> wordQ = new PriorityQueue<>();

	public static String getIgnoreFileName() {
		return IGNORE_FILE_NAME;
	}
	
	public Queue<Word> getWordQueue() {
		return wordQ;
	}
	
	public int numUniqueWordsRead() {
		return map.size();
	}
	
	// Add each word in the ignorewords.txt file (if it exists) to a Set (Use a set since there's no need for duplicates).
	// Hash tables (Map or Set) store objects at arbitrary locations and offer an average O(1) for insertion.
	public void parseIgnoreFile() {
		try {
			BufferedReader ignoreFile = new BufferedReader(new FileReader(IGNORE_FILE_NAME));
			String next = null;

			while ((next = ignoreFile.readLine()) != null) {

				if (next.isEmpty())
					continue;

				ignoreSet.add(next.toLowerCase()); // Adding to a HashSet is O(1)
			}
			ignoreFile.close();
		} catch (IOException e) {
			// no ignorewords.txt file found
			System.out.printf("[Warning] No \"%s\" file was found. Proceed anyway (y/N)? ", IGNORE_FILE_NAME);

			if (Character.toUpperCase(console.next().charAt(0)) != 'Y')
				System.exit(0);
		}
	}
	
	public void parseInput(InputStream in) throws Exception {

		BufferedReader inputFile = new BufferedReader(new InputStreamReader(in));
		String next = null;

		// O(n^2)
		while ((next = inputFile.readLine()) != null) {

			// Add every word in each line of the file to an array, using spaces as delimiters.
			String[] words = next.toLowerCase().split(" ");
			processWords(words);
		}
		sortMap();
		in.close();
	}

	public void parseInput(String url) throws Exception {

		try {
			Document doc = Jsoup.connect(url).get();
			String content = doc.select("p").text(); // extract the text between all <p> tags

			// Add every word in content to an array, using spaces as delimiters.
			String[] words = content.toLowerCase().split(" ");
			processWords(words);
			sortMap();	
		}
		catch (IOException e) {
			System.out.println("[Error] Unable to parse "+ url);
			System.exit(0);
		}
	}
	
	private void processWords(String[] words) {
		for (String word : words) {
			word = stripPunctuation(word);
			// Searching a HashSet is O(1).
			if (!ignoreSet.contains(word)) { // Only add words that aren't blacklisted.					
				if (!word.isEmpty())
					updateMap(word);
			}
		}
	}

	private String stripPunctuation(String word) {		
		// Source: Patrick Parker - https://stackoverflow.com/a/43171912
		return word.replaceAll("(?:(?<!\\S)\\p{Punct}+)|(?:\\p{Punct}+(?!\\S))", "");
	}

	// The HashMap implementation provides constant-time performance for the basic operations (get and put).
	private void updateMap(String word) {
		int freq;
		
		// If the word already exists in the Map, its frequency is retrieved and then incremented.
		if (map.containsKey(word)) { // calling containsKey() is O(1)
			freq = map.get(word); // get the current frequency of the word
			map.put(word, ++freq);
		}
		else // First occurrence of this word: Set the frequency to 1
			map.put(word, 1); 
	}
	
	// Add each word in the Map to a PriorityQueue. 
	// Words are sorted each time a new element is added based on their frequencies by using the compareTo() method defined in Word.java
	private void sortMap() {
		Set<String> keys = map.keySet(); // Add each key contained in the Map to a Set. O(1)
	
		// Iterate over each of the keys in the Set, instantiate a new Word object, and add it to the PriorityQueue. 
		// O(n log n)
		for(String key : keys)
			wordQ.offer(new Word(key, map.get(key))); // Offering to a PriorityQueue has a time complexity of O(log n) 
	}
}
