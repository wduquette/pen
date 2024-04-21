package pen.widgets;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

public class HtmlView extends StackPane {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final WebView webView = new WebView();

    //-------------------------------------------------------------------------
    // Constructor

    public HtmlView() {
        var resource = getClass().getResource("HtmlView.css");
        assert resource != null;
        System.out.println("CSS: " + resource.toExternalForm());
        webView.getEngine().setUserStyleSheetLocation(resource.toExternalForm());

        getChildren().add(webView);
    }

    //-------------------------------------------------------------------------
    // API

    /**
     * Displays the given HTML in the view.
     * @param html The HTML to display
     */
    public void show(String html) {
        webView.getEngine().loadContent(html);
    }


}
