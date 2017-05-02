import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    public static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }

    public static double[][] multiplyByMatrix(double[][] m1, double[][] m2) {
        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if(m1ColLength != m2RowLength) return null; // matrix multiplication is not possible
        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for(int i = 0; i < mRRowLength; i++) {         // rows from m1
            for(int j = 0; j < mRColLength; j++) {     // columns from m2
                for(int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return mResult;
    }

    public static double[][] invert(double a[][])
    {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i=0; i<n-1; ++i)
            for (int j=i+1; j<n; ++j)
                for (int k=0; k<n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i]*b[index[i]][k];

        // Perform backward substitutions
        for (int i=0; i<n; ++i)
        {
            x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
            for (int j=n-2; j>=0; --j)
            {
                x[j][i] = b[index[j]][i];
                for (int k=j+1; k<n; ++k)
                {
                    x[j][i] -= a[index[j]][k]*x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    // Method to carry out the partial-pivoting Gaussian
    // elimination.  Here index[] stores pivoting order.

    public static void gaussian(double a[][], int index[])
    {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i=0; i<n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i=0; i<n; ++i)
        {
            double c1 = 0;
            for (int j=0; j<n; ++j)
            {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j=0; j<n-1; ++j)
        {
            double pi1 = 0;
            for (int i=j; i<n; ++i)
            {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1)
                {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i)
            {
                double pj = a[index[i]][j]/a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l=j+1; l<n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
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
        // calculate (L^T L)^-1 L^T

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

        double[][] L = new double[4][3];

        L[0][0] = l1x;
        L[0][1] = l1y;
        L[0][2] = l1z;

        L[1][0] = l2x;
        L[1][1] = l2y;
        L[1][2] = l2z;

        L[2][0] = l3x;
        L[2][1] = l3y;
        L[2][2] = l3z;

        L[3][0] = l4x;
        L[3][1] = l4y;
        L[3][2] = l4z;

        double[][] L_t = this.transposeMatrix(L);

        double[][] L_t_L = this.multiplyByMatrix(L_t, L);

        double[][] L_t_L_inv = this.invert(L_t_L);

        double[][] multToColor = this.multiplyByMatrix(L_t_L_inv, L_t);

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

                double[][] colorMat = new double[4][1];
                colorMat[0][0] = bottomRGB;
                colorMat[1][0] = rightRGB;
                colorMat[2][0] = topRGB;
                colorMat[3][0] = leftRGB;

                double[][] normal = this.multiplyByMatrix(multToColor, colorMat);

                double magnitude = Math.sqrt(normal[0][0] * normal[0][0] + normal[1][0] * normal[1][0] + normal[2][0] * normal[2][0]);

                double[] normalizedNorm = new double[3];
                normalizedNorm[0] = normal[0][0] / magnitude;
                normalizedNorm[1] = normal[1][0] / magnitude;
                normalizedNorm[2] = normal[2][0] / magnitude;


//                double nx = rightRGB - leftRGB;
                double ny = topRGB - bottomRGB;
                double nx = 0.0;

                nx /= 255;
                ny /= 255;

                double nz = Math.sqrt(1.0 - nx * nx - ny * ny);

                int r = (int) ((0.5 + 0.5 * normalizedNorm[0]) * 255);
                int g = (int) ((0.5 + 0.5 * normalizedNorm[1]) * 255);
                int b = (int) ((0.5 + 0.5 * normalizedNorm[2]) * 255);
                int color = (r << 16) | (g << 8) | b;




                normalMap.setRGB(i, j, color);
            }
        }

        return normalMap;
    }
}
