/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs1302.gallery;

import javafx.application.*;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.stage.*;

public class GalleryApp extends Application {

        /**
         * This method prints "Initializing" right before
         * the gui starts.
         */
    @Override
    public void init() {
        System.out.println("***Gallery Application***");
        System.out.println("Initializing...");
    } // init

    static Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(new MainGui(), 633, 595);
        stage.setTitle("Habi Gallery!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    } // start

        /**
         * This method prints "Terminating" right before
         * the gui stops.
         */
    @Override
    public void stop() {
        System.out.println("Terminating...");
    } // stop
    
        /**
         * This is the main method
         */
    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            System.err.println("If this is a DISPLAY problem, then your X server connection");
            System.err.println("has likely timed out. This can generally be fixed by logging");
            System.err.println("out and logging back in.");
            System.exit(1);
        } // try
    } // main
        /**
         * This method adds CSS styling to the 
         * scene when it is called.
         */
    public static void style(){
        scene.getStylesheets().add("style.css");
    } // style
    
        /**
         * This method removes CSS styling from the 
         * scene when it is called.
         */
    public static void unStyle(){
        scene.getStylesheets().clear();
    } // unStyle
    
        /**
         * This method dispays a new 
         * stage that contains information
         * about the auther of this API.
         */
    public static void about(){
        ImageView imageView = new ImageView("About.PNG");
        Pane pane = new Pane();
        pane.getChildren().add(imageView);
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setTitle("About Habi");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.setResizable(false);
        stage.show();
    } // about

} // GalleryApp

