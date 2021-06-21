package ie.gmit.sw.ui;

import ie.gmit.sw.Word;
import ie.gmit.sw.helpers.Random;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;

public class ImageGenerator {
    private final int imageHeight;
    private final int imageWidth;
    private final double maxFontSize;
    private final double minFontSize;
    private final String outputFormat;
    private final Integer[] fontStyles;
    private final String[] fontFamilies;

    private final BufferedImage image;

    public ImageGenerator(Builder builder) {
        imageHeight = builder.imageHeight;
        imageWidth = builder.imageWidth;
        maxFontSize = builder.maxFontSize;
        minFontSize = builder.minFontSize;
        outputFormat = builder.outputFormat;
        fontStyles = builder.fontStyles;
        fontFamilies = builder.fontFamilies;

        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public void drawWords(Queue<Word> wordQ, int maxWords) {
        int max = 0;
        int min = 0;

        String[] colorScheme = Colours.getColourScheme();

        Graphics2D graphics = image.createGraphics();

        // Set the colour of the background
        graphics.setColor(Color.decode(Colours.getBackgroundColour()));
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        int counter = 0;
        Iterator<Word> i = wordQ.iterator();

        while (i.hasNext() && counter < maxWords) {
            int j = i.next().getFrequency();

            if (j > max) max = j;
            if (j < min) min = j;

            while (!wordQ.isEmpty() && counter < maxWords) {
                // Get the highest priority item in the queue.
                Word word = wordQ.poll();

                graphics.setFont(new Font(
                        fontFamilies[Random.generate(fontFamilies)],
                        fontStyles[Random.generate(fontStyles)],
                        scale(word.getFrequency(), min, max)
                ));
                graphics.setColor(Color.decode(colorScheme[Random.generate(colorScheme)]));

                // Determine coordinates
                FontMetrics fm = graphics.getFontMetrics();

                int x = Random.generate(imageWidth - fm.stringWidth(word.getWord()));
                int y = Random.generate(fm.getHeight(), imageHeight);

                graphics.drawString(word.getWord(), x, y);
                counter++;
            }
        }

        graphics.dispose();
    }

    public void write(String path) throws IOException {
        ImageIO.write(image, outputFormat, new File(path));
    }

    /**
     * Scales a word in accordance with its frequency.
     */
    private int scale(double freq, double min, double max) {
        return (int) Math.floor((maxFontSize - minFontSize) * (freq - min) / (max - min) + minFontSize);
    }

    public static class Builder {
        private int imageHeight = 600;
        private int imageWidth = 800;
        private double maxFontSize = 100;
        private double minFontSize = 10;
        private String outputFormat = "png";
        private Integer[] fontStyles = {Font.PLAIN, Font.BOLD, Font.ITALIC};
        private String[] fontFamilies = {Font.SERIF, Font.SANS_SERIF};

        public Builder imageHeight(int value) {
            imageHeight = value;
            return this;
        }

        public Builder imageWidth(int value) {
            imageWidth = value;
            return this;
        }

        public Builder maxFontSize(double value) {
            maxFontSize = value;
            return this;
        }

        public Builder minFontSize(double value) {
            minFontSize = value;
            return this;
        }

        public Builder outputFormat(String value) {
            outputFormat = value;
            return this;
        }

        public Builder fontStyles(Integer... value) {
            fontStyles = value;
            return this;
        }

        public Builder fontFamilies(String... value) {
            fontFamilies = value;
            return this;
        }

        public ImageGenerator build() {
            return new ImageGenerator(this);
        }
    }
}
