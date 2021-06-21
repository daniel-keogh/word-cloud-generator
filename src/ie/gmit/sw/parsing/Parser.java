package ie.gmit.sw.parsing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import ie.gmit.sw.Word;

public abstract class Parser {
    public static final String IGNORE_FILE_NAME = "ignorewords.txt";

    private final Set<String> ignoreSet = new HashSet<>();
    private final Map<String, Integer> map = new HashMap<>();
    private final Queue<Word> wordQ = new PriorityQueue<>();

    public Queue<Word> getWordQueue() {
        return wordQ;
    }

    public int numUniqueWordsRead() {
        return map.size();
    }

    public abstract void parse(String uri) throws IOException;

    /**
     * Add each word in the ignorewords.txt file (if it exists) to a Set (use a set since there's no need for duplicates).
     * Hash tables store objects at arbitrary locations and offer an average O(1) for insertion.
     */
    public void parseIgnoreFile() throws IOException {
        try (BufferedReader ignoreFile = new BufferedReader(new FileReader(IGNORE_FILE_NAME))) {
            String next;

            while ((next = ignoreFile.readLine()) != null) {
                if (next.trim().isEmpty())
                    continue;

                ignoreSet.add(next.toLowerCase());
            }
        }
    }

    /**
     * Outputs all the words in the Queue to a file.
     */
    public void outputToFile(String filename, int numWords) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(filename)) {
            int counter = 0;

            // Copy the Queue so the words in wordQ aren't polled prematurely
            // Add all of the elements in wordQ to copyQ
            Queue<Word> copyQ = new PriorityQueue<>(wordQ);

            while (counter < copyQ.size() && counter < numWords) {
                pw.println(copyQ.poll());
                counter++;
            }
        }
    }

    protected void processWords(String[] words) {
        for (String word : words) {
            word = stripPunctuation(word).trim();

            // Only add words that aren't blacklisted.
            if (!ignoreSet.contains(word)) {
                if (!word.isEmpty()) {
                    updateMap(word);
                }
            }
        }
    }

    /**
     * Add each word in the Map to a PriorityQueue.
     * Words are sorted each time a new element is added based on their frequencies.
     */
    protected void sortMap() {
        // Iterate over each of the keys in the Set, instantiate a new Word object,
        // and add it to the PriorityQueue.
        for (String key : map.keySet()) {
            wordQ.offer(new Word(key, map.get(key)));
        }
    }

    private void updateMap(String word) {
        // If the word already exists in the Map, its frequency is retrieved and then incremented.
        if (map.containsKey(word)) {
            int freq = map.get(word);
            map.put(word, ++freq);
        } else {
            // First occurrence of this word: Set the frequency to 1
            map.put(word, 1);
        }
    }

    private static String stripPunctuation(String word) {
        // Source: Patrick Parker - https://stackoverflow.com/a/43171912
        return word.replaceAll("(?:(?<!\\S)\\p{Punct}+)|(?:\\p{Punct}+(?!\\S))", "");
    }
}
