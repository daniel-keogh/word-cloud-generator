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
	private static final double MAX_FONT_SIZE = 100;
	private static final double MIN_FONT_SIZE = 10;
	
	private static int[] fontStyle = {Font.PLAIN, Font.BOLD, Font.ITALIC};
	private static String[] fontFamily = {Font.SERIF, Font.SANS_SERIF};
	
	private BufferedImage image;
	
	public ImageGenerator() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public void drawWords(Queue<Word> wordQ, int maxWords) {
		int j, counter = 0, max = 0, min = 0, offset = 150;
		
		String[] colorScheme = Colours.getColourScheme();
		Graphics2D graphics = image.createGraphics(); 

		// Set the colour of the background
		graphics.setColor(Color.decode(Colours.getBackgroundColour()));
		graphics.fillRect(0, 0, WIDTH, HEIGHT);
		
		Iterator<Word> i = wordQ.iterator(); // wordQ.Iterator() returns an iterator over the words in wordQ. Calling iterator() is constant time.
		
		Word wordFreq = new Word();
		
		while (i.hasNext() && counter < maxWords) { // Iterating over the elements in i is O(n)
			j = i.next().getFrequency();
			if (j > max) max = j;
			if (j < min) min = j;
			
			while ((!wordQ.isEmpty()) && counter < maxWords) { // isEmpty runs in constant time O(1). 
				wordFreq = wordQ.poll(); // Calling poll() will return the highest priority item in the queue. O(log n)
				
				graphics.setFont(new Font(fontFamily[GetRandom.generate(0, fontFamily.length - 1)], fontStyle[GetRandom.generate(0, fontStyle.length - 1)], scale(wordFreq.getFrequency(), min, max)));
				graphics.setColor(Color.decode(colorScheme[GetRandom.generate(0, colorScheme.length - 1)])); // Accessing an array index is O(1)
				graphics.drawString(wordFreq.getWord(), GetRandom.generate(0, WIDTH - offset), GetRandom.generate(0, HEIGHT));
				counter++;
			}	
		}
		graphics.dispose();
	}
	
	// Scale a word in accordance with its frequency
	private int scale(double freq, double min, double max) {
		return (int)Math.ceil((MAX_FONT_SIZE - MIN_FONT_SIZE) * (freq - min) / (max - min) + MIN_FONT_SIZE);
	}
}
