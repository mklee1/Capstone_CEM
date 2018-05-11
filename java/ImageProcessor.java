import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageProcessor {
    
    private final Map<Integer, int[]> dirs;
    private final Map<int[], Integer> inverseDirs;
    private final Map<Integer, String> dirs1;

    private Map<Integer, int[]> dirsConstruct() {
        Map<Integer, int[]> res = new HashMap<>();
        int[] arr1 = {-1, 1};
        int[] arr2 = {-1, 0};
        int[] arr3 = {0, 1};
        int[] arr4 = {1, 1};
        int[] arr5 = {1, 0};
        int[] arr6 = {1, -1};

        res.put(1, arr1);
        res.put(2, arr2);
        res.put(3, arr1);
        res.put(4, arr3);
        res.put(5, arr4);
        res.put(6, arr5);
        res.put(7, arr6);
        res.put(8, arr3);
        return res;
    }
    
    public ImageProcessor() {
        dirs = dirsConstruct();
        
        inverseDirs = new HashMap<>();
        
        for (Map.Entry<Integer, int[]> entry : dirs.entrySet()) {
            int k = entry.getKey();
            int[] v = entry.getValue();
            inverseDirs.put(v, k);
        }

        dirs1 = new HashMap<>();
        dirs1.put(1, "top left....");
        dirs1.put(2, "top.........");
        dirs1.put(3, "top right...");
        dirs1.put(4, "right.......");
        dirs1.put(5, "bottom right");
        dirs1.put(6, "bottom......");
        dirs1.put(7, "bottom left.");
        dirs1.put(8, "left........");
    }

    public Image imageResize(String filename, int height, int width) {

        if (width == -1) {
            width = height;
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        img = (BufferedImage)img.getScaledInstance(height, height, Image.SCALE_DEFAULT);
        return img;
    }

    public int[][] imageTo2DList(BufferedImage image) {
    		Raster raster = image.getData();
    		int width = raster.getWidth();
    		int height = raster.getHeight();
    		int [][] img = new int[width][height];
    		int whiteLimit = 50;
    		
    		for (int i = 0; i < width; i++) {
    			for (int j = 0; j < height; j++) {
    				int element = raster.getSample(i, j, 0);
    				if (element > whiteLimit) {
    					img[i][j] = 1;
    				}
    				img[i][j] = 1;
    			}
    		}
    		return img;
    }
    
    // print 2D list for testing purposes
    public void print2DList(int[][] image) {
        
        int height = image.length;
        int width = image[0].length;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (image[i][j] == 0) {
                    System.out.print("_");
                }
                else if (image[i][j] == 1) {
                    System.out.print("O");
                }
                else if (image[i][j] == 2) {
                    System.out.print("@");
                }
                else if (image[i][j] == 3) {
                    System.out.print("X");
                }
                else {
                    System.out.print(" ");
                }
            }
        }
        System.out.println();
    }
    
    private int numNeighbors(int[][] image, int row, int col) {
        int len = image.length;
        int width = image[0].length;
        int numNeighbors = 0;
        for (int i = 0; i < 8; i++) {
            int ind = i+1;
            int[] info = dirs.get(i);
            int addrow = info[0];
            int addcol = info[1];
            int nr = row + addrow;
            int nc = col + addcol;

            if ((bound(nr, nc, len, width, ind) && image[nr][nc] >= 1)) {
                numNeighbors += 1;
            }
        }
        return numNeighbors;
    }

    public int[] digitSegment(int[][] image, int bias) {
    		int realCol;
    		int height = image.length;
    		int width = image[0].length - bias;
    		int[] bounds = {-1, -1, -1, -1};
    		for (int col = 0; col < width; col++) {
    			for (int row = 0; row < height; row++) {
    				realCol = col + bias;
    				if (image[row][realCol] == 1) {
    					image = trace(image, row, realCol, height, width+bias, -1);
    					bounds = getBounds(image, bias, 2);
    					System.out.println("End Moore tracing" + Arrays.toString(bounds));
    					return bounds;
    				}
    			}
    		}
        return bounds;
    }
    public int[] getBounds(int[][] image, int bias, int search) {
    		int height = image.length;
    		int width = image[0].length;
    		int minRow = height;
    		int minCol = width;
    		int maxRow = 0;
    		int maxCol = 0;
    		int[] result = new int[4];
    		
    		for (int row = 0; row < height; row++) {
    			for (int col = 0; col < width-bias; col++) { 
    				int realCol = col+bias;
    				int element = (int)image[row][realCol];
    				if (element == search) {
    					if (row < minRow) {
    						minRow = row;
    					} else if (row > maxRow) {
    						maxRow = row;
    					}
    					
    					if (realCol < minCol) {
    						minCol = realCol;
    					} else if (realCol > maxCol) {
    						maxCol = realCol;
    					}
    				}
    			}
    		}
    		result[0] = minRow; result[1] = maxRow;
    		result[2] = minCol; result[3] = maxCol;
    		return result;
    }

    private int[][] trace(int[][] image, int row, int col, int height, int width, int loc) {
        int newRow = row;
        int newCol = col;
        if (row == -1 && col == -1) {
            return image;
        }
        else {
            int lastRow = row;
            int lastCol = col;
            int[] info = findMooreNeighbor(image, row, col, height, width, loc);
            newRow = info[0];
            newCol = info[1];
            System.out.println(" (" + newRow + "," + newCol + ")");
            image[lastRow][lastCol] = 2;
            image[newRow][newCol] = 3;

            try {
                int drow = newRow-lastRow;
                int dcol = newCol-lastCol;
                int[] key = {drow, dcol};
                loc = (inverseDirs.get(key)+4) % 8 + 1;
                // print2DList(image);
                // System.out.println("Start next from " + dirs1.get(loc));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            image = trace(image, newRow, newCol, height, width, loc);
        }
        return image;
    }
    public int[][] cutImage(int[][] image, int[] bounds) {
    		int minRow = bounds[0];
    		int maxRow = bounds[1]+1;
    		int minCol = bounds[2];
    		int maxCol = bounds[3]+1;
    		
    		int rows = maxRow-minRow;
    		int cols = maxCol-minCol+4;
    		int[][] result = new int[rows+4][cols];
    		
    		int imgI = 0;
    		int imgJ = 0;
    		
    		for (int i = 0; i < rows; i++) {
    			imgI = i + minRow;
    			for (int j = 0; j < cols-4; j++) {
    				imgJ = j + minCol;
    				result[i+2][j+2] = image[imgI][imgJ];
    			}
    		}
    		return result;
    }
    
    private boolean bound(int row, int col, int L, int W, int ind) {
        if (!(0 <= row && row <= L)) {
            return false;
        }
        if (!(0 <= col && col <= W)) {
            return false;
        }
        return true;
    }

    private int[] findMooreNeighbor(int[][] img, int r, int c, int L, int W, int start) {
        System.out.printf("Init: %s %s", r, c);
        for (int i = 0; i < 8; i++) {
            int ind = (start+i-1) % 8 + 1;
            int[] info = dirs.get(ind);
            int addrow = info[0];
            int addcol = info[1];
            int nr = r + addrow;
            int nc = c + addcol;
            boolean nn = numNeighbors(img, nr, nc) > 1;
            System.out.printf("checking %s ...", dirs1.get(i));

            if (bound(nr, nc, L, W, ind) && img[nr][nc] == 2) {
                System.out.println("End condition");
                int[] res = {-1, -1};
                return res;
            }
            else if (bound(nr, nc, L, W, ind) && (img[nr][nc] == 1) && nn) {
                System.out.println("Found!");
                int[] res = {nr, nc};
                return res;
            }
            System.out.println("continuing");
        }
        int[] res = {-1, -1};
        return res;
    }
}
