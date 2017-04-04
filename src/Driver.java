import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by cjhsu on 3/8/17.
 */
public class Driver {

    public static void main(String[] args) throws IOException {
        String experimentFileName = args[0];

        String phonopostFile1;
        String phonopostFile2;

        List<CompWindow> windows = new ArrayList<CompWindow>();

        // Read in windows of interest
        try (BufferedReader br = new BufferedReader(new FileReader(experimentFileName))) {
            String line;
            line = br.readLine();
            phonopostFile1 = line;
            line = br.readLine();
            phonopostFile2 = line;

            while ((line = br.readLine()) != null) {
                String[] windowArgs = line.split(" ");
                CompWindow newWindow = new CompWindow(windowArgs[0], windowArgs[1], windowArgs[2], windowArgs[3]);
                windows.add(newWindow);
            }
        }

        BufferedImage phonopostImg1 = ImageIO.read(new File(phonopostFile1));
        BufferedImage phonopostImg2 = ImageIO.read(new File(phonopostFile2));

        ScanComparator sc = new ScanComparator(phonopostImg1, phonopostImg2, windows);

        final long startTime = System.currentTimeMillis();
        double[] minCoordinates = sc.minDistance();
        final long endTime = System.currentTimeMillis();


        System.out.println("Moving " + phonopostFile2 + " to align with " + phonopostFile1);
        System.out.println("X: " + minCoordinates[0]);
        System.out.println("Y: " + minCoordinates[1]);
        System.out.println("Sum of distances squared (scaled): " + minCoordinates[2]);
//        System.out.println("Red distance: " + minCoordinates[3]);
//        System.out.println("Green distance: " + minCoordinates[4]);
//        System.out.println("Blue distance: " + minCoordinates[5]);
        System.out.println("Execution time: " + (endTime - startTime));
    }
}

