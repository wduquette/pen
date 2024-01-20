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
    //-------------------------------------------------------------------------
    // Instance Variables

    private final StackPane root = new StackPane();
    private final Canvas canvas = new Canvas();
    private final Stencil stencil;

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
    public void draw(Drawing drawing) {
        // FIRST, draw the image to determine the drawing bounds
        stencil.clear();
        stencil.draw(drawing);

        // NEXT, resize the canvas to fit, draw it again, and save it.
        var size = stencil.getImageSize();
        canvas.setWidth(size.getWidth());
        canvas.setHeight(size.getHeight());
        stencil.clear();
        stencil.draw(drawing);
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
