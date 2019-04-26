package ie.gmit.sw;

import java.util.concurrent.ThreadLocalRandom;

public class GetRandom {
	
	public static int generate(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	public static int generate(int max) {
		return ThreadLocalRandom.current().nextInt(0, max + 1);
	}
}
