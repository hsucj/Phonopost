import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by cjhsu on 4/15/17.
 */
public class NormalMapper {

    private String experimentName;
    private ScanTransformation top;
    private ScanTransformation bottom;
    private ScanTransformation left;
    private ScanTransformation right;

    private static final double l1x = 0;
    private static final double l1y = 0.2;
    private static final double l1z = 0.9797;

    public NormalMapper (String inputFileName) throws IOException {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("alignments/" + inputFileName))) {

            this.experimentName = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] transformNums = line.split(" ");
                String orientation = transformNums[0];
                String imgFileName = transformNums[1];
                double theta = Double.valueOf(transformNums[2]);
                int xOffset = Integer.valueOf(transformNums[3]);
                int yOffset = Integer.valueOf(transformNums[4]);

                BufferedImage img = null;

                try {
                    img = ImageIO.read(new File(imgFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (orientation.equals("top")) {
                    this.top = new ScanTransformation(img, theta, xOffset, yOffset);
                } else if (orientation.equals("bottom")) {
                    this.bottom = new ScanTransformation(img, theta, xOffset, yOffset);
                } else if (orientation.equals("left")) {
                    this.left = new ScanTransformation(img, theta, xOffset, yOffset);
                } else if (orientation.equals("right")) {
                    this.right = new ScanTransformation(img, theta, xOffset, yOffset);
                }
            }
        }

    }

    public BufferedImage getNormalMapImage() {
        BufferedImage base = this.bottom.getOrigImg();
        int width = base.getWidth();
        int height = base.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        BufferedImage normalMap = new BufferedImage(width, height, base.getType());

        // Creating L matrix

        double rightRotation = -1.5708 + this.right.getTheta();

        double l2x = l1x * Math.cos(rightRotation) - l1y * Math.sin(rightRotation);
        double l2y = l1y * Math.cos(rightRotation) + l1x * Math.sin(rightRotation);
        double l2z = l1z;

        double topRotation = -(2 * 1.5708) + this.top.getTheta();

        double l3x = l1x * Math.cos(topRotation) - l1y * Math.sin(topRotation);
        double l3y = l1y * Math.cos(topRotation) + l1x * Math.sin(topRotation);
        double l3z = l1z;

        double leftRotation = -(3 * 1.5708) + this.top.getTheta();

        double l4x = l1x * Math.cos(leftRotation) - l1y * Math.sin(leftRotation);
        double l4y = l1y * Math.cos(leftRotation) + l1x * Math.sin(leftRotation);;
        double l4z = l1z;

        Vector<Vector<Double>> L = new Vector<Vector<Double>>();

        Vector<Double> l1 = new Vector<Double>();
        l1.add(l1x);
        l1.add(l1y);
        l1.add(l1z);
        Vector<Double> l2 = new Vector<Double>();
        l2.add(l2x);
        l2.add(l2y);
        l2.add(l2z);
        Vector<Double> l3 = new Vector<Double>();
        l3.add(l3x);
        l3.add(l3y);
        l3.add(l3z);
        Vector<Double> l4 = new Vector<Double>();
        l4.add(l4x);
        l4.add(l4y);
        l4.add(l4z);

        L.add(l1);
        L.add(l2);
        L.add(l3);
        L.add(l4);

        // calculate (L^T L)^-1 L^T

        // raise values of blue channel to power of 2.2 for gamma correction

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int xFromCenter = i - centerX;
                int yFromCenter = j - centerY;

                int rightRGB = this.right.getTransformedRGB(xFromCenter, yFromCenter) & 0xFF;
                int leftRGB = this.left.getTransformedRGB(xFromCenter, yFromCenter) & 0xFF;

                int topRGB = this.top.getTransformedRGB(xFromCenter, yFromCenter) & 0xFF;
                int bottomRGB = this.bottom.getTransformedRGB(xFromCenter, yFromCenter) & 0xFF;

                if (rightRGB < 0 || leftRGB < 0 || topRGB < 0 || bottomRGB < 0) {
                    normalMap.setRGB(i, j, 0);
                    continue;
                }

//                double nx = rightRGB - leftRGB;
                double ny = topRGB - bottomRGB;
                double nx = 0.0;

                nx /= 255;
                ny /= 255;

                double nz = Math.sqrt(1.0 - nx * nx - ny * ny);

                int r = (int) ((0.5 + 0.5 * nx) * 255);
                int g = (int) ((0.5 + 0.5 * ny) * 255);
                int b = (int) ((0.5 + 0.5 * nz) * 255);
                int color = (r << 16) | (g << 8) | b;




                normalMap.setRGB(i, j, color);
            }
        }

        return normalMap;
    }
}
