package ie.gmit.sw.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import ie.gmit.sw.parsing.FileParser;
import ie.gmit.sw.parsing.Parser;
import ie.gmit.sw.Word;
import ie.gmit.sw.parsing.UrlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Menu {
    private static final int DEFAULT_MAX_WORDS = 35;
    private static final Scanner console = new Scanner(System.in);

    private final Map<Character, MenuCommand> options = new HashMap<>();

    private Parser parser;
    private ContentType contentType;
    private String inputUri;
    private String imageFileName;
    private int maxWords = DEFAULT_MAX_WORDS;

    private Menu() {
        options.put('A', this::generateWordCloud);
        options.put('B', this::setMaxWords);
        options.put('C', this::setImageFileName);
        options.put('D', Colours::setCustomColourScheme);
        options.put('E', this::editIgnoreFile);
        options.put('F', this::setInputUri);
        options.put('Q', () -> System.exit(0));
    }

    public static Menu createMenu() {
        System.out.println("============================================================================");
        System.out.println("|                            Word Cloud Generator                          |");
        System.out.println("============================================================================");

        Menu menu = new Menu();
        menu.setContentType();
        menu.setInputUri();
        menu.autoSetImageFileName();
        return menu;
    }

    /**
     * A command-line menu that presents the user with the available options.
     */
    public void display() {
        System.out.println("============================================================================");
        System.out.println("|                                   Menu                                   |");
        System.out.println("============================================================================");
        System.out.println("| Options:                                                                 |");
        System.out.println("|  A. Generate the word-cloud.                                             |");
        System.out.println("|  B. Set the maximum number of words to display.                          |");
        System.out.println("|  C. Set the name of the generated word cloud image file.                 |");
        System.out.println("|  D. Choose a custom colour scheme to use.                                |");
        System.out.println("|  E. Edit the ignored words txt file.                                     |");

        if (contentType == ContentType.FILE) {
            System.out.println("|  F. Re-enter the name of the input file.                                 |");
        } else {
            System.out.println("|  F. Re-enter the URL.                                                    |");
        }

        System.out.println("|  Q. Quit.                                                                |");
        System.out.println("============================================================================");

        while (true) {
            System.out.print("\nSelect an option listed above: ");
            char option = console.next().toUpperCase().charAt(0);

            MenuCommand cmd = options.getOrDefault(option, () -> System.out.println("[Error] Please enter one of the options provided."));
            cmd.execute();

            if (option == 'A')
                return;
        }
    }

    private void setContentType() {
        String input;

        do {
            System.out.print("Do you want to parse a text file (f) or a URL (url)? ");
            input = console.next().toUpperCase();
        } while (!input.equals("F") && !input.equals("URL"));

        if (input.equals("F")) {
            contentType = ContentType.FILE;
            parser = new FileParser();
        } else {
            contentType = ContentType.URL;
            parser = new UrlParser();
        }
    }

    private void setInputUri() {
        if (contentType == ContentType.FILE) {
            setInputFileName();
        } else {
            setUrl();
        }
    }

    private void setInputFileName() {
        Scanner console = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the path to the input file: ");
            String userInput = console.nextLine().trim();

            // verify that the file exists
            if (new File(userInput).isFile()) {
                inputUri = userInput;
                return;
            } else {
                System.out.printf("\"%s\" could not be found. Try again.\n", userInput);
            }
        }
    }

    private void setUrl() {
        while (true) {
            System.out.print("Enter the URL you want to parse: ");
            String url = console.next().trim();

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            try {
                System.out.println("Connecting...");
                new URL(url).openConnection().connect();
            } catch (Exception e) {
                System.out.println("[Error] Unable connect to " + url + "\nCheck your network connection and make sure the URL entered is valid.");
                continue;
            }

            inputUri = url;
            return;
        }
    }

    /**
     * Automatically sets the file name of the word-cloud image.
     */
    private void autoSetImageFileName() {
        if (contentType == ContentType.FILE) {
            // Set the imageFileName to be the same as the inputFileName.png by default.
            // Removes invalid chars in case a path was entered.
            if (inputUri.contains(".")) {
                imageFileName = removeInvalidChars(inputUri.substring(0, inputUri.lastIndexOf('.')) + ".png");
            } else {
                imageFileName = removeInvalidChars(inputUri + ".png");
            }
        } else {
            // Make sure jsoup is available & set the name the of image file to the pages title.
            try {
                Document doc = Jsoup.connect(inputUri).get();
                imageFileName = removeInvalidChars(doc.title() + ".png").trim();
            } catch (NoClassDefFoundError e) {
                System.out.println("[Error] No jsoup.jar file was found.\nPlease visit https://jsoup.org/ and download the latest version in order to parse a URL.");
                System.exit(0);
            } catch (Exception e) {
                imageFileName = "WordcloudURL.png";
            }
        }
    }

    /**
     * Prompt the user to set the file name of the word-cloud image.
     */
    private void setImageFileName() {
        Scanner console = new Scanner(System.in);

        while (true) {
            System.out.printf("Enter a name for the PNG file (current is \"%s\"): ", imageFileName);
            String input = console.nextLine().trim();

            if (!input.isEmpty()) {
                imageFileName = removeInvalidChars(input);

                // Append .png to the filename entered
                if (imageFileName.contains(".")) {
                    imageFileName = imageFileName.substring(0, imageFileName.lastIndexOf('.')) + ".png";
                } else {
                    imageFileName += ".png";
                }

                return;
            }
        }
    }

    /**
     * Sets the maximum number of words to display.
     */
    private void setMaxWords() {
        int maxWords = this.maxWords;
        boolean invalid = true;

        do {
            try {
                System.out.printf("Enter the maximum number of words to display in the word-cloud (current is %d): ", maxWords);
                maxWords = Math.abs(console.nextInt());
            } catch (InputMismatchException e) {
                System.out.println("[Error] Please enter an integer.");
                console.next();
                continue;
            }
            invalid = false;
        } while (invalid);

        this.maxWords = maxWords;
    }

    private void generateWordCloud() {
        try {
            parseIgnoreFile();

            System.out.print("Do you want to output the words and their respective frequencies to a file (y/N)? ");
            boolean outWordsToFile = Character.toUpperCase(console.next().charAt(0)) == 'Y';

            double startTime = System.currentTimeMillis();

            parser.parse(inputUri);
            Queue<Word> wordQ = parser.getWordQueue();

            if (outWordsToFile) {
                String filename = imageFileName.substring(0, imageFileName.lastIndexOf('.')) + "_Freq.txt";
                parser.outputToFile(filename, maxWords);
            }

            ImageGenerator ig = new ImageGenerator.Builder().build();
            ig.drawWords(wordQ, maxWords);
            ig.write(imageFileName);

            System.out.printf("\n%d unique words read in %.2f (s).\n", parser.numUniqueWordsRead(), procTime(startTime));
            openImgFile();
        } catch (IOException e) {
            System.err.println("[Error] IO Error.");
            e.printStackTrace();
        }
    }

    private void parseIgnoreFile() {
        try {
            parser.parseIgnoreFile();
        } catch (IOException e) {
            System.out.printf("[Warning] No \"%s\" file was found. Proceed anyway (y/N)? ", Parser.IGNORE_FILE_NAME);

            if (Character.toUpperCase(console.next().charAt(0)) != 'Y') {
                System.exit(0);
            }
        }
    }

    /**
     * Opens the ignorewords.txt file in the default text editor.
     */
    private void editIgnoreFile() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File(Parser.IGNORE_FILE_NAME));
            } catch (IllegalArgumentException e) {
                System.out.printf("[Error] Cannot open the file \"%s\". File not found.\n", Parser.IGNORE_FILE_NAME);
            } catch (UnsupportedOperationException e) {
                System.out.printf("[Error] Cannot open the file \"%s\". Action not supported on the current platform.\n", Parser.IGNORE_FILE_NAME);
            } catch (Exception e) {
                System.out.printf("[Error] Cannot open the file \"%s\".\n", Parser.IGNORE_FILE_NAME);
            }
        } else {
            System.out.printf("[Error] Cannot open the file \"%s\". Desktop not supported.\n", Parser.IGNORE_FILE_NAME);
        }
    }

    /**
     * Ask to open the outputted png file in the default image viewer.
     */
    private void openImgFile() {
        if (new File(imageFileName).isFile()) {
            System.out.print("Do you want to view the output image file (y/N)? ");

            if (Character.toUpperCase(console.next().charAt(0)) == 'Y') {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(new File(imageFileName));
                    } catch (IllegalArgumentException e) {
                        System.out.printf("[Error] Cannot open the file \"%s\". File not found.\n", imageFileName);
                    } catch (UnsupportedOperationException e) {
                        System.out.printf("[Error] Cannot open the file \"%s\". Action not supported on the current platform.\n", imageFileName);
                    } catch (Exception e) {
                        System.out.printf("[Error] Cannot open the file \"%s\".\n", imageFileName);
                    }
                } else {
                    System.out.printf("[Error] Cannot open the file \"%s\". Desktop not supported.\n", imageFileName);
                }
            }
        }
    }

    /**
     * Removes any illegal characters in (Windows) filenames & replaces them with underscores.
     */
    private static String removeInvalidChars(String filename) {
        // Source: CoderCroc - https://stackoverflow.com/a/31564206
        return filename.replaceAll("[\\\\\\\\/:*?\\\"<>|]", "_");
    }

    private static double procTime(double startTime) {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}