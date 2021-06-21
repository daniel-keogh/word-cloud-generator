package ie.gmit.sw.parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UrlParser extends Parser {
    @Override
    public void parse(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        // Extract the text between selected tags
        String content = doc.select("p, h1, h2, h3, h4, h5, h6").text();

        // Add every word in content to an array, using spaces as delimiters.
        String[] words = content.toLowerCase().split(" ");

        processWords(words);
        sortMap();
    }
}
