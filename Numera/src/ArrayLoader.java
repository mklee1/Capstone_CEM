import java.util.Scanner;

public class ArrayLoader {
    
	private static final String FILE1;
	private static final String FILE2;
	private static final String FILE3;
	private static final String FILE4;
	
	private final Double[][][][] X;
	private final Double[][][][] conv_w;
	private final Double[][] conv_b;
	private final Double[][] FC_W;
	
	private Double[][][][] load4DArray(String filename) {
		Double[][][][] res = new Double[32][1][3][3];
		try (Scanner sc = new Scanner(filename)) {
			while (sc.hasNext()) {
				String line = sc.nextLine();
				String sub = line.substring(0, 3);
				// case 1
				if (sub.equals("[[[")) {
					
				}
				// case 2
				else if (sub.equals("  [")) {
					
				}
			}
		}
	}
	public ArrayLoader() {
		X = null;
		conv_w = null;
		conv_b = null;
		FC_W = null;
	}
}
