
public class Testing {
    
	public static void print(Double[][] a) {
    	for (int i = 0; i < a.length; i++) {
    		for (int j = 0; j < a[0].length; j++) {
    			System.out.print(a[i][j] + " ");
    		}
    		System.out.println();
    	}
    }
	
	public static Double[][] ones(int x, int y) {
		Double[][] res = new Double[x][y];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				res[i][j] = 1.0;
			}
		}
		return res;
	}
	
	public static Double[][][] ones(int x, int y, int z) {
		Double[][][] res = new Double[x][y][z];
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < z; k++) {
					res[i][j][k] = 1.0;
				}
			}
		}
		return res;
	}
	
	public static Double[][][][] ones(int x1, int x2, int x3, int x4) {
		Double[][][][] res = new Double[x1][x2][x3][x4];
		for (int i = 0; i < x1; i++) {
			for (int j = 0; j < x2; j++) {
				for (int k = 0; k < x3; k++) {
					for (int l = 0; l < x4; l++) {
						res[i][j][k][l] = 1.0;
					}
				}
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
		Double[][][][] X = NP.reshape(NP.arange(28 * 28), 1, 1, 28, 28);
		Double[][][][] conv_W = ones(32, 1, 3, 3);
	    Double[][] conv_b = NP.reshape1D(NP.arange(32), 32, 1);
	    //print(conv_b);
	    Double[][] FC_W = ones(7200, 10);
	    Double[][] FC_b = NP.reshape1D(NP.arange(10), 1, 10);
	    NP.predictSingle(X, conv_W, conv_b, FC_W, FC_b);
	}
}
