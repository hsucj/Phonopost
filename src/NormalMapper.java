import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by cjhsu on 4/15/17.
 */
public class NormalMapper {

    String experimentName;
    ScanTransformation top;
    ScanTransformation bottom;
    ScanTransformation left;
    ScanTransformation right;

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

        System.out.println(this.right);
    }

    public BufferedImage getNormalMapImage() {
        BufferedImage base = this.bottom.getOrigImg();
        int width = base.getWidth();
        int height = base.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        BufferedImage normalMap = new BufferedImage(width, height, base.getType());

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

            }
        }
    }
}
