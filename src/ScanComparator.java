import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by cjhsu on 3/8/17.
 */

public class ScanComparator {
    private BufferedImage phonopostImg1;
    private BufferedImage phonopostImg2;
    private int centerX1;
    private int centerY1;
    private double rotationStart;
    private double rotationEnd;
    private double rotationPrecision;
    private int xStart;
    private int xEnd;
    private int yStart;
    private int yEnd;

    private BigInteger sumDistSquared = null;

    //windows are from the center
    private ArrayList<CompWindow> windows;
    private final double scale = 255 * 255;

    public ScanComparator (BufferedImage img1, BufferedImage img2, double rotationStart, double rotationEnd,
                           double rotationPrecision, int xStart, int xEnd, int yStart, int yEnd, ArrayList<CompWindow> windows) {
        this.phonopostImg1 = img1;
        this.phonopostImg2 = img2;
        this.centerX1 = img1.getWidth()/2;
        this.centerY1 = img1.getHeight()/2;
        this.rotationStart = rotationStart;
        this.rotationEnd = rotationEnd;
        this.rotationPrecision = rotationPrecision;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.windows = windows;
    }

    public double[] calculateMinDistance() {
        double minDistanceSquared = Double.MAX_VALUE;
        BigInteger sumMinDistanceSquared = null;

        boolean nullSum = true;

        // 0 is x offset, 1 is y offset, 2 is theta of rotation, 3 is numPixels
        double[] minCoordinates = new double[4];

        // rotate
        for (double theta = rotationStart; theta <= rotationEnd; theta += rotationPrecision) {
            AffineTransform rotation = new AffineTransform();
            rotation.rotate(theta, phonopostImg2.getWidth()/2, phonopostImg2.getHeight()/2);
            AffineTransformOp rotateOp = new AffineTransformOp(rotation, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage rotatedPhonopostImg2 = rotateOp.filter(phonopostImg2, null);

            int centerX2 = rotatedPhonopostImg2.getWidth()/2;
            int centerY2 = rotatedPhonopostImg2.getHeight()/2;


            //System.out.println("theta: " + theta);



            for (int xTranslate = xStart; xTranslate <= xEnd; xTranslate++) {
                breakloop:
                for (int yTranslate = yStart; yTranslate <= yEnd; yTranslate++) {
                    BigInteger bDiffSqSum = BigInteger.ZERO;
                    int numPixels = 0;

                    // for each window
                    for (int winNum = 0; winNum < windows.size(); winNum++) {

                        CompWindow currWindow = windows.get(winNum);

                        for (int x = currWindow.getX(); x < currWindow.getX() + currWindow.getWidth(); x++) {
                            for (int y = currWindow.getY(); y < currWindow.getY() + currWindow.getHeight(); y++) {
                                if ((x + xTranslate) <= 0 || (x + xTranslate) >= phonopostImg2.getWidth() || (y +  yTranslate) <= 0 || (y + yTranslate) >= phonopostImg2.getHeight()) {
                                    System.out.println("out of comp bounds");
                                }

                                int rgb1 = phonopostImg1.getRGB(x, y);
                                int xFromCenter = (x - centerX1) + centerX2;
                                int yFromCenter = (y - centerY1) + centerY2;
                                //System.out.println("X offset: " + (x-centerX1));
                                //System.out.println("Ys: " + y + " " + yFromCenter);

                                int rgb2 = rotatedPhonopostImg2.getRGB(xFromCenter + xTranslate, yFromCenter + yTranslate);

                                int bDiff = ((rgb1) & 0xFF) - ((rgb2) & 0xFF);
                                int bDiffSq = bDiff * bDiff;

                                bDiffSqSum = bDiffSqSum.add(BigInteger.valueOf(bDiffSq));

                                numPixels++;

                                if (!nullSum) {
                                    // if the sum already exceeds a found min, forget this iteration
                                    if (bDiffSqSum.compareTo(sumMinDistanceSquared) > 0) {
                                        break breakloop;
                                    }
                                }
                            }
                        }
                    }


                    if (nullSum) {
                        sumMinDistanceSquared = bDiffSqSum;
                        minCoordinates[0] = xTranslate;
                        minCoordinates[1] = yTranslate;
                        minCoordinates[2] = theta;
                        minCoordinates[3] = numPixels;
                        nullSum = false;
                    }
                    else if (bDiffSqSum.compareTo(sumMinDistanceSquared) < 0) {
                        sumMinDistanceSquared = bDiffSqSum;
                        minCoordinates[0] = xTranslate;
                        minCoordinates[1] = yTranslate;
                        minCoordinates[2] = theta;
                        minCoordinates[3] = numPixels;
                    }
                }
            }

        }



        this.sumDistSquared = sumMinDistanceSquared;

        return minCoordinates;
    }

    public BigInteger getSumDistSquared() {
        return this.sumDistSquared;
    }
}
