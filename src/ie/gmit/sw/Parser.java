package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	
	// Add each word in the ignorewords.txt file (if it exists) to a Set (use a set since there's no need for duplicates).
	// Hash tables store objects at arbitrary locations and offer an average O(1) for insertion.
	public void parseIgnoreFile() {
		try {
			BufferedReader ignoreFile = new BufferedReader(new FileReader(IGNORE_FILE_NAME));
			String next = null;

			while ((next = ignoreFile.readLine()) != null) {

				if (next.trim().isEmpty())
					continue;

				ignoreSet.add(next.toLowerCase()); // Adding to a HashSet is O(1)
			}
			ignoreFile.close();
		} catch (IOException e) {
			System.out.printf("[Warning] No \"%s\" file was found. Proceed anyway (y/N)? ", IGNORE_FILE_NAME);

			if (Character.toUpperCase(console.next().charAt(0)) != 'Y')
				System.exit(0);
		}
	}
	
	public void parseInput(InputStream in) throws Exception {

		BufferedReader inputFile = new BufferedReader(new InputStreamReader(in));
		String next = null;

		// O(n^2) - Loop within a loop
		while ((next = inputFile.readLine()) != null) {
			String[] words = next.toLowerCase().split(" "); // Add every word in each line of the file to an array, using spaces as delimiters. O(n)
			processWords(words);
		}
		sortMap();
		in.close();
	}

	public void parseInput(String url) {

		try {
			Document doc = Jsoup.connect(url).get();
			 // extract the text between selected tags
			String content = doc.select("p, h1, h2, h3, h4, h5, h6").text();
			String[] words = content.toLowerCase().split(" ");	// Add every word in content to an array, using spaces as delimiters. O(n)
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
			word = stripPunctuation(word).trim();
			// Searching a HashSet (i.e. calling contains()) is O(1).
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
	// Words are sorted each time a new element is added based on their frequencies by using the Comparable implemented in Word.java
	private void sortMap() {
		Set<String> keys = map.keySet(); // Add each key contained in the Map to a Set. O(1)
	
		// Iterate over each of the keys in the Set, instantiate a new Word object, and add it to the PriorityQueue. 
		// O(n log n) 
		for(String key : keys)
			wordQ.offer(new Word(key, map.get(key))); // Offering to a PriorityQueue has a time complexity of O(log n) 
	}
	
	public void outputToFile(String filename, int numWords) throws FileNotFoundException {
		int counter = 0;
		PrintWriter pw = new PrintWriter(filename);

		// Copy the Queue so the words in wordQ aren't polled prematurely
		Queue<Word> copyQ = new PriorityQueue<Word>();
		// Add all of the elements in wordQ to copyQ
		// Calling add() on a PriorityQueue is O(log n) so addAll() is O(k log n), where k is the number of elements in wordQ
		copyQ.addAll(wordQ);
		
		// O(n log n) - Because there are n iterations & polling from a PriorityQueue is O(log n)
		while(counter < copyQ.size() && counter < numWords) {
			pw.println(copyQ.poll());
			counter++;
		}
		pw.close();
	}
}
