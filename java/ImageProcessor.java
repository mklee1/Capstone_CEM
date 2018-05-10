import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public Image imageResize(String filename, int length, int width) {

        if (width == -1) {
            width = length;
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image resized = img.getScaledInstance(length, length, Image.SCALE_DEFAULT);
        return resized;
    }

    public Double[][] ndarrayTo2DList(double[][][] image) {
        
        List<Double> row = new ArrayList<>();
        int length = image.length;
        int width = image[0].length;
        Double[][] result = new Double[length][width];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                int add = 0;
                if (image[i][j][0] > 0) {
                    add = 1;
                }
                row.add((double) add);
            }
            Double[] converted = new Double[row.size()];
            converted = row.toArray(converted);
            result[i] = converted;
            // clear row 
            row.clear();
        }
        return result;
    }
    
    // print 2D list for testing purposes
    public void print2DList(double[][] image) {
        
        int length = image.length;
        int width = image[0].length;

        for (int i = 0; i < length; i++) {
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
    
    public int numNeighbors(double[][] image, int row, int col) {
        int len = image.length;
        int numNeighbors = 0;
        for (int i = 0; i < 8; i++) {
            int ind = i+1;
            int[] info = dirs.get(i);
            int addrow = info[0];
            int addcol = info[1];
            int nr = row + addrow;
            int nc = col + addcol;

            if ((bound(nr, nc, len, ind) && image[nr][nc] >= 1)) {
                numNeighbors += 1;
            }
        }
        return numNeighbors;
    }

    public double[][] inTrace(double[][] image, int row, int col, int length) {
        return trace(image, row, col, length, -1);
    }

    public double[][] trace(double[][] image, int row, int col, int length, int loc) {
        int newRow = row;
        int newCol = col;
        if (row == -1 && col == -1) {
            return image;
        }
        else {
            int lastRow = row;
            int lastCol = col;
            int[] info = findMooreNeighbor(image, row, col, length, loc);
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
                print2DList(image);
                System.out.println("Start next from " + dirs1.get(loc));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            image = trace(image, newRow, newCol, length, loc);
        }
        return image;
    }
    
    public boolean bound(int row, int col, int L, int ind) {
        if (!(0 <= row && row <= L)) {
            return false;
        }
        if (!(0 <= col && col <= L)) {
            return false;
        }
        return true;
    }

    public int[] findMooreNeighbor(double[][] img, int r, int c, int L, 
                                   int start) {
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

            if (bound(nr, nc, L, ind) && img[nr][nc] == 2) {
                System.out.println("End condition");
                int[] res = {-1, -1};
                return res;
            }
            else if (bound(nr, nc, L, ind) && (img[nr][nc] == 1) && nn) {
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