import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Created by cjhsu on 4/15/17.
 */
public class ScanTransformation {
    private BufferedImage origImg;
    private BufferedImage rotImg;
    private double theta;
    private int xOffset;
    private int yOffset;
    private int transformedCenterX;
    private int transformedCenterY;

    public static final int BOTTOM = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int LEFT = 4;


    public ScanTransformation (BufferedImage origImg, double theta, int xOffset, int yOffset) {
        this.origImg = origImg;
        this.theta = theta;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        AffineTransform rotation = new AffineTransform();
        rotation.rotate(theta, origImg.getWidth()/2, origImg.getHeight()/2);
        AffineTransformOp rotateOp = new AffineTransformOp(rotation, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage rotatedPhonopostImg = rotateOp.filter(origImg, null);

        this.rotImg = rotatedPhonopostImg;
        this.transformedCenterX = rotatedPhonopostImg.getWidth() / 2;
        this.transformedCenterY = rotatedPhonopostImg.getHeight() / 2;
    }

    public BufferedImage getOrigImg() {
        return this.origImg;
    }

    public double getTheta() { return this.theta;}

    public int getTransformedRGB (int xFromCenter, int yFromCenter) {
        int targetX = this.transformedCenterX + xFromCenter - this.xOffset;
        int targetY = this.transformedCenterY + yFromCenter - this.yOffset;

        if (targetX < 0 || targetX >= rotImg.getWidth() || targetY < 0 || targetY >= rotImg.getHeight()) {
            return -1;
        }

        return this.rotImg.getRGB(targetX, targetY);
    }
}
