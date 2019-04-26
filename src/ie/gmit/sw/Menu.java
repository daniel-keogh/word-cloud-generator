package ie.gmit.sw;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
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
			
			// Set the imageFileName to be the same as the inputFileName.png by default.
			// Removes invalid chars in case a path was entered.
			if (inputFileName.contains("."))
				imageFileName = removeInvalidChars(inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".png");	
			else 
				imageFileName = removeInvalidChars(inputFileName + ".png");
		}	
		else {
			contentIsFromFile = false;
			
			setURL();
			
			// make sure jsoup is available & set the name the of image file to the pages title.
			try {
				Document doc = Jsoup.connect(url).get(); 
				imageFileName = removeInvalidChars(doc.title()+".png").trim();
			} catch (NoClassDefFoundError e) {
				System.out.println("[Error] No jsoup.jar file was found.\nPlease visit https://jsoup.org/ and download the latest version in order to parse a URL.");
				System.exit(0);
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
			System.out.print("Enter the path to the input file: ");
			userInput = console.nextLine().trim();

			// verify that the file exists
			if (new File(userInput).isFile())
				fileExists = true;
			else
				System.out.printf("\"%s\" could not be found. Try again.\n", userInput);
		} while (!fileExists);
		
		inputFileName = userInput;
	}
	
	public String getURL() {
		return url;
	}
	
	public void setURL() {
		String url;
		boolean invalid = true;
		
		do {
			System.out.print("Enter the URL you want to parse: ");
			url = console.next().trim();
			
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;

			try {
				System.out.println("Connecting...");
			    new URL(url).openConnection().connect();
			} catch (Exception e) {
			    System.out.println("[Error] Unable connect to "+ url +"\nCheck your network connection and make sure the URL entered is valid.");
			    continue;
			}
			
			invalid = false;
			this.url = url;
		} while(invalid);
	}

	public String getImageFileName() {
		return imageFileName;
	}
	
	// Set the file name of the word-cloud image to save.
	public void setImageFileName() {
		Scanner console = new Scanner(System.in);
		boolean invalid = true;
		String input;
		
		do {
			System.out.printf("Enter a name for the PNG file (current is \"%s\"): ", imageFileName);
			input = console.nextLine().trim();
			
			if (!input.isEmpty()) {
				imageFileName = removeInvalidChars(input);
				invalid = false;
			}
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

	// Sets the maximum number of words to display.
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

	// Opens the ignorewords.txt file in the default text editor
	public void editIgnoreFile() {		
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().edit(new File(Parser.getIgnoreFileName()));
			} catch (IllegalArgumentException e) {
				System.out.printf("[Error] Cannot open the file \"%s\". File not found.\n", Parser.getIgnoreFileName());
			} catch (UnsupportedOperationException e) {
				System.out.printf("[Error] Cannot open the file \"%s\". Action not supported on the current platform.\n", Parser.getIgnoreFileName());
			} catch (Exception e) {
				System.out.printf("[Error] Cannot open the file \"%s\".\n", Parser.getIgnoreFileName());
			}
		}
		else {
			System.out.printf("[Error] Cannot open the file \"%s\". Desktop not supported.\n", Parser.getIgnoreFileName());
		}	
	}
	
	// Ask to open the outputted png file in the default image viewer
	public void openImgFile() {
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
				}
				else
					System.out.printf("[Error] Cannot open the file \"%s\". Desktop not supported.\n", imageFileName);
			}
		}
	}
	
	// Removes any illegal characters in (Windows) filenames & replaces them with underscores.
	private String removeInvalidChars(String filename) {
		// Source: CoderCroc - https://stackoverflow.com/a/31564206
		return filename.replaceAll("[\\\\\\\\/:*?\\\"<>|]", "_");
	}
	
	public boolean isContentFromFile() {
		return contentIsFromFile ? true : false;
	}

	@Override
	public String toString() {
		return "Menu [inputFileName=" + inputFileName + ", url=" + url + ", imageFileName=" + imageFileName
				+ ", maxWords=" + maxWords + ", contentIsFromFile=" + contentIsFromFile + "]";
	}
}