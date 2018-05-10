import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class NewPrediction {
    
    public static int predictSingle(Double[][][][] X, Double[][][][] conv_W, Double[][] conv_b,
                                    Double[][] FC_W, Double[][] FC_b) {
        List<Object> results = conv_forward(X, conv_W, conv_b);
        Double[][][][] x1 = (Double[][][][]) results.get(0);
        int[] conv_out_dim = (int[]) results.get(1);
        Double[][][][] x2 = relu_forward(x1);
        Double[] x3 = maxpool_forward(x2, conv_out_dim);
        double[][] x4 = flatten_forward(x3);
        double[][] x5 = FC_forward(x4, FC_W, FC_b);
        return 0;
    }
    // helper method which performs matrix multiplication
    public static Double[][] multiplicar(Double[][] A, Double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        Double[][] C = new Double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    public static Double[][] matrixAdd(Double[][] A, Double[][] B) {

        int rows = A.length;
        int cols = A[0].length;
        
        Double[][] res = new Double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                res[i][j] = A[i][j] + B[i][j];
            }
        }

        return res;
    }

    private static double[][] reshape(double[][] A, int m, int n) {
        int origM = A.length;
        int origN = A[0].length;
        if (origM*origN != m*n) {
            throw new IllegalArgumentException("New matrix must be of same area as matix A");
        }
        double[][] B = new double[m][n];
        double[] A1D = new double[A.length * A[0].length];

        int index = 0;
        for(int i = 0;i<A.length;i++){
            for(int j = 0;j<A[0].length;j++){
                A1D[index++] = A[i][j];
            }
        }

        index = 0;
        for(int i = 0;i<n;i++){
            for(int j = 0;j<m;j++){
                B[j][i] = A1D[index++];
            }

        }
        return B;
    }
    
    private <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }
    
    // first hardecoded transpose case
    // matrix[i][j][k][l] --> matrix[j][k][l][i]
    private static Double[][][][] transpose1(Double[][][][] X) {
        int iLen = X.length; // i
        int jLen = X[0].length; // j
        int kLen = X[0][0].length; // k
        int lLen = X[0][0][0].length; // l 

        Double[][][][] transpose = new Double[jLen][kLen][kLen][lLen];

        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                    for (int l = 0; l < lLen; l++) {
                        transpose[j][k][l][i] = X[i][j][k][l];
                    }
                }
            }
        }
        return transpose;
    }

    // second hardecoded transpose case
    // matrix[i][j][k]--> matrix[k][i][j]
    private static Double[][][] transpose2(Double[][][] X) {
        int iLen = X.length; // i
        int jLen = X[0].length; // j
        int kLen = X[0][0].length; // k

        Double[][][] transpose = new Double[jLen][kLen][kLen];

        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                    transpose[k][i][j] = X[i][j][k];
                }
            }
        }
        return transpose;
    }
    
     // second hardecoded transpose case
    // matrix[i][j][k][l] --> matrix[k][l][i][j]
    private static Double[][][][] transpose3(Double[][][][] X) {
        int iLen = X.length; // i
        int jLen = X[0].length; // j
        int kLen = X[0][0].length; // k
        int lLen = X[0][0][0].length; // l 

        Double[][][][] transpose = new Double[kLen][lLen][iLen][jLen];

        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                    for (int l = 0; l < lLen; l++) {
                        transpose[k][l][i][j] = X[i][j][k][l];
                    }
                }
            }
        }
        return transpose;
    }
    
    private static Double[][] reshape1D(Double[] arr, int r, int c) {
        Double[][] res = null;

        if (r == -1 && c == 1) {
            res = new Double[arr.length][1];
            for (int i = 0; i < arr.length; i++) {
                res[i][1] = arr[i];
            }
        }

        else if (r == -1 && c == 1) {
            res = new Double[1][arr.length];
            for (int i = 0; i < arr.length; i++) {
                res[1][i] = arr[i];
            }
        }
        
        return res;
    }

    public static List<Object> conv_forward(Double[][][][] X, Double[][][][] W, Double[][] b) {
        int n_filter = 32;
        int h_filter = 3;
        int w_filter = 3;
        int stride = 1;
        int padding = 2;

        int d_X = 1;
        int h_X = 28;
        int w_X = 28;
        
        int n_X = X.length;

        int h_out = (int) ((h_X - h_filter + 2 * padding) / stride + 1);
        int w_out = (int) ((w_X - w_filter + 2 * padding) / stride + 1);

        Double[][] X_col = im2_col_indices(X, h_filter, w_filter, stride, padding);
        Double[][] W_row = reshape4DSpecial(W, n_filter, -1); // implement specially

        Double[][] out = matrixAdd(multiplicar(W_row, X_col), b);
        //Double[][][][] o = reshape(out, 32, 30, 30, 1);
        Double[][][][] o = reshape(out, n_filter, h_out, w_out, n_X);
        o = transpose1(o);
        int[] out_dim = {n_filter, h_out, w_out};
        List<Object> res = new ArrayList<>();
        res.add(o);
        res.add(out_dim);
        return res;
    }
    
    // numpy function translated
    private static Double[] repeat(Double[] arr, int repeats) {
        Double[] res = new Double[arr.length * repeats];
        int idx = 0;
        for (int i = 0; i < repeats; i++) {
        	for (int j = 0; j < arr.length; j++) {
        		res[idx] = arr[j];
        		idx++;
        	}
        }
        return res;
    }
 
    // construct array which is a range
    private static Double[] arange(int upper_bound) {
        Double[] res = new Double[upper_bound];
        for (int i = 0; i < upper_bound; i++) {
        	res[i] = (double) i;
        }
        return res;
    }

    // construct array with itself repeated (repeats times)
    private static Double[] tile(Double[] arr, int repeats) {
        int len = arr.length * repeats;
        Double[] res = new Double[len];
        int i = 0;
        while (i < len) {
            for (int j = 0; j < arr.length; j++) {
                res[i] = arr[j];
                i++;
            }
        }
        return res;
    }
    
    // double version of scalar multiplication
    private static Double[] scalarMult(double c, Double[] arr) {
        Double[] res = new Double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = c * arr[i];
        }
        return res;
    }

    private static Double[][] innerAdd(Double[][] v1, Double[][] v2) {
    	// res should be type M x N
    	int M = v1.length;
    	int N = v2[0].length;
    	Double[][] res = new Double[M][N];
    	for (int i = 0; i < M; i++) {
    		for (int j = 0; j < N; j++) {
    			res[i][j] = v2[0][j] + v1[i][0];
    		}
    	}
    	return res;
    }
    
    public static List<Object> get_im2col_indices(int[] x_shape, int field_height, int field_width,
                                        int padding, int stride) {
        List<Object> res = new ArrayList<>();
        int N = x_shape[0];
        int C = x_shape[1];
        int H = x_shape[2];
        int W = x_shape[3];
        assert((H + 2 * padding - field_height) % stride == 0);
        assert((W + 2 * padding - field_height) % stride == 0);
        int out_height = (H + 2 * padding - field_height) / stride + 1;
        int out_width = (W + 2 * padding - field_height) / stride + 1;

        // numpy.repeat --> repeat given elements in given array n amount of timees
        // if no axis specified, flatten it!

        // numpy.arange --> return evenly spaced values within a given interval
        Double[] i0 = repeat(arange(field_height), field_width);
        i0 = tile(i0, C);
        
        // BE CAREFUL YOU IMPLEMENTED THIS CORRECTLY!!!
        Double[] i1 = scalarMult(stride, repeat(arange(out_height), out_width));
        Double[] j0 = tile(arange(field_width), (field_height * C));
        Double[] j1 = scalarMult(stride, tile(arange(out_width), (int) out_height));

        // i0 : column vector insode array
        // i1 : row vector inside array
        // i : column vector inside array
         
        
        // clean up here
        // BE CAREFUL THAT YOU IMPLEMENTED ADDS CORRECTLY!!!
        Double[][] i = innerAdd(reshape1D(i0, -1, 1), reshape1D(i1, 1, -1)); // add matrix addition

        // same for J 
        Double[][] j = innerAdd(reshape1D(j0, -1, 1), reshape1D(j1, 1, -1));
        Double[] lol = repeat(arange(C), (field_height * field_width));
        Double[][] k = reshape1D(lol, -1, 1);
        res.add(i);
        res.add(j);
        res.add(k);
        return res;
    }
    
    
    private static Double[][][][] pad(Double[][][][] x, int p1, int p2) {
    	
    	int iLen = x.length;
    	int jLen = x[0].length;
    	int kLen = x[0][0].length;
    	int lLen = x[0][0][0].length;
    	
    	// NOTE THAT THE FIRST TWO DIMENSIONS REMAIN UNCHANGED
    	// ONLY LAST TWO ARE CHANGED
    	// NOTE THAT EVERYTHING IS INITIALIZE TO ZERO
    	// SO JUST FOCUS ON COPYING INTO INNER PART 
    	Double[][][][] res = new Double[iLen][jLen][kLen+2*p1][lLen+2*p2];
    	
    	for (int i = 0; i < iLen; i++) {
    		for (int j = 0; j < jLen; j++) {
    			
    			// grab 2d array to make life easier
    			Double[][] inner = x[i][j];
    			// set first two elements 0
    			for (int k = 2; k < kLen-2; k++) {
    				for (int l = 2; l < lLen-2; l++) {
    					res[i][j][k][l] = x[i][j][k-2][l-2];
    				}
    			}
    		}
    	}
    	
    	return res;
    }
    // NOTE THAT X IS A 3D ARRAY!!!
    public static Double[][] im2_col_indices(Double[][][][] x, int field_height,
    		                         int field_width, int padding, int stride) {
        int p = padding;
        // x_padded ask Charlie when he comes lol
        Double[][][][] x_padded = pad(x, padding, padding);
        // set up shape of X
        int[] x_shape = {1, 1, 28, 28};
        List<Object> objects = get_im2col_indices(x_shape, field_height, field_width, padding,
                                                  stride);
        Double[][] k = (Double[][]) objects.get(0);
        Double[][] i = (Double[][]) objects.get(1);
        Double[][] j = (Double[][]) objects.get(2);
        
        int dim1 = x_padded.length;
        int dim2 = x_padded[0].length;
        int dim3 = x_padded[0][0].length;
        int dim4 = x_padded[0][0][0].length;
        
        Double[][][] a = x_padded[0];
        Double[] b = i[0];
        Double[] c = i[1];
        
        Double[][][] cols = new Double[a.length][i.length][i[0].length];
        // now do Charlie's loop
        for (int i1 = 0; i1 < a.length; i1++) {
            for (int i2 = 0; i2 < i.length; i2++) {
            	for (int i3 = 0; i3 < i[0].length; i3++) {
            	    int temp2 = (int) (double) i[i2][i3];
            	    int temp3 = (int) (double) j[i2][i3];
            		//cols[i1][i2][i3] = x_padded[i1][0][i[i2][i3]][j[i2][i3]];
            	    cols[i1][i2][i3] = x_padded[i1][0][temp2][temp3];
            	}
            }
        }
        
        int C = x_shape[1];
        Double[][] fin = reshape3D(transpose2(cols), (field_height * field_width), -1);
        return fin;
    }

    // done
    public static Double[][][][] relu_forward(Double[][][][] X) {
        
        int iLen = X.length;
        int jLen = X[0].length;
        int kLen = X[0][0].length;
        int lLen = X[0][0][0].length;
        Double[][][][] res = new Double[iLen][jLen][kLen][lLen];

        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[0].length; j++) {
                for (int k = 0; k < kLen; k++) {
                    for (int l = 0; l < lLen; l++) {
                        res[i][j][k][l] = Math.min(X[i][j][k][l], 0);
                    }
                }
            }
        }

        return res;
    }

    private static Double[] flatten(Double[][] matrix) {
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	Double[] res = new Double[iLen * jLen];
    	int idx = 0;
    	
    	for (int i = 0; i < iLen; i++) {
    	    for (int j = 0; j < jLen; j++) {
    	    	res[idx] = matrix[i][j];
    	    	idx++;
    	    }
    	}
    	
    	return res;
    }
    
    private static Double[] flatten(Double[][][] matrix) {
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	int kLen = matrix[0][0].length;
    	Double[] res = new Double[iLen * jLen * kLen];
    	int idx = 0;
    	
    	for (int i = 0; i < iLen; i++) {
    	    for (int j = 0; j < jLen; j++) {
    	    	for (int k = 0; k < kLen; k++) {
    	    		res[idx] = matrix[i][j][k];
    	    		idx++;
     	    	}
    	    }
    	}
    	
    	return res;
    }
    
    private static Double[] flatten(Double[][][][] matrix) {
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	int kLen = matrix[0][0].length;
    	int lLen = matrix[0][0][0].length;
    	Double[] res = new Double[iLen * jLen * kLen * lLen];
    	int idx = 0;
    	
    	for (int i = 0; i < iLen; i++) {
    	    for (int j = 0; j < jLen; j++) {
    	    	for (int l = 0; l < lLen; l++) {
    	    		for (int k = 0; k < kLen; k++) {
    	    			res[idx] = matrix[i][j][k][l];
    	    			idx++;
    	    		}
     	    	}
    	    }
    	}
    	
    	return res;
    }
    
    public static Double[][][][] reshape(Double[][] matrix, int x1, int x2, int x3, int x4) {
    
    	Double[] flattened = flatten(matrix);
    	int idx = 0;
    	Double[][][][] res = new Double[x1][x2][x3][x4];
    	
        for (int i = 0; i < x1; i++) {
        	for (int j = 0; j < x2; j++) {
        		for (int k = 0; k < x3; k++) {
        			for (int l = 0; l < x4; l++) {
        				res[x1][x2][x3][x4] = flattened[idx];
        				idx++;
        			}
        		}
        	}
        }
        
        return res;
    }

    public static Double[][] reshape(Double[][] matrix, int r, int c) {
    	
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	int total = iLen * jLen;
    	Double[] flattened = flatten(matrix);
    	int idx = 0;
    	Double[][] res = null;
    	
        if (c == -1) {
        	int upper = total / r;
        	res = new Double[r][total / 32];
            //assert(32 * (total/32) == total);
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < upper; j++) {
                	res[i][j] = flattened[idx];
                	idx++;
                }
            }
        }
        
        return res;
    }

    public static Double[][] reshape4DSpecial(Double[][][][] matrix, int r, int c) {
    	
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	int lLen = matrix[0][0].length;
    	int kLen = matrix[0][0][0].length;
    	int total = iLen * jLen * lLen * kLen;
    	Double[] flattened = flatten(matrix);
    	int idx = 0;
    	Double[][] res = null;
    	
        if (c == -1) {
        	int upper = total / 32;
        	res = new Double[32][total / 32];
            //assert(32 * (total/32) == total);
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < upper; j++) {
                	res[i][j] = flattened[idx];
                	idx++;
                }
            }
        }
        
        return res;
    }
    
    // TOTALLY BRUTE FORCED
    public static Double[][] reshape3D(Double[][][] matrix, int x1,
    		                               int x2) {
        
    	// easiest way
    	// first flatten the 4D matrix
    	// then insert into result array
        Double[][] res = null;
        Double[] flattened = flatten(matrix);
        int len = flattened.length;
        int idx = 0;
        if (x2 == -1) {
        	int dim = len/x1;
        	res = new Double[x1][dim];
        	
        	for (int i = 0; i < x1; i++) {
        		for (int j = 0; j < dim; j++) {
        			res[i][j] = flattened[idx];
        			idx++;
        		}
        	}
        }

        return res;
    }
    
    // TOTALLY BRUTE FORCED
    public static Double[][][][] reshape4D(Double[][][][] matrix, int x1,
    		                               int x2, int x3, int x4) {
        
    	// easiest way
    	// first flatten the 4D matrix
    	// then insert into result array
        Double[][][][] res = new Double[x1][x2][x3][x4];
        Double[] flattened = flatten(matrix);
        int idx = 0;
        for (int i = 0; i < x1; i++) {
            for (int j = 0; j < x2; j++) {
                for (int k = 0; k < x3; k++) {
                    for (int l = 0; l < x4; l++) {
                        res[i][j][k][l] = flattened[idx];
                        idx++;    
                    }
                }
            }
        }

        return res;
    }
    
    // WITH RESPECT TO X-AXIS
    public static Double[] argmax(Double[][] arr) {
        Double[] res = new Double[arr.length]; //???????????????????????
        int idx = 0;

        int rows = arr.length;
        int cols = arr[0].length;
   
        for (int j = 0; j < cols; j++) {

            double max = arr[0][j];
            
            for (int i = 1; i < rows; i++) {
                
                if (arr[i][j] > max) {
                    max = arr[i][j];
                }
            }

            res[idx] = max;
            idx++;
        }
        
        return res;
    }

    public static Double[] maxpool_forward(Double[][][][] X, int[] conv_out_dim) {
        int n_X = X.length;

        int d_X = conv_out_dim[0];
        int h_X = conv_out_dim[1];
        int w_X = conv_out_dim[2];

        int size = 2;
        int stride = 2;
        
        int h_out = (int) ((h_X - size) / stride + 1); 
        int w_out = (int) ((w_X - size) / stride + 1);

        // X : 32 x 1 x 30 x 30
        // --> reshape X as : 
        // BRUTE FORCED ABOVE
        Double[][][][] x_reshaped = X;
        Double[][] x_col = im2_col_indices(x_reshaped, size, size, 0, stride);
        Double[] maxes = argmax(x_col);
   
        
        return maxes;
    }
    
    public static Double[] ravel(Double[][] arr) {

        Double[] res = new Double[arr.length * arr[0].length];
        int idx = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                res[idx] = arr[i][j];
            }
            idx++;
        }
        return res;
    }

    // KEEP RESULT AS 2D
    // WILL BE MULTIPLIED WITH ANOTHER 2D MATRIX 
    public static Double[][] flatten_forward(Double[][] X) {
        int[] x_shape = {1, 1, 28, 28};
        int[] out_shape = {x_shape[0], -1};
        Double[] raveled = ravel(X);
        Double[][] res = {raveled};    
        return res;
    }

 
    public static Double[][] FC_forward(Double[][] X, Double[][] W, Double[][] b) {
    	
        Double[][] out = matrixAdd(multiplicar(X, W), b);
        return out; 
    }

    public static double x_max(Double[] x) {
        
        double max = x[0];
        int len = x.length;

        for (int i = 1; i < len; i++) {
            if (x[i] > max) {
                max = x[i];
            }
        }

        return max;
    }
    
    // done
    public static Double[] exp(Double[] arr) {
    	
    	Double[] res = new Double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = Math.pow(Math.E, arr[i]); 
        }

        return res;
    }

    public static Double[] sub(Double[] arr, double max) {
         
         Double[] res = new Double[arr.length];
         for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i] - max;
         }

         return res;
    }
    
    public static double sum(Double[] arr) {
        
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    // done
    public static Double[] softmax(Double[][] x) {
        Double[] inner_x = x[0];
        double max = x_max(inner_x);
        Double[] subbed = sub(inner_x, max);
        Double[] exped = exp(subbed);
        // now do (1/sum) * (exped)
        double sum = (1.0) / sum(exped);// PLACEHOLDER
        return scalarMult(sum, exped);
    }

}