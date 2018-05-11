import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NP {
    
	public static boolean equals(Double[][][][] x1, Double[][][][] x2) {
		for (int i = 0; i < x1.length; i++) {
    		for (int j = 0; j < x1[0].length; j++) {
    			for (int k = 0; k < x1[0][0].length; k++) {
    				for (int l = 0; l < x1[0][0][0].length; l++) {
    					if (x1[i][j][k][l] != x2[i][j][k][l]) {
    						System.out.printf("%.1f %.1f\n", x1[i][j][k][l], x2[i][j][k][l]);
    						return false;
    					}
    				}
    			}
    		}
    	}
		return true;
	}
	
	public static boolean noNull(Double[][][][] x1) {
		for (int i = 0; i < x1.length; i++) {
    		for (int j = 0; j < x1[0].length; j++) {
    			for (int k = 0; k < x1[0][0].length; k++) {
    				for (int l = 0; l < x1[0][0][0].length; l++) {
    					if (x1[i][j][k][l] == null) {
    						return false;
    					}
    				}
    			}
    		}
    	}
		return true;
	}
	
	public static boolean noNull(Double[][] x1) {
		for (int i = 0; i < x1.length; i++) {
    		for (int j = 0; j < x1[0].length; j++) {
    		    if (x1[i][j] == null) {
    		    	return false;
    		    }
    		}
    	}
		return true;
	}
	
	public static boolean noNull(Double[] x1) {
		for (int i = 0; i < x1.length; i++) {
		    if (x1[i] == null) {
		    	System.out.println("" + i);
		    	return false;
		    }
    	}
		return true;
	}
	
    public static Double[] predictSingle(Double[][][][] X, Double[][][][] conv_W, Double[][] conv_b,
                                    Double[][] FC_W, Double[][] FC_b) {
    	List<Object> results = conv_forward(X, conv_W, conv_b);
        Double[][][][] x1 = (Double[][][][]) results.get(0);
        System.out.println("starting");
		//print(x1[0][10]);
		System.out.println("ending");
        int[] conv_out_dim = (int[]) results.get(1);
        Double[][][][] x2 = relu_forward(x1);
        //print(x2[0][0]);
        //assert(equals(x1, x2));
        Double[][][][] x3 = maxpool_forward(x2, conv_out_dim);
        Double[][] x4 = flatten_forward(x3);
        /*for (int i = 300; i < 350; i++) {
        	System.out.println("x4 val = " + x4[0][i]);
        }*/
        //print(x4);
        /*for (int i = 0; i < x4.length; i++) {
        	for (int j = 0; j < x4[0].length; j++) {
        		System.out.println(x4[i][j]);
        	}
        }*/
        Double[][] x5 = FC_forward(x4, FC_W, FC_b);
        Double[] vals = x5[0];
        for (int i = 0; i < vals.length; i++) {
        	System.out.print(vals[i] + " ");
        }
        System.out.println();
        return x5[0];
    }
    
    // helper method which performs matrix multiplication
    public static Double[][] multiplicar(Double[][] A, Double[][] B) {
        
    	//assert(noNull(A));
    	//assert(noNull(B));
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
                	//System.out.println(A[i][k]);
                	//System.out.println(B[k][j]);
                    C[i][j] += A[i][k] * B[k][j];
                    /*if (j == 0 && k >= 300 && k < 350) {
                    	System.out.println("C[i][j] = " + C[i][j] + "; A[i][k] = " + A[i][k] + "; B[k][j] = " + B[k][j]);
                    }*/
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
    
    // first hardecoded transpose case
    // matrix[i][j][k][l] --> matrix[l][i][j][k]
    public static Double[][][][] transpose1(Double[][][][] X) {
        int iLen = X.length; // i
        int jLen = X[0].length; // j
        int kLen = X[0][0].length; // k
        int lLen = X[0][0][0].length; // l 

        Double[][][][] transpose = new Double[lLen][iLen][jLen][kLen];

        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                    for (int l = 0; l < lLen; l++) {
                        transpose[l][i][j][k] = X[i][j][k][l];
                    }
                }
            }
        }
        return transpose;
    }

    // second hardecoded transpose case
    // matrix[i][j][k]--> matrix[j][k][i]
    public static Double[][][] transpose2(Double[][][] X) {
        int iLen = X.length; // i
        int jLen = X[0].length; // j
        int kLen = X[0][0].length; // k

        Double[][][] transpose = new Double[jLen][kLen][iLen];

        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                    transpose[j][k][i] = X[i][j][k];
                }
            }
        }
        return transpose;
    }
    
     // second hardecoded transpose case
    // matrix[i][j][k][l] --> matrix[k][l][i][j]
    public static Double[][][][] transpose3(Double[][][][] X) {
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
    
    public static Double[][] reshape1D(Double[] arr, int r, int c) {
        Double[][] res = null;

        if (r == -1 && c == 1) {
            res = new Double[arr.length][1];
            for (int i = 0; i < arr.length; i++) {
                res[i][0] = arr[i];
            }
        }

        else if (r == 1 && c == -1) {
            res = new Double[1][arr.length];
            for (int i = 0; i < arr.length; i++) {
                res[0][i] = arr[i];
            }
        }
        
        else {
        	res = new Double[r][c];
        	int idx = 0;
        	for (int i = 0; i < r; i++) {
        		for (int j = 0; j < c; j++) {
        			res[i][j] = arr[idx++];
        		}
        	}
        }
        //assert(noNull(res));
        return res;
    }
    
    public static Double[][] extend(Double[][] b, int cols) {
    	Double[][] res = new Double[b.length][cols];
    	for (int i = 0; i < b.length; i++) {
    		for (int j = 0; j < cols; j++) {
    			res[i][j] = b[i][0];
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

        Double[][] X_col = im2_col_indices(X, h_filter, w_filter, padding, stride);
        Double[][] W_row = reshape4DSpecial(W, n_filter, -1); // implement specially
        //assert(noNull(X_col));
        /*for (int i = 0; i < W_row.length; i++) {
        	for (int j = 0; j < W_row[0].length; j++) {
        		System.out.println(W_row[i][j]);
        	}
        }*/
        
        /*for (int i = 0; i < X_col.length; i++) {
        	for (int j = 0; j < X_col[0].length; j++) {
        		System.out.println(X_col[i][j]);
        	}
        }*/
        
        //System.out.printf("len1: %d len2: %d\n", X_col.length, X_col[0].length);
        //System.out.printf("len1: %d len2: %d\n", W_row.length, W_row[0].length);
        Double[][] extended = extend(b, 900);
        Double[][] out = matrixAdd(multiplicar(W_row, X_col), extended);
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
    public static Double[] repeat(Double[] arr, int repeats) {
        Double[] res = new Double[arr.length * repeats];
        int idx = 0;
        for (int i = 0; i < arr.length; i++) {
        	for (int j = 0; j < repeats; j++) {
        		res[idx++] = arr[i];
        	}
        }
        return res;
    }
 
    // construct array which is a range
    public static Double[] arange(int upper_bound) {
        Double[] res = new Double[upper_bound];
        for (int i = 0; i < upper_bound; i++) {
        	res[i] = (double) i;
        }
        return res;
    }

    // construct array with itself repeated (repeats times)
    public static Double[] tile(Double[] arr, int repeats) {
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
    public static Double[] scalarMult(double c, Double[] arr) {
        Double[] res = new Double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = c * arr[i];
        }
        return res;
    }

    public static Double[][] innerAdd(Double[][] v1, Double[][] v2) {
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
        
        
        //assert((H + 2 * padding - field_height) % stride == 0);
        //assert((W + 2 * padding - field_height) % stride == 0);
        //System.out.printf("bingo %d %d %d %d\n", H, padding, field_height, stride);
        int out_height = (H + 2 * padding - field_height) / stride + 1;
        int out_width = (W + 2 * padding - field_height) / stride + 1;
        
        System.out.println("out height: " + out_height);
        System.out.println("out width: " + out_width);
        // numpy.repeat --> repeat given elements in given array n amount of timees
        // if no axis specified, flatten it!

        // numpy.arange --> return evenly spaced values within a given interval
        Double[] i0 = repeat(arange(field_height), field_width);
        //System.out.println(Arrays.toString(i0));
        
        i0 = tile(i0, C);
        System.out.printf("C: %d dim1: %d\n", C, i0.length);
        
        // BE CAREFUL YOU IMPLEMENTED THIS CORRECTLY!!!
        Double[] i1 = scalarMult(stride, repeat(arange(out_height), out_width));
        //System.out.println(Arrays.toString(i1));
        Double[] j0 = tile(arange(field_width), (field_height * C));
        //System.out.println(Arrays.toString(j0));
        Double[] j1 = scalarMult(stride, tile(arange(out_width), (int) out_height));
        //System.out.println("j1 length: " + j1.length);
        // i0 : column vector insode array
        // i1 : row vector inside array
        // i : column vector inside array
         
        
        // clean up here
        // BE CAREFUL THAT YOU IMPLEMENTED ADDS CORRECTLY!!!
        //assert(noNull(i0));
        //assert(noNull(i1));
        Double[][] i = innerAdd(reshape1D(i0, -1, 1), reshape1D(i1, 1, -1)); // add matrix addition

        // same for J 
        Double[][] j = innerAdd(reshape1D(j0, -1, 1), reshape1D(j1, 1, -1));
        System.out.println("j[0] length: " + j[0].length);
        Double[] lol = repeat(arange(C), (field_height * field_width));
        Double[][] k = reshape1D(lol, -1, 1);
        res.add(i);
        res.add(j);
        res.add(k);
        //assert(noNull(i));
        //assert(noNull(j));
        //assert(noNull(k));
        return res;
    }
    
    
    public static Double[][][][] pad(Double[][][][] x, int p1, int p2) {
    	
    	int iLen = x.length;
    	int jLen = x[0].length;
    	int kLen = x[0][0].length;
    	int lLen = x[0][0][0].length;
    	
    	//assert(noNull(x));
    	// NOTE THAT THE FIRST TWO DIMENSIONS REMAIN UNCHANGED
    	// ONLY LAST TWO ARE CHANGED
    	// NOTE THAT EVERYTHING IS INITIALIZE TO ZERO
    	// SO JUST FOCUS ON COPYING INTO INNER PART 
    	Double[][][][] res = new Double[iLen][jLen][kLen+2*p1][lLen+2*p2];
    	for (int i = 0; i < iLen; i++) {
        	for (int j = 0; j < jLen; j++) {
        		for (int k = 0; k < kLen+2*p1; k++) {
        			for (int l = 0; l < lLen+2*p2; l++) {
        				res[i][j][k][l] = 0.0;
        			}
        		}
        	}
        }
    	
    	for (int i = 0; i < iLen; i++) {
    		for (int j = 0; j < jLen; j++) {
    			
    			// grab 2d array to make life easier
    			Double[][] inner = x[i][j];
    			// set first two elements 0
    			for (int k = p1; k < kLen+p1; k++) {
    				for (int l = p2; l < lLen+p2; l++) {
    					res[i][j][k][l] = inner[k-p1][l-p2];
    				}
    			}
    		}
    	}
    	
    	return res;
    }
    
    public static void print(Double[][][][] a) {
    	for (int i = 0; i < a.length; i++) {
    		for (int j = 0; j < a[0].length; j++) {
    			for (int k = 0; k < a[0][0].length; k++) {
    				for (int l = 0; l < a[0][0][0].length; l++) {
    					System.out.println(a[i][j][k][l]);
    				}
    			}
    		}
    	}
    }
    
    public static void print(Double[] a) {
    	for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
    	}
		System.out.println();
    }
    
    public static void print(Double[][] a) {
    	for (int i = 0; i < a.length; i++) {
    		for (int j = 0; j < a[0].length; j++) {
    			System.out.print(a[i][j] + " ");
    		}
    		System.out.println();
    	}
    }
    // NOTE THAT X IS A 3D ARRAY!!!
    public static Double[][] im2_col_indices(Double[][][][] x, int field_height,
    		                         int field_width, int padding, int stride) {
        int p = padding;
        
        // x_padded ask Charlie when he comes lol
        Double[][][][] x_padded = pad(x, padding, padding);
        //print(x_padded[0][0]);
        
        // set up shape of X
        int[] x_shape = {x.length, x[0].length, x[0][0].length, x[0][0][0].length}; 
        List<Object> objects = get_im2col_indices(x_shape, field_height, field_width, padding,
                                                  stride);
        Double[][] i = (Double[][]) objects.get(0);
        Double[][] j = (Double[][]) objects.get(1);
        Double[][] k = (Double[][]) objects.get(2);
        //print(i);
        // print(j);
        //int dim1 = x_padded.length;
        //int dim2 = x_padded[0].length;
        //int dim3 = x_padded[0][0].length;
        //int dim4 = x_padded[0][0][0].length;
        
        //System.out.printf("%d %d %d %d\n", dim1, dim2, dim3, dim4);
        //Double[][][] a = x_padded[0];
        int a = x.length;
        //Double[] b = i[0];
        //Double[] c = i[1];
        
        Double[][][] cols = new Double[a][i.length][i[0].length];
        System.out.println("dim1 dim2 = " + i.length + "   " + i[0].length);
        for (int i1 = 0; i1 < a; i1++) {
        	for (int i2 = 0; i2 < i.length; i2++) {
        		for (int i3 = 0; i3 < i[0].length; i3++) {
        			cols[i1][i2][i3] = 0.0;
        		}
        	}
        }
        System.out.printf("%d %d %d\n", cols.length, cols[0].length, cols[0][0].length);
        
        // now do Charlie's loop
        //assert(i.length == j.length && i[0].length == j[0].length);
        for (int i1 = 0; i1 < a; i1++) {
            for (int i2 = 0; i2 < i.length; i2++) {
            	for (int i3 = 0; i3 < i[0].length; i3++) {
            	    int temp2 = (int) (double) i[i2][i3];
            	    int temp3 = (int) (double) j[i2][i3];
            	    /*if (temp2 >= 30 || temp3 >= 30) {
            	    	System.out.println(temp2);
            	    	System.out.println(temp3);
            	    }*/
            		//cols[i1][i2][i3] = x_padded[i1][0][i[i2][i3]][j[i2][i3]];
            	    cols[i1][i2][i3] = x_padded[i1][0][temp2][temp3];
            	}
            }
        }
        
        //print(cols[0]);
        int C = x_shape[1];
        Double[][][] t = transpose2(cols);
        //System.out.println("whatttt");
        /*for (int i1 = 0; i1 < t.length; i1++) {
        	for (int i2 = 0; i2 < t[0].length; i2++) {
        		for (int i3 = 0; i3 < t[0][0].length; i3++) {
        			System.out.println(t[i1][i2][i3]);
        		}
        	}
        }*/
        Double[][] fin = reshape3D(t, (field_height * field_width * C), -1);
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
                        res[i][j][k][l] = Math.max(X[i][j][k][l], 0);
                    }
                }
            }
        }

        return res;
    }

    public static Double[] flatten(Double[][] matrix) {
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
    
    public static Double[] flatten(Double[][][] matrix) {
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
    
    public static Double[] flatten(Double[][][][] matrix) {
    	int iLen = matrix.length;
    	int jLen = matrix[0].length;
    	int kLen = matrix[0][0].length;
    	int lLen = matrix[0][0][0].length;
    	Double[] res = new Double[iLen * jLen * kLen * lLen];
    	int idx = 0;
    	
    	for (int i = 0; i < iLen; i++) {
    	    for (int j = 0; j < jLen; j++) {
    	    	for (int k = 0; k < kLen; k++) {
    	    		for (int l = 0; l < lLen; l++) {
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
        				res[i][j][k][l] = flattened[idx];
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
    
    public static Double[][][][] reshape(Double[] matrix, int x1, int x2,
    		                             int x3, int x4) {
    	
    	Double[][][][] res = new Double[x1][x2][x3][x4];
        int idx = 0;
        assert(x1*x2*x3*x4 == matrix.length);
    	for (int i = 0; i < x1; i++) {
    		for (int j = 0; j < x2; j++) {
    			for (int k = 0; k < x3; k++) {
    				for (int l = 0; l < x4; l++) {
    					res[i][j][k][l] = matrix[idx];
    					idx++;
    				}
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
        Double[] res = new Double[arr[0].length]; //???????????????????????
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

    public static Double[][][][] maxpool_forward(Double[][][][] X, int[] conv_out_dim) {
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
        Double[][][][] x_reshaped = reshape4D(X, 32, 1, 30, 30);
        //print(x_reshaped[10][0]);
        Double[][] x_col = im2_col_indices(x_reshaped, size, size, 0, stride);
        Double[] maxes = argmax(x_col);
        //print(maxes);
        //System.out.printf("%d %d\n", x_col.length, x_col[0].length);
        Double[][][][] out = reshape(maxes, h_out, w_out, n_X, d_X);
        out = transpose3(out);
        return out;
    }
    
    public static Double[] ravel(Double[][][][] arr) {

    	int iLen = arr.length;
    	int jLen = arr[0].length;
    	int kLen = arr[0][0].length;
    	int lLen = arr[0][0][0].length;
    	
    	//assert(noNull(arr));
        Double[] res = new Double[iLen * jLen * kLen * lLen];
        int idx = 0;
        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                for (int k = 0; k < kLen; k++) {
                	for (int l = 0; l < lLen; l++) {
                		res[idx] = arr[i][j][k][l];
                		idx++;
                	}
                }
            }
        }
        
        //assert(noNull(res));
        return res;
    }

    // KEEP RESULT AS 2D
    // WILL BE MULTIPLIED WITH ANOTHER 2D MATRIX 
    public static Double[][] flatten_forward(Double[][][][] X) {
        int[] x_shape = {X.length, X[0].length, X[0][0].length, X[0][0][0].length};
        
        //int[] out_shape = {x_shape[0], -1};
        Double[] raveled = ravel(X);
        Double[][] res = reshape1D(raveled, x_shape[0], -1);    
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