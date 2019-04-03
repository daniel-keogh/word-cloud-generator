package ie.gmit.sw;

import java.awt.Color;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class Colours {

	private static Set<String> customColourScheme = new HashSet<>(); 
	private static Scanner console = new Scanner(System.in);
	
	// Some default colour schemes to use
	private static String[] monokai = {"#ff6188", "#a9dc76", "#ffd866", "#ab9df2", "#78dce8"};
	private static String[] blueBeach = {"#dcd3ca", "#b29c89", "#4aa49e", "#166470", "#01456d"};
	private static String[] material = {"#03a9f4", "#3f51b5", "#673ab7", "#e91e63", "#f44336"};
	private static String[] solarizedAccents = {"#d33682", "#cb4b16", "#dc2f2f", "#268bd2", "#859900"};
	
	private static final String darkBackgroundColour = "#3b3539";
	private static final String lightBackgroundColour = "#f2f2f2";
	
	private static boolean useDarkBG = true;
	
	public static String getBackgroundColour() {
		if (useDarkBG)
			return darkBackgroundColour;
		else
			return lightBackgroundColour;
	}
	
	// Retrieves the colours of the words to be displayed in the image. 
	// If a custom colour scheme was set at the menu then use that one, otherwise randomly select one of the defaults.
	public static String[] getColourScheme() {
		
		if (!customColourScheme.isEmpty()) { // isEmpty() runs in constant time O(1). 
			String[] customColours = new String[customColourScheme.size()]; // calling size() on a HashSet is O(1)
			customColours = customColourScheme.toArray(customColours); 	// Convert the Set to a String[] - O(n)
			return customColours;
		}	
		else {			
			String[][] defaultColourSchemes = {monokai, blueBeach, material, solarizedAccents};	
			// GetRandom.generate() returns a random number as the index
			// Accessing an array at a known index is O(1)
			return defaultColourSchemes[GetRandom.generate(0, defaultColourSchemes.length - 1)];
		}
	}
	
	public static void setCustomColourScheme() {
		final int COLOUR_CODE_LEN = 7;
		String colour;
		char ans;
		int numColours = 0;
		boolean invalid = true;

		// Make sure the Set is empty (in case the user already set a custom colour scheme).
		if (!customColourScheme.isEmpty()) // isEmpty() returns the result of size == 0 and runs in constant time O(1).
			customColourScheme.clear(); // HashSet clear() calls map.clear() which iterates over each element in the structure and sets them to null O(n).
		
		do {
			try {
				System.out.print("How many different colours do you want to use? ");
				numColours = console.nextInt();
				
				if (numColours < 0) {
					System.out.println("Please enter a positive integer (or 0 to just use a default).");
					continue;
				}
				else 
					invalid = false;
			} catch (InputMismatchException e) {
				System.out.println("[Error] Please enter an integer.");
				console.next();
				continue;
			} 
		} while (invalid);
		
		
		if (numColours != 0) {
			System.out.printf("Enter %d hex colour codes to use (eg. format: #ff0000):\n", numColours);
			
			int counter = 1;
			while (customColourScheme.size() < numColours) { // size() runs in constant time
				
				System.out.printf("%d: ", counter);
				colour = console.next();
				
				if (!colour.startsWith("#")) // append a '#' if it wasn't added by the user
					colour = "#" + colour;

				if (colour.length() == COLOUR_CODE_LEN) {
					try {
						Color.decode(colour);
					} catch (NumberFormatException e) {
						System.out.print("[Error] That isn't a valid colour. Try again:\n");
						continue;
					}				
					
					// A Set is used over an ArrayList to prevent duplicates & because calling contains() on a HashSet is O(1) compared to O(n) on a List.
					if (customColourScheme.contains(colour)) {			
						System.out.println("You already entered that colour. Enter a different one.");
						continue;
					}

					customColourScheme.add(colour); // Adding to a HashSet is constant time
					counter++;			
				}
				else
					System.out.print("[Error] That isn't a valid colour. Try again:\n");
			}
		}
		
		do {
			System.out.print("Do you want a light (l) or dark (d) background? ");
			
			ans = Character.toUpperCase(console.next().charAt(0));
			if (ans == 'L')
				useDarkBG = false;
		} while (ans != 'L' && ans != 'D');
	}
}
