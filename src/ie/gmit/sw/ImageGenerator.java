package ie.gmit.sw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Queue;

public class ImageGenerator {

	private static final int HEIGHT = 600;
	private static final int WIDTH = 800;
	private static final int OFFSET = 150;
	private static final double MAX_FONT_SIZE = 100;
	private static final double MIN_FONT_SIZE = 10;
	private static final int[] FONT_STYLE = {Font.PLAIN, Font.BOLD, Font.ITALIC};
	private static final String[] FONT_FAMILY = {Font.SERIF, Font.SANS_SERIF};
	
	private BufferedImage image;
	
	public ImageGenerator() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public void drawWords(Queue<Word> wordQ, int maxWords) {
		int j, counter = 0, max = 0, min = 0;
		
		String[] colorScheme = Colours.getColourScheme();
		Graphics2D graphics = image.createGraphics(); 

		// Set the colour of the background
		graphics.setColor(Color.decode(Colours.getBackgroundColour()));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		
		Iterator<Word> i = wordQ.iterator(); // wordQ.Iterator() returns an iterator over the words in wordQ. Calling iterator() is constant time.
		
		Word word = new Word();
		
		// O(n^2 log n) i think
		while (i.hasNext() && counter < maxWords) { 
			j = i.next().getFrequency();
			if (j > max) max = j;
			if (j < min) min = j;
			
			// O(n log n) - Because there are n iterations & polling from a PriorityQueue is O(log n)
			while ((!wordQ.isEmpty()) && counter < maxWords) { // isEmpty() runs in constant time O(1). 
				word = wordQ.poll(); // returns the highest priority item in the queue.
				
				graphics.setFont(new Font(FONT_FAMILY[GetRandom.generate(FONT_FAMILY.length - 1)], FONT_STYLE[GetRandom.generate(FONT_STYLE.length - 1)], scale(word.getFrequency(), min, max)));
				graphics.setColor(Color.decode(colorScheme[GetRandom.generate(colorScheme.length - 1)])); // Accessing an array index is O(1)
				graphics.drawString(word.getWord(), GetRandom.generate(WIDTH - OFFSET), GetRandom.generate(HEIGHT));
				counter++;
			}	
		}
		graphics.dispose();
	}
	
	// scales a word in accordance with its frequency
	private int scale(double freq, double min, double max) {
		return (int)Math.ceil((MAX_FONT_SIZE - MIN_FONT_SIZE) * (freq - min) / (max - min) + MIN_FONT_SIZE);
	}
}
