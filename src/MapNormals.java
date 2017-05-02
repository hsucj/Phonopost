import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by cjhsu on 4/15/17.
 */
public class MapNormals {
    public static void main(String[] args) throws IOException {
        NormalMapper nm = new NormalMapper("scan1");

        BufferedImage nmImage = nm.getNormalMapImage();

        try {
            File nmOutput = new File("normal_maps/scan1.png");
            ImageIO.write(nmImage, "png", nmOutput);
        } catch (IOException e) {
            System.out.println("File write error");
        }
    }
}
