import java.awt.image.BufferedImage;

/**
 * Created by cjhsu on 3/8/17.
 */

public class ScanComparator {
    private BufferedImage phonopostImg1;
    private BufferedImage phonopostImg2;
    private int centerX1;
    private int centerY1;
    private int centerX2;
    private int centerY2;
    private List<CompWindow> windows;
    private final int comparisonRadius = 10;
    private final double scale = 255 * 255;

    public ScanComparator (BufferedImage img1, BufferedImage img2, List<CompWindow> windows) {
        this.phonopostImg1 = img1;
        this.phonopostImg2 = img2;
        this.centerX1 = img1.getWidth()/2;
        this.centerY1 = img1.getHeight()/2;
        this.centerX2 = img2.getWidth()/2;
        this.centerY2 = img2.getHeight()/2;
        this.windows = windows;
    }

    // positive radians for counterclockwise rotation
    private double rotationSample (double radians, double x, double y) {

    }

    public double[] minDistance() {
        double minDistanceSquared = Double.MAX_VALUE;

//        double minR = Double.MAX_VALUE;
//        double minG = Double.MAX_VALUE;
//        double minB = Double.MAX_VALUE;

        double[] minCoordinates = new double[6];

        for (int xTranslate = -comparisonRadius; xTranslate <= comparisonRadius; xTranslate++) {
            for (int yTranslate = -comparisonRadius; yTranslate <= comparisonRadius; yTranslate++) {
                double distSum = 0;
                double rDiffSum = 0;
//                double gDiffSum = 0;
//                double bDiffSum = 0;
                int numPixels = 0;
                for (int x = 60; x < phonopostImg1.getWidth() - 60; x++) {
                    for (int y = 60; y < phonopostImg1.getHeight() - 4000; y++) {
                        if ((x + xTranslate) <= 0 || (x + xTranslate) >= phonopostImg2.getWidth() || (y +  yTranslate) <= 0 || (y + yTranslate) >= phonopostImg2.getHeight()) {
                            continue;
                        }
                        numPixels++;
                        int rgb1 = phonopostImg1.getRGB(x, y);
                        int rgb2 = phonopostImg2.getRGB(x + xTranslate, y + yTranslate);

                        int rDiff = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);

//                        int gDiff = ((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF);
//                        int bDiff = ((rgb1) & 0xFF) - ((rgb2) & 0xFF);

                        // Scaled 0-1
                        double rDiffSq = (rDiff * rDiff);
//                        double gDiffSq = (gDiff * gDiff);
//                        double bDiffSq = (bDiff * bDiff);

                        distSum += rDiffSq;
                        rDiffSum += rDiffSq;
//                        gDiffSum += gDiffSq;
//                        bDiffSum += bDiffSq;
                    }
                }

                if ((distSum / numPixels) < minDistanceSquared) {
                    minDistanceSquared = distSum / numPixels;

//                    minR = rDiffSum / numPixels;
//                    minG = gDiffSum / numPixels;
//                    minB = bDiffSum / numPixels;

                    minCoordinates[0] = xTranslate;
                    minCoordinates[1] = yTranslate;
                    minCoordinates[2] = minDistanceSquared;
//                    minCoordinates[3] = minR;
//                    minCoordinates[4] = minG;
//                    minCoordinates[5] = minB;
                }
            }
        }

        return minCoordinates;
    }
}
