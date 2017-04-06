import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by cjhsu on 3/8/17.
 */
public class Driver {


    public static void runExperiment(String experimentFileName) throws IOException {

        String phonopostFile1;
        String phonopostFile2;

        double rotationStart;
        double rotationEnd;
        double rotationPrecision;

        ArrayList<CompWindow> windows = new ArrayList<CompWindow>();

        // Read in windows of interest
        try (BufferedReader br = new BufferedReader(new FileReader("experiments/" + experimentFileName))) {
            String line;
            line = br.readLine();
            phonopostFile1 = line;
            line = br.readLine();
            phonopostFile2 = line;
            line = br.readLine();
            rotationStart = Double.valueOf(line);
            line = br.readLine();
            rotationEnd = Double.valueOf(line);
            line= br.readLine();
            rotationPrecision = Double.valueOf(line);

            while ((line = br.readLine()) != null) {
                String[] windowArgs = line.split(" ");
                CompWindow newWindow = new CompWindow(Integer.parseInt(windowArgs[0]),
                        Integer.parseInt(windowArgs[1]), Integer.parseInt(windowArgs[2]), Integer.parseInt(windowArgs[3]));
                windows.add(newWindow);
            }
        }

        BufferedImage phonopostImg1 = ImageIO.read(new File(phonopostFile1));
        BufferedImage phonopostImg2 = ImageIO.read(new File(phonopostFile2));

        ScanComparator sc = new ScanComparator(phonopostImg1, phonopostImg2, rotationStart, rotationEnd, rotationPrecision, windows);

        final long startTime = System.currentTimeMillis();
        double[] minCoordinates = sc.calculateMinDistance();
        final long endTime = System.currentTimeMillis();

        // write text output data
        try{
            PrintWriter writer = new PrintWriter("results/" + experimentFileName + "/output" , "UTF-8");
            writer.println("Moving " + phonopostFile2 + " to align with " + phonopostFile1);
            writer.println("X: " + minCoordinates[0]);
            writer.println("Y: " + minCoordinates[1]);
            writer.println("Theta: " + minCoordinates[2]);
            writer.println("Num pixels: " + minCoordinates[3]);
            writer.println("Sum Dist Squared: " + sc.getSumDistSquared().toString());

            writer.println("Execution time: " + (endTime - startTime));
            writer.println();
            writer.println();
            writer.close();
        } catch (IOException e) {
            System.out.println("PrintWriter error");
        }

        // write difference data
    }

    public static void main(String[] args) throws IOException {
        String[] experiments = new String[]{"experiment1", "experiment2", "experiment3"};

        for (String experiment : experiments) {
            runExperiment(experiment);
        }
    }
}

