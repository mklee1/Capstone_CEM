import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

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
        res.put(4,  arr3);
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
    
    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(
            image.getWidth(null), image.getHeight(null),
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
    
    public static void makeGray(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); ++x)
        for (int y = 0; y < img.getHeight(); ++y)
        {
            int rgb = img.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb & 0xFF);

            int grayLevel = (r + g + b) / 3;
            int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
            img.setRGB(x, y, gray);
        }
    }
    
    public void printPixels(Image img) {
    	BufferedImage buf = convertToBufferedImage(img);
        int width = buf.getWidth();
        int height = buf.getHeight();
        //Double[][] result = new Double[height][width];
        
        for (int row = 0; row < height; row++) {
        	for (int col = 0; col < width; col++) {
        		System.out.print(buf.getRGB(col, row) + " ");
        	}
        	System.out.println();
        }
    }
    
    // jpeg img
    // call imgResize
    // ndArrayTo2DList
    public BufferedImage imageResize(String filename, int length, int width) throws IOException {

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
        ImageIO.write(convertToBufferedImage(resized), "jpg", new File("resized.jpg"));
        //System.out.println("Printing pixels...");
        //printPixels(resized);
        
        // original
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        ImageIO.write(image, "jpg", new File("blackAndWhite1.jpg"));
        
        // uncomment this later?
        // resized
        //BufferedImage image2 = new BufferedImage(length, length, BufferedImage.TYPE_BYTE_BINARY);
        //Graphics g2 = image2.getGraphics();
        //g2.drawImage(resized, 0, 0, null);
        //g2.dispose();
        //ImageIO.write(image2, "jpg", new File("blackAndWhite2.jpg"));
        
        // compare results later
        // resized
        BufferedImage image2 = new BufferedImage(length, length, BufferedImage.TYPE_BYTE_BINARY);
        Graphics g2 = image2.getGraphics();
        g2.drawImage(resized, 0, 0, null);
        g2.dispose();
        ImageIO.write(image2, "jpg", new File("blackAndWhite2.jpg"));
        
        //Image resized = img.getScaledInstance(length, length, Image.SCALE_DEFAULT);
        //ImageFilter filter = new GrayFilter(true, 50);
        //ImageProducer producer = new FilteredImageSource(resized.getSource(), filter);
        //resized = Toolkit.getDefaultToolkit().createImage(producer);
        // now turn back to BufferedImage
        //BufferedImage dimg = convertToBufferedImage(resized);
        //makeGray(dimg);
        return image2;
    }

    public Double[][][][] ndarrayTo4DList(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        Double[][] result = new Double[height][width];
        
        for (int row = 0; row < height; row++) {
        	for (int col = 0; col < width; col++) {
        		// 
        		result[row][col] = (double) (255 - rgbToGray(img.getRGB(col, row)));
        	}
        }
        Double[][][] inner= {result};
        Double[][][][] res = {inner};
        return res;
    }
    
    public int rgbToGray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);

        int grayLevel = (r + g + b) / 3;
        return grayLevel;
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