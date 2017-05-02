import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by cjhsu on 5/1/17.
 */
public class ScanGUI extends JComponent implements ChangeListener {

    JPanel gui;

    JLabel scanImageCanvas;
    Dimension size;
    double scale = 1.0;
    private BufferedImage scanImage;
    private RegionSelectorListener windowSelector;

    public ScanGUI(File scanDirectory) {
        setBackground(Color.black);
        try {
            scanImage = ImageIO.read(new File(scanDirectory.getPath() + "/color0.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScanImageCanvas(Image image) {
        scanImageCanvas.setIcon(new ImageIcon(image));
    }

    public void initComponents() {
        if (gui == null) {
            gui = new JPanel(new BorderLayout());
            gui.setBorder(new EmptyBorder(5, 5, 5, 5));
            scanImageCanvas = new JLabel();
            windowSelector = new RegionSelectorListener(scanImageCanvas);
            JPanel imageCenter = new JPanel(new GridBagLayout());
            imageCenter.add(scanImageCanvas);
            JScrollPane imageScroll = new JScrollPane(imageCenter);
            imageScroll.setPreferredSize(new Dimension(300, 100));
            gui.add(imageScroll, BorderLayout.CENTER);
        }
    }

    public void stateChanged(ChangeEvent e) {
        int value = ((JSlider) e.getSource()).getValue();
        scale = value / 100.0;
        paintImage();
    }


    public Container getGui() {
        initComponents();
        return gui;
    }

    protected void paintImage() {
        int w = getWidth();
        int h = getHeight();
        int imageWidth = scanImage.getWidth();
        int imageHeight = scanImage.getHeight();
        BufferedImage bi = new BufferedImage((int) (imageWidth * scale), (int) (imageHeight * scale), scanImage.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double x = (w - scale * imageWidth) / 2;
        double y = (h - scale * imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(scanImage, at);
        setScanImageCanvas(bi);
    }

    public Dimension getPreferredSize() {
        int w = (int) (scale * size.width);
        int h = (int) (scale * size.height);
        return new Dimension(w, h);
    }

    private JSlider getControl() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 5);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        return slider;
    }

    public static void main(String[] args) {
        File scanDirectory = null;
//        JFrame gui = new JFrame("Sonorine");
//        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JLabel scanImage = new JLabel();
//
//        gui.getContentPane().add(scanImage);
//        gui.setSize(460,200);
//        gui .setVisible(true);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());
            scanDirectory = chooser.getSelectedFile();
            ScanGUI sgui = new ScanGUI(scanDirectory);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(sgui.getGui());

            sgui.setScanImageCanvas(sgui.scanImage);

            frame.getContentPane().add(sgui.getControl(), "Last");
            frame.setSize(700, 500);
            frame.setLocation(200, 200);
            frame.setVisible(true);
        }
        else {
            System.out.println("No Selection ");
        }

//        JFileChooser fileopen = new JFileChooser();
//        int ret = fileopen.showDialog(null, "Open file");
//        if (ret == JFileChooser.APPROVE_OPTION) {
//
//            File file = fileopen.getSelectedFile();
//            Image scan = null;
//            try {
//                scan = ImageIO.read(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ImageIcon icon = new ImageIcon(scan);
//            i_from.setIcon(icon);
//        }
    }
}
