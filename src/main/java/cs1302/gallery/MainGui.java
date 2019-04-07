package cs1302.gallery;

import com.google.gson.*;
import static cs1302.gallery.GalleryApp.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import java.io.*;
import java.net.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.util.*;
import javafx.animation.*;
import javafx.animation.Animation.Status;
import javafx.application.*;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.*;

/*
 * This class creates the entire interface
 * for the application and also have methods 
 * to handle all the events triggered by actions
 */
public class MainGui extends VBox {

    //List to hold images' urls
    ArrayList<String> imageUrlList = new ArrayList<>();
    ArrayList<String> tempImageUrlList = new ArrayList<>();

    //Create HBox for the progress bar
    ProgressBar pb = new ProgressBar(1);
    Label lbitunes = new Label("Images provided courtesy of iTunes");
    BorderPane borderPane = new BorderPane();
    HBox hBoxForPb = new HBox(5);

    //GridPane
    GridPane gridPane = new GridPane();

    //Search bar
    Button btPause = new Button("Pause");
    Label lbSearchQuery = new Label("Search Query: ");
    TextField tf = new TextField("Illenium");
    Button btUpdateImages = new Button("Update Images");
    HBox hBoxSearchBar = new HBox(5);
    Separator separator = new Separator();
    

    //Menu
    MenuBar menuBar = new MenuBar();
    Menu menuFile = new Menu("File");
    MenuItem menuItemExit = new MenuItem("Exit");
    Menu menuTheme = new Menu("Theme");
    MenuItem menuItemDefault = new MenuItem("Default");
    MenuItem menuItemDark = new MenuItem("Dark");
    Menu menuHelp = new Menu("Help");
    MenuItem menuItemAbout = new MenuItem("About");

    //Timeline for animation
    Timeline timeline = new Timeline();

    /*
     * This constructor takes no argument and 
     * but creates the main object. It also contains
     * event handlers as well.
     * <p>
     * In this constructor, all the nodes declared in the global scope
     * are added to an pane that extends HBOX. It also contain the implementation
     * of a timeline which takes care of the repeative changing of images. Lastly.
     * it contains event handler.
     */
    public MainGui() {
        //Progressbar
        hBoxForPb.setAlignment(Pos.CENTER_LEFT);
        hBoxForPb.getChildren().addAll(pb, lbitunes);
        borderPane.setLeft(hBoxForPb);

        //Search bar
        separator.setOrientation(Orientation.VERTICAL);
        hBoxSearchBar.getChildren().addAll(btPause, separator, lbSearchQuery, tf, btUpdateImages);
        hBoxSearchBar.setAlignment(Pos.CENTER_LEFT);
        hBoxSearchBar.setPadding(new Insets(0, 3, 0, 0));

        //Menu bar
        menuFile.getItems().add(menuItemExit);
        menuTheme.getItems().addAll(menuItemDefault, menuItemDark);
        menuHelp.getItems().add(menuItemAbout);
        menuBar.getMenus().addAll(menuFile, menuTheme, menuHelp);

        //Settings
        //adding to the VBox
        gridPane.setPadding(new Insets(4, 0, 0, 0));
        getChildren().addAll(menuBar, hBoxSearchBar, gridPane, borderPane);
        setSpacing(3);
        setPadding(new Insets(3, 3, 3, 3));

        search();

        //Animation
        try {
            EventHandler<ActionEvent> handler = event -> {
                Collections.shuffle(imageUrlList);
                int randomX = 1 + (int) (Math.random() * 3);
                int randomY = (int) (Math.random() * 4);
                int randomImageIndex = 1 + (int) (Math.random() * (imageUrlList.size() - 1));
                Image image = new Image(imageUrlList.get(randomImageIndex));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(126);
                imageView.setFitWidth(126);
                gridPane.add(imageView, randomX, randomY);

            };
            KeyFrame keyFrame;
            keyFrame = new KeyFrame(Duration.seconds(2), handler);
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } //catch

        /**
         * When the button btUpdateImages is pressed, the method search() is
         * called.
         */
        btUpdateImages.setOnAction(e -> {
            search();
        });

        /**
         * When the search TextField is set on action by pressing Enter, the
         * method search() is called.
         */
        tf.setOnAction(e -> {
            search();
        });

        //EventHandlers
        /**
         * When the button btPause is pressed, the gridPane animation is either
         * paused or resumed.
         */
        btPause.setOnAction(e -> {
            if (btPause.getText().equals("Pause")) {
                timeline.pause();
                btPause.setText("Play");
            } else {
                timeline.play();
                btPause.setText("Pause");
            } // else
        });

        /**
         * When the button menuItemExit is pressed, the application terminates.
         */
        menuItemExit.setOnAction(e -> {
            System.exit(0);
        });

        /**
         * When the button menuItemDefault is pressed, the API theme switches to
         * default.
         */
        menuItemDefault.setOnAction(e -> {
            unStyle();
        });

        /**
         * When the button menuItemNeon is pressed, the API theme switches to
         * neon.
         */
        menuItemDark.setOnAction(e -> {
            style();
        });

        /**
         * When the button menuItemAbout is pressed, a new window pops up
         * displaying information about the author of this API.
         */
        menuItemAbout.setOnAction(e -> {
            about();
        });

    } // MainGui

