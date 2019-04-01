package ie.gmit.sw;

import java.util.concurrent.ThreadLocalRandom;

public class GetRandom {

	// Generate a random number between a given range
	public static int generate(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
