import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by cjhsu on 4/6/17.
 */
public class ImageUtils {
    public static BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2, double theta, double xOffset, double yOffset) {
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(theta, img2.getWidth()/2, img2.getHeight()/2);
        AffineTransformOp rotateOp = new AffineTransformOp(rotation, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedImg2 = rotateOp.filter(img2, null);

        int width1 = img1.getWidth();
        int height1 = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();

        BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

        int diff;
        int result; // Stores output pixel
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int rgb1 = img1.getRGB(j, i);
                int rgb2;
                if (j + xOffset >= 0 && j + xOffset < width2 && i + yOffset >= 0 && i + yOffset < height2) {
                    rgb2 = rotatedImg2.getRGB(j + (int) xOffset, i + (int) yOffset);
                } else {
                    rgb2 = 0;
                }

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
                diff = Math.abs(r1 - r2); // Change
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
                diff /= 3; // Change - Ensure result is between 0 - 255
                // Make the difference image gray scale
                // The RGB components are all the same
                result = (diff << 16) | (diff << 8) | diff;
                outImg.setRGB(j, i, result); // Set result
            }
        }

        // Now return
        return outImg;

    }
}
