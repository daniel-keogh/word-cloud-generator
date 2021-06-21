package ie.gmit.sw.ui;

import ie.gmit.sw.helpers.Random;

import java.awt.Color;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class Colours {
    private static Background bg = Background.DARK;
    private static final Set<String> customColourScheme = new HashSet<>();
    private static final Scanner console = new Scanner(System.in);

    private Colours() {
    }

    public static String getBackgroundColour() {
        return bg.colour();
    }

    /**
     * Retrieves the colours of the words to be displayed in the image.
     * If a custom colour scheme was set at the menu then that one is used, otherwise one of the defaults is randomly selected.
     */
    public static String[] getColourScheme() {
        if (!customColourScheme.isEmpty()) {
            return customColourScheme.toArray(new String[0]);
        } else {
            DefaultColours[] values = DefaultColours.values();
            return values[Random.generate(values.length - 1)].list();
        }
    }

    public static void setCustomColourScheme() {
        if (!customColourScheme.isEmpty()) {
            customColourScheme.clear();
        }

        int numColours = setNumColours();

        if (numColours != 0) {
            System.out.printf("Enter %d hex colour codes to use (format: #ff0000):\n", numColours);

            while (customColourScheme.size() < numColours) {
                System.out.printf("%d: ", customColourScheme.size() + 1);

                String colour = prependHash(console.next());

                if (!isColourCodeValid(colour)) {
                    System.out.print("[Error] Invalid colour. Try again:\n");
                    continue;
                }

                // Make sure the colour wasn't added already.
                if (customColourScheme.contains(colour)) {
                    System.out.println("You already entered that colour. Enter a different one.");
                } else {
                    customColourScheme.add(colour);
                }
            }
        }

        setBgColour();
    }

    private static int setNumColours() {
        while (true) {
            try {
                System.out.print("How many different colours do you want to use? ");
                int numColours = console.nextInt();

                if (numColours < 0) {
                    throw new InputMismatchException();
                } else {
                    return numColours;
                }
            } catch (InputMismatchException e) {
                System.out.println("[Error] Enter a positive integer (or 0 to just use a default colour scheme).");
                console.next();
            }
        }
    }

    private static void setBgColour() {
        char ans;

        do {
            System.out.print("Do you want a light (L) or dark (D) background? ");
            ans = Character.toUpperCase(console.next().charAt(0));
            bg = ans == 'L' ? Background.LIGHT : Background.DARK;
        } while (ans != 'L' && ans != 'D');
    }

    /**
     * Prepends a '#' if it wasn't added by the user.
     */
    private static String prependHash(String colour) {
        return (!colour.startsWith("#")) ? "#" + colour : colour;
    }

    private static boolean isColourCodeValid(String colour) {
        try {
            Color.decode(colour);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
