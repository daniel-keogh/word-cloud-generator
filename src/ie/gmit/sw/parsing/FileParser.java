package ie.gmit.sw.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileParser extends Parser {
    @Override
    public void parse(String path) throws IOException {
        try (BufferedReader inputFile = new BufferedReader(new FileReader(path))) {
            String next;

            while ((next = inputFile.readLine()) != null) {
                // Add every word in each line of the file to an array, using spaces as delimiters.
                String[] words = next.toLowerCase().split(" ");
                processWords(words);
            }

            sortMap();
        }
    }
}
