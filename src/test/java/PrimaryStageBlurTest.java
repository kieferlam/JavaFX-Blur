import com.kieferlam.javafxblur.Blur;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PrimaryStageBlurTest extends Application {

    public static final Color titleBarColour = new Color(Color.SLATEGREY.getRed(), Color.SLATEGREY.getGreen(), Color.SLATEGREY.getBlue(), 0.5);
    public static final Background titleBarBackground = new Background(new BackgroundFill(titleBarColour, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Color highlightColour = new Color(Color.SLATEGREY.getRed(), Color.SLATEGREY.getGreen(), Color.SLATEGREY.getBlue(), 0.9);
    public static final Background highlightBackground = new Background(new BackgroundFill(highlightColour, CornerRadii.EMPTY, Insets.EMPTY));
    public static final Color textColor = Color.GHOSTWHITE;

    public static void main(String[] args) {
        Blur.loadBlurLibrary();
        launch(args);
    }

    public static Pane createTitleBar(Stage primaryStage, StringProperty titleProperty) {
        AnchorPane titlebar = new AnchorPane();

        titlebar.setBackground(titleBarBackground);

        Text titleText = new Text(0, 0, titleProperty.getValue());
        titleText.textProperty().bind(titleProperty);
        titleText.setFill(textColor);
        titleText.setFont(new Font(14.0));
        HBox titleTextContainer = new HBox();
        titleTextContainer.getChildren().add(titleText);

        HBox titlebarControls = new HBox();
        Button minBtn = new Button("Min");
        minBtn.setBackground(titleBarBackground);
        Button maxBtn = new Button("Max");
        maxBtn.setBackground(titleBarBackground);
        Button closeBtn = new Button("Close");
        closeBtn.setBackground(titleBarBackground);

        closeBtn.setOnAction(event -> primaryStage.close());

        titlebarControls.getChildren().addAll(minBtn, maxBtn, closeBtn);
        titlebarControls.getChildren().forEach(node -> {
            ((Button)node).setTextFill(textColor);
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> ((Button) node).setBackground(highlightBackground));
            node.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> ((Button) node).setBackground(titleBarBackground));
        });

        titlebar.getChildren().add(titleTextContainer);
        titlebar.getChildren().add(titlebarControls);

        AtomicReference<Point2D> stageDragOffset = new AtomicReference<>(new Point2D(0.0, 0.0));
        titlebar.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> stageDragOffset.set(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY())));
        titlebar.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent ->{
                primaryStage.setX(mouseEvent.getScreenX() - stageDragOffset.get().getX());
                primaryStage.setY(mouseEvent.getScreenY() - stageDragOffset.get().getY());
        });

        AnchorPane.setLeftAnchor(titleTextContainer, 5.0);
        AnchorPane.setRightAnchor(titlebarControls, 0.0);

        return titlebar;
    }

    public static Pane createContentPane(Stage primaryStage){
        BorderPane pane = new BorderPane();

        pane.setBackground(new Background(new BackgroundFill(Color.gray(0.2, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

        Text content = new Text("Content");
        content.setFont(new Font(72.0));
        content.setFill(textColor);

        pane.setCenter(content);

        pane.prefWidthProperty().bind(primaryStage.widthProperty());
        pane.prefHeightProperty().bind(primaryStage.heightProperty());

        return pane;
    }

    public void start(Stage primaryStage) {
        Pane titlebar = createTitleBar(primaryStage, new SimpleStringProperty("JavaFX Blur Test"));

        Pane contentPane = createContentPane(primaryStage);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(titlebar, contentPane);
        AnchorPane.setTopAnchor(titlebar, 0.0);
        AnchorPane.setTopAnchor(contentPane, 24.0);

        root.setBackground(Background.EMPTY);
        Scene scene = new Scene(root, 640.0, 480.0);
        scene.setFill(Color.TRANSPARENT);

        titlebar.prefWidthProperty().bind(scene.widthProperty());
        titlebar.prefHeight(24.0);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("JavaFX");

        primaryStage.setScene(scene);
        primaryStage.show();

        Blur.applyBlur(primaryStage);
    }
}
