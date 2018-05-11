import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Image;

import javax.imageio.ImageIO;

public class Driver {
	public static void main(String args[]) {
		int[] bounds;
		int[][] img;
		int[][] seg;
		ImageProcessor ip = new ImageProcessor();
		try {
            BufferedImage image = ImageIO.read(new File("test_segment2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
		img = ip.ndarrayTo2DList(image);

		bounds = ip.getBounds(img, 0, 1);
		image = ip.cutImage(img, bounds); 
		ip.print2DList(img);

		bounds = ip.digitSegment(img,0);
		while (bounds[0] != -1) {
		    seg = ip.cutImage(img, bounds);
		    ip.print2DList(seg);
		    int limit = bounds[3]+1;
		    bounds = ip.digitSegment(img, limit);
		}
	}
}

