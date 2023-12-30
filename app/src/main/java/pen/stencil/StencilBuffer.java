package pen.stencil;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * A tool for drawing to an off-screen Stencil and saving the result as a PNG.
 */
@SuppressWarnings("unused")
public class StencilBuffer {
    /**
     * The default margin added on the right and bottom of the canvas.
     */
    public static final double DEFAULT_MARGIN = 10;

    //-------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private final Stencil stencil;
    private double margin = DEFAULT_MARGIN;

    // This field needs to be preserved so that it isn't garbage collected;
    // the image conversion won't work right without it.
    @SuppressWarnings("FieldCanBeLocal")
    private final Scene scene;

    //-------------------------------------------------------------------------
    // Constructor

    public StencilBuffer() {
        this.stencil = new Stencil(canvas.getGraphicsContext2D());
        root.getChildren().add(canvas);
        this.scene = new Scene(root);
    }

    //-------------------------------------------------------------------------
    // Configuration


    public double getMargin() {
        return margin;
    }

    /**
     * Sets the margin, in pixels, to be added on the right and bottom sides
     * of the canvas.  It is the drawing's responsibility to leave any
     * desired space on the top and left.
     * @param margin The margin
     */
    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getWidth() {
        return canvas.getWidth();
    }

    public double getHeight() {
        return canvas.getHeight();
    }

    //-------------------------------------------------------------------------
    // Drawing

    /**
     * Draws the drawing, setting the canvas size accordingly
     * @param drawing The drawing
     */
    public void draw(StencilDrawing drawing) {
        stencil.clear();
        stencil.draw(drawing);

        stencil.getDrawingBounds().ifPresent(box -> {
            canvas.setWidth(box.getMaxX() + margin);
            canvas.setHeight(box.getMaxY() + margin);
            stencil.clear();
            stencil.draw(drawing);
        });
    }

    /**
     * Gets the drawing as a JavaFX Image
     * @return The image
     */
    public Image getImage() {
        root.layout();
        return canvas.snapshot(null, null);
    }

    /**
     * Saves the drawing to disk as a PNG image file
     * @param file The file
     * @throws IOException On write error
     */
    public void save(File file) throws IOException {
        saveImage(file, getImage());
    }

    /**
     * Saves the image to a file as a PNG image.
     * @param file The file
     * @param image The image
     * @throws IOException On write error
     */
    public static void saveImage(File file, Image image) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
    }
}
