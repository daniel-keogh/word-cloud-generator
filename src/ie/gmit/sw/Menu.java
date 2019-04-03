package ie.gmit.sw;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Menu {

	private static final int DEFAULT_MAX_WORDS = 35;
	
	private static Scanner console = new Scanner(System.in);
	
	private String inputFileName;
	private String url;
	private String imageFileName;
	private int maxWords;
	private boolean contentIsFromFile;

	public Menu() {
		String contentType;
		
		System.out.println("============================================================================");
		System.out.println("|                            Word Cloud Generator                          |");
		System.out.println("============================================================================");
		
		do {
			System.out.print("Do you want to parse a text file (f) or a URL (url)? ");
			contentType = console.next().toUpperCase();
		} while (!contentType.equals("F") && !contentType.equals("URL"));

		// Set some defaults in case the user doesn't enter any options at the menu.
		
		if (contentType.equals("F")) {
			contentIsFromFile = true;
			
			setInputFileName();
			
			// Set the imageFileName to be the same as the inputFileName.png by default
			if (inputFileName.contains("."))
				imageFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".png";	
			else
				imageFileName += ".png";
		}	
		else {
			contentIsFromFile = false;
			
			setURL();
			
			try {
				Document doc = Jsoup.connect(url).get(); 
				imageFileName = doc.title().substring(doc.title().lastIndexOf(" - ")).replace(" - ", "") + ".png";
				imageFileName = removeInvalidChars(imageFileName).trim();
				System.out.println(imageFileName);
			} catch (Exception e) {
				imageFileName = "WordcloudURL.png";
			}
		}
		
		maxWords = DEFAULT_MAX_WORDS;
	}
	
	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName() {
		Scanner console = new Scanner(System.in);
		String userInput;
		boolean fileExists = false;
		
		do {	
			System.out.print("Enter the name of the input file: ");
			userInput = console.nextLine();

			// verify that the file exists
			if (new File(userInput).isFile())
				fileExists = true;
			else
				System.out.printf("The file \"%s\" could not be found. Try again.\n", userInput);
		} while (!fileExists);
		
		inputFileName = userInput;
	}
	
	public void setURL() {
		String url;
		boolean invalid = true;
		
		do {
			System.out.print("Enter the (full) URL you want to parse: ");
			url = console.next();
			
			try {
				System.out.println("Connecting...");
				Jsoup.connect(url);
			}
			catch (IllegalArgumentException e) {
				System.out.println("That is not a valid URL. Try again.");
				continue;
			}	
			
			invalid = false;
			this.url = url;
		} while(invalid);
	}
	
	public String getURL() {
		return url;
	}

	public String getImageFileName() {
		return imageFileName;
	}
	
	// Set the file name of the word-cloud image to save.
	public void setImageFileName() {
		Scanner console = new Scanner(System.in);
		boolean invalid = true;
		
		do {
			System.out.print("Enter a name for the PNG file: ");
			imageFileName = removeInvalidChars(console.nextLine()).trim();
			
			if (!imageFileName.isEmpty())
				invalid = false;
		} while (invalid);
		
		// Append .png to the filename entered
		if (imageFileName.contains("."))
			imageFileName = imageFileName.substring(0, imageFileName.lastIndexOf('.')) + ".png";	
		else
			imageFileName += ".png";
	}

	public int getMaxWords() {
		return maxWords;
	}

	// Returns the maximum number of words to display.
	public void setMaxWords() {
		int maxWords = this.maxWords;
		boolean invalid = true;

		do {
			try {
				System.out.printf("Enter the maximum number of words to display in the wordcloud (current is %d): ", maxWords);
				maxWords = Math.abs(console.nextInt());
			} catch (InputMismatchException e) {
				System.out.println("[Error] Please enter an integer.");
				console.next();
				continue;
			}
			invalid = false;
		} while(invalid);
		
		this.maxWords = maxWords;
	}
	
	// A command-line menu that prompts that present the user with a suite of options.
	public void displayMenu() {
		char option;
		boolean cont = true;

		System.out.println("============================================================================");
		System.out.println("|                                   Menu                                   |");
		System.out.println("============================================================================");
		System.out.println("| Options:                                                                 |");
		System.out.println("|  A. Generate the word cloud.                                             |");
		System.out.println("|  B. Set the maximum number of words to display.                          |");
		System.out.println("|  C. Set the name of the generated word cloud image file.                 |");
		System.out.println("|  D. Choose a custom colour scheme to use.                                |");
		System.out.println("|  E. Edit the file of ignored words.                                      |");
		
		if (contentIsFromFile)
			System.out.println("|  F. Re-enter the name of the input file.                                 |");
		else 
			System.out.println("|  F. Re-enter the URL.                                                    |");
		
		System.out.println("|  Q. Quit.                                                                |");
		System.out.println("============================================================================\n");

		do {
			System.out.print("Select an option listed above: ");
			option = console.next().charAt(0);

			if (!Character.toString(option).matches("[a-fqA-FQ]")) {
				System.out.println("[Error] Please enter one of the options provided.\n");
				continue;
			}

			cont = handleOption(option);
		} while (cont);
	}

	private boolean handleOption(char option) {
		switch (Character.toUpperCase(option)) {
			case 'A':
				return false;
			case 'B':
				setMaxWords();
				break;
			case 'C':
				setImageFileName();
				break;
			case 'D':
				Colours.setCustomColourScheme();
				break;
			case 'E':
				editIgnoreFile();
				break;
			case 'F':
				if (contentIsFromFile)
					setInputFileName();
				else 
					setURL();
				break;
			case 'Q':
				System.exit(0);
			default:
				break;
		}	
		
		return true;
	}

	// Open the ignorewords.txt file in the default text editor
	public void editIgnoreFile() {		
		if (Desktop.isDesktopSupported()) {
			File ignoreFile = new File(Parser.getIgnoreFileName());
			try {
				Desktop.getDesktop().edit(ignoreFile);
			} catch (IllegalArgumentException e) {
				System.out.printf("[Error] Cannot open the file \"%s\". File not found.\n", Parser.getIgnoreFileName());
			} catch (IOException e) {
				System.out.printf("[Error] Cannot open the file \"%s\".\n", Parser.getIgnoreFileName());
			}
		}
		else {
			System.out.printf("[Error] Cannot open the file \"%s\" (Desktop not supported).\n", Parser.getIgnoreFileName());
		}	
	}
	
	// Removes any illegal characters in (Windows) filename & replaces them with underscores.
	private String removeInvalidChars(String filename) {
		// Source: CoderCroc - https://stackoverflow.com/a/31564206
		return filename.replaceAll("[\\\\\\\\/:*?\\\"<>|]", "_");
	}
	
	public boolean isContentFromFile() {
		if (contentIsFromFile)
			return true;
		else 
			return false;
	}

	@Override
	public String toString() {
		return "Menu [inputFileName=" + inputFileName + ", url=" + url + ", imageFileName=" + imageFileName
				+ ", maxWords=" + maxWords + ", contentIsFromFile=" + contentIsFromFile + "]";
	}
}