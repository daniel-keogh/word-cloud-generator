package ie.gmit.sw;

public class ProcTime {

	public static double calc(double startTime) {
		return (System.currentTimeMillis() - startTime) / 1000;
	}
}
