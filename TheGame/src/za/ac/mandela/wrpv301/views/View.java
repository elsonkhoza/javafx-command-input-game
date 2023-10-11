package za.ac.mandela.wrpv301.views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import za.ac.mandela.wrpv301.controllers.GameController;


public class View extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Get the scene
        Scene scene = layout();
        // Connect to the controller
        GameController c = new GameController(scene);
        primaryStage.setScene(scene);
        // show
        primaryStage.show();
    }

    /**
     * Creates the layout of  the game
     * @return newly created scene
     */
    private Scene layout() {

        // Screen
        TextArea screen = new TextArea();
        screen.setId("screen");
        screen.setEditable(false);
        screen.setWrapText(true);
        screen.setPrefWidth(600);
        screen.setPrefHeight(400);
        // Input
        TextField enterFiled = new TextField();
        enterFiled.setId("text");
        enterFiled.setPrefWidth(600);
        enterFiled.setPrefHeight(50);

        // Commands
        TextArea commands = new TextArea();
        commands.setEditable(false);
        commands.appendText("\n consume [name]");
        commands.appendText("\n see items");
        commands.appendText("\n take [item name]");
        commands.appendText("\n drop [item name]");
        commands.appendText("\n examine [item name]");
        commands.appendText("\n use [item name]");
        commands.appendText("\n unlock door");
        commands.appendText("\n n(north) s(south) e(east) w(west)");
        commands.setPrefWidth(300);
        commands.setPrefHeight(80);

        // Buttons
        Button restartBtn = new Button("restart");
        restartBtn.setId("restart");
        Button pauseBtn = new Button("save");
        pauseBtn.setId("pause");
        Button resumeBtn=new Button("resume");
        resumeBtn.setId("resume");
        RadioButton mute = new RadioButton("mute");
        mute.setId("mute");

        HBox options = new HBox(restartBtn,pauseBtn,resumeBtn ,mute,commands);
        options.setAlignment(Pos.CENTER_RIGHT);
        options.setSpacing(5);

        VBox leftContainer = new VBox(new Label("Save your friend!"), screen, enterFiled,options);
        leftContainer.setSpacing(10);

        // Map
        Pane map = new Pane();
        map.setPrefWidth(500);
        map.setPrefHeight(400);
        map.setStyle("-fx-background-color: black");
        map.setId("map");

        // Items
        ListView collection = new ListView();
        collection.setId("items");
        collection.setPrefWidth(200);
        collection.setPrefHeight(150);
        VBox col = new VBox(new Label("Inventory"), collection);

        VBox rightContainer = new VBox(new Label("MAP"), map, col);
        rightContainer.setSpacing(10);


        HBox root = new HBox(leftContainer, rightContainer);
        root.setPadding(new Insets(10,10,10,10));
        root.setSpacing(10);


        Scene scene = new Scene(root, 1100, 600);
        return scene;
    }


}





