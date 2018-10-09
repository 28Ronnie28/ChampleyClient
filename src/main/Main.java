package main;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Scanner;

public class Main extends Application {

    public static final File APPLICATION_FOLDER = new File(System.getProperty("user.home") + "/AppData/Local/PCUniverse/Champley");
    public static final File LOCAL_CACHE = new File(APPLICATION_FOLDER.getAbsolutePath() + "/Local Cache");
    public static ConnectionHandler connectionHandler = new ConnectionHandler();
    public static Stage stage;
    private static StackPane backgroundPane;
    public VBox loginPane;
    private static Scene scene;
    public static volatile BooleanProperty uploading = new SimpleBooleanProperty(false);

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Setup tooltip delays
        //<editor-fold desc="Tooltip Delays">
        try {
            Class<?> clazz = Tooltip.class.getDeclaredClasses()[0];
            Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(new Duration(300), new Duration(5000), new Duration(300), false);
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            fieldBehavior.set(Tooltip.class, tooltipBehavior);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        //Setup background pane
        //<editor-fold desc="Background Pane">
        backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("background-pane");
        backgroundPane.setEffect(new GaussianBlur(0));
        //</editor-fold>

        //Setup login pane
        //<editor-fold desc="Login Pane">
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LoginPane.fxml"));
        loginPane = loader.load();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), loginPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        StackPane loginContentPane = new StackPane(backgroundPane, loginPane);
        scene = new Scene(loginContentPane);
        scene.getStylesheets().add("/resources/ChampleyStyle.css");
        //</editor-fold>

        //Setup stage
        //<editor-fold desc="Setup Stage">
        stage = primaryStage;
        stage.setTitle("Champley " + getBuild());
        stage.getIcons().add(new Image("/resources/NELogo.png"));
        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        //</editor-fold>

        //Setup stage close listener
        //<editor-fold desc="Stage Close Listener">
        stage.setOnCloseRequest(e -> System.exit(0));
        //</editor-fold>

    }

    public static void setStage(VBox root){
        StackPane contentPane = new StackPane(backgroundPane, root);
        scene.setRoot(contentPane);
        stage.setScene(scene);
        stage.show();
    }

    private String getBuild() {
        try {
            Scanner scn = new Scanner(new File(APPLICATION_FOLDER.getAbsolutePath() + "/Version.txt"));
            return "(Build " + scn.nextLine() + ")";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "(Build N/A)";
    }

    public class LogOut {

        public void logOut() {
            /*connectionHandler.logOut();
            usernameTextField.setText("");
            passwordField.setText("");
            loginPane.getChildren().clear();
            loginPane.getChildren().addAll(loginLogoImageView, usernameTextField, passwordField, loginButton, forgotPasswordHyperlink);
            contentPane.getChildren().clear();
            contentPane.getChildren().addAll(backgroundPane, loginPane);
            connectionHandler = new ConnectionHandler();*/
        }

    }


    public static void main(String[] args) {
        if (!LOCAL_CACHE.exists()) {
            if (!LOCAL_CACHE.mkdirs()) {
                System.exit(0);
            }
        }
        if (args.length == 1) {
            ConnectionHandler.LOCAL_ADDRESS = args[0];
        }
        launch(args);
    }
}
