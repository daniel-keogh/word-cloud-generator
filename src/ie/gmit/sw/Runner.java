package ie.gmit.sw;

import java.io.File;
import java.io.FileInputStream;
import java.util.Queue;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Runner {
	
	private static Scanner console = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception {
		
		boolean outWordsToFile;
		double startTime;
		
		Menu menu = new Menu();
		menu.displayMenu();
		
		Parser parser = new Parser();
		parser.parseIgnoreFile();
		
		System.out.print("Do you want to output the words and their respective frequencies to a file (y/N)? ");
		outWordsToFile = (Character.toUpperCase(console.next().charAt(0)) == 'Y') ? true : false;
		
		startTime = System.currentTimeMillis();
		
		if (menu.isContentFromFile())
			parser.parseInput(new FileInputStream(menu.getInputFileName()));
		else
			parser.parseInput(menu.getURL());

		Queue<Word> wordQ = parser.getWordQueue();
		
		if (outWordsToFile)
			parser.outputToFile(menu.getImageFileName().substring(0, menu.getImageFileName().lastIndexOf('.')) + "_Freq.txt", menu.getMaxWords());
		
		ImageGenerator ig = new ImageGenerator();
		ig.drawWords(wordQ, menu.getMaxWords());
		ImageIO.write(ig.getImage(), "png", new File(menu.getImageFileName()));
		
		System.out.printf("\n%d unique words read in %.2f (s).\n", parser.numUniqueWordsRead(), ProcTime.calc(startTime));
		menu.openImgFile();
	}
}