    /**
     * This method displays a gallery of images based on the results of a search
     * query to the iTunes Search API
     * <p>
     * The method first creates a new thread in order for the application to not
     * freeze. It then searches the iTunes API for images. It then stores the result
     * in an ArrayList. Duplicates are then removed from the list.Finally, it displays
     * then in a GridPane through a Platform.runLater() method that ensures that 
     * this part of the code is ran on the other thread.
     */
    public void search() {
        try {
            Thread t = new Thread(() -> {
                tempImageUrlList.clear();
                URL url;
                InputStreamReader reader;
                try {
                    url = new URL("https://itunes.apple.com/search?term=" + tf.getText().replaceAll("\\s+", "+"));
                    reader = new InputStreamReader(url.openStream());
                    JsonParser jp = new JsonParser();
                    JsonElement je = jp.parse(reader);
                    JsonObject root = je.getAsJsonObject();                      // root of response
                    JsonArray results = root.getAsJsonArray("results");          // "results" array
                    int numResults = results.size();                             // "results" array size
                    for (int i = 0; i < numResults; i++) {
                        JsonObject result = results.get(i).getAsJsonObject();    // object i in array
                        JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
                        if (artworkUrl100 != null) {                             // member might not exist
                            tempImageUrlList.add(artworkUrl100.getAsString());   // get member as string
                        } // if
                    } // for
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

                // Remove duplicates
                Set<String> hs = new HashSet<>();
                hs.addAll(tempImageUrlList);
                tempImageUrlList.clear();
                tempImageUrlList.addAll(hs);
                if (!(tempImageUrlList.size() < 20)) {
                    imageUrlList.clear();
                    imageUrlList.addAll(tempImageUrlList);
                    for (int x = 0; x < 4; x++) {
                        for (int y = 0; y < 5; y++) {
                            int a = x;
                            int b = y;
                            Image image = new Image(imageUrlList.get((a * 5) + b));
                            ImageView imageView = new ImageView(image);
                            imageView.setFitHeight(126);
                            imageView.setFitWidth(126);
                            Runnable r = () -> {
                                gridPane.add(imageView, b, a);
                                pb.setProgress((double) ((a * 5) + b + 1) / (double) 20);
                            };
                            Platform.runLater(r);
                        } // for
                    } //for

                } else {
                    //Alert User
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Exception");
                        alert.setHeaderText(null);
                        alert.setContentText("Less than 20 items has been found");
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.showAndWait();
                    });
                } //else

            });
            t.start();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } // catch
    } // Search
} // MainGui
