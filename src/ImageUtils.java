import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        int width2 = rotatedImg2.getWidth();
        int height2 = rotatedImg2.getHeight();

        int centerX1 = width1/2;
        int centerY1 = height1/2;
        int centerX2 = width2/2;
        int centerY2 = height2/2;

        BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

        int diff;
        int result; // Stores output pixel

        // i is y, j is x
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int rgb1 = img1.getRGB(j, i);
                int rgb2;

                int xFromCenter = (j - centerX1);
                int yFromCenter = (i - centerY1);
                int x2 = xFromCenter + centerX2;
                int y2 = yFromCenter + centerY2;
                int translatedX = x2 - (int) xOffset;
                int translatedY = y2 - (int) yOffset;

                if (translatedX >= 0 && translatedX < width2 && translatedY >= 0 && translatedY < height2) {
                    rgb2 = rotatedImg2.getRGB(translatedX, translatedY);
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

    public static void outputWindows(String experimentName, BufferedImage img, ArrayList<CompWindow> windows) {
        for (int i = 0; i < windows.size(); i++) {
            try {
                File output = new File("windows/" + experimentName + "/win" + i + ".png");
                CompWindow currWindow = windows.get(i);
                BufferedImage outImage = img.getSubimage(currWindow.getX(), currWindow.getY(),
                        currWindow.getWidth(), currWindow.getHeight());
                ImageIO.write(outImage, "png", output);
            } catch (IOException e) {
                System.out.println("File write error");
            }
        }

        System.out.println("Output windows for " + experimentName + " written.");
    }
}
