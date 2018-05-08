import java.util.Map;

public class CNN {
    
	private Map<int[], Integer> lol;
	
	// 3d input array
    public double[][][] maxPoolForward(double[][][] data) {
        double[][][] results = new double[data.length][][];
        int i = 0;
        for (double[][] item : data) {
            double[][] reduced = maxPool(item);
            results[i++] = reduced;
        }
        return results;
    }
}
