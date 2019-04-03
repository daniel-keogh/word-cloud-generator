package ie.gmit.sw;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Runner {
	
	private static Scanner console = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {
		
		double runTime;
		
		Menu menu = new Menu();
		menu.displayMenu();
		
		Parser parser = new Parser();
		parser.parseIgnoreFile();
		
		runTime = System.currentTimeMillis();
		
		if (menu.isContentFromFile())
			parser.parseInput(new FileInputStream(menu.getInputFileName()));
		else
			parser.parseInput(menu.getURL());

		Queue<Word> wordQ = parser.getWordQueue();
		
		//outputToFile(wordQ);
		
		ImageGenerator ig = new ImageGenerator();
		ig.drawWords(wordQ, menu.getMaxWords());
		ImageIO.write(ig.getImage(), "png", new File(menu.getImageFileName()));
		
		System.out.printf("%d unique words read in %.2f (s).\n", parser.numUniqueWordsRead(), procTime(runTime));
		openImgFile(menu);
	}
	
	// Ask the user if they want to open the outputted png file in their default image viewer
	private static void openImgFile(Menu menu) {
		
		if (new File(menu.getImageFileName()).isFile()) {
			System.out.print("Do you want to view the output file (y/N)? ");
			
			if (Character.toUpperCase(console.next().charAt(0)) == 'Y') {
				if (Desktop.isDesktopSupported()) {
					File pngFile = new File(menu.getImageFileName());
					try {
						Desktop.getDesktop().open(pngFile);
					} catch (IllegalArgumentException e) {
						System.out.printf("[Error] Cannot open the file \"%s\". File not found.\n", menu.getImageFileName());
					} catch (IOException e) {
						System.out.printf("[Error] Cannot open the file \"%s\".\n", menu.getImageFileName());
					} 
				}
				else {
					System.out.printf("[Error] Cannot open the file \"%s\" (Desktop not supported).\n", menu.getImageFileName());
				}	
			}
		}
	}
	
	private static double procTime(double start) {
		return (System.currentTimeMillis() - start) / 1000;
	}
	
	// Output all the words in the PriorityQueue to a file (debugging).
	private static void outputToFile(Queue<Word> wordQ) throws Exception {
		int counter = 0;
		PrintWriter pw = new PrintWriter("DEBUG.txt");
		while(counter < wordQ.size()) { // calling size() is O(1)
			pw.println(wordQ.poll()); // polling from a PriorityQueue is O(log n)
			counter++;
		}
		pw.close();
	}
}
