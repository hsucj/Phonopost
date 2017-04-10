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
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

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
            line = br.readLine();
            rotationPrecision = Double.valueOf(line);
            line = br.readLine();
            xStart = Integer.valueOf(line);
            line = br.readLine();
            xEnd = Integer.valueOf(line);
            line = br.readLine();
            yStart = Integer.valueOf(line);
            line = br.readLine();
            yEnd = Integer.valueOf(line);

            while ((line = br.readLine()) != null) {
                String[] windowArgs = line.split(" ");
                CompWindow newWindow = new CompWindow(Integer.parseInt(windowArgs[0]),
                        Integer.parseInt(windowArgs[1]), Integer.parseInt(windowArgs[2]), Integer.parseInt(windowArgs[3]));
                windows.add(newWindow);
            }
        }

        BufferedImage phonopostImg1 = ImageIO.read(new File(phonopostFile1));
        BufferedImage phonopostImg2 = ImageIO.read(new File(phonopostFile2));

        ImageUtils.outputWindows(experimentFileName, phonopostImg1, windows);

        ScanComparator sc = new ScanComparator(experimentFileName, phonopostImg1, phonopostImg2, rotationStart, rotationEnd,
                rotationPrecision, xStart, xEnd, yStart, yEnd, windows);

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

    public static void writeDiffImage(String experimentFileName, String phonopostFile1, String phonopostFile2,
                                      double theta, double xOffset, double yOffset) {
        BufferedImage phonopostImg1 = null;
        try {
            phonopostImg1 = ImageIO.read(new File(phonopostFile1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage phonopostImg2 = null;
        try {
            phonopostImg2 = ImageIO.read(new File(phonopostFile2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage diffImage = ImageUtils.getDifferenceImage(phonopostImg1, phonopostImg2, theta, xOffset, yOffset);

        try {
            File diffOutput = new File("results/" + experimentFileName + "/diff.png");
            ImageIO.write(diffImage, "png", diffOutput);
        } catch (IOException e) {
            System.out.println("File write error");
        }
    }

    public static void main(String[] args) throws IOException {
        String[] experiments = new String[]{"experiment1", "experiment2", "experiment3"};

        // runExperiment(experiments[0]);

//        for (String experiment : experiments) {
//            System.out.println("Running experiment: " + experiment);
//            runExperiment(experiment);
//            System.out.println("Experiment complete: " + experiment);
//        }

        writeDiffImage("experiment1", "scan1/color0.png", "scan1/color1.png", 0.047050000000000085, 26, 40);
    }
}

