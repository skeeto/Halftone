package halftone;

import java.net.URL;

import java.util.Timer;
import java.util.TimerTask;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

import javax.swing.JFrame;
import javax.swing.JComponent;

import javax.imageio.ImageIO;

public class Halftone extends JComponent {
    private static final long serialVersionUID = 1l;

    private BufferedImage image;
    private static final double SQ2 = Math.sqrt(2);
    private static final double SCALE = 3.0;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new Halftone());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public Halftone() {
        setDefaultImage();
        new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    repaint();
                }
            }, 1000l, 1000l);
    }

    private void setDefaultImage()  {
        try {
            URL res = Halftone.class.getResource("/halftone/lenna.png");
            setImage(ImageIO.read(res));
        } catch (java.io.IOException e) {
            System.out.println("Failed to fetch lenna.");
        }
    }

    public void setImage(BufferedImage display) {
        image = display;
        Dimension size = new Dimension(image.getWidth() * 2,
                                       image.getHeight() * 2);
        AffineTransform scale
            = AffineTransform.getScaleInstance(1 / SCALE, 1 / SCALE);
        AffineTransformOp op
            = new AffineTransformOp(scale, AffineTransformOp.TYPE_BICUBIC);
        image = op.filter(image, null);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        /* Adjust the display. */
        g.scale(SCALE, SCALE);
        g.translate(1.0, 1.0);

        /* Tune drawing parameters. */
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);

        /* Draw image. */
        g.setColor(Color.BLACK);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int c = image.getRGB(x, y);
                /* Use the luma model. */
                double sum = (+ 0.30 * (c >> 16 & 0xff)
                              + 0.59 * (c >>  8 & 0xff)
                              + 0.11 * (c >>  0 & 0xff)) / 255.0;
                sum = 1.0 - sum;
                double size = sum * SQ2;
                g.fill(new Ellipse2D.Double((double) x * 2 - size,
                                            (double) y * 2 - size,
                                            size * 2, size * 2));
            }
        }
    }
}
