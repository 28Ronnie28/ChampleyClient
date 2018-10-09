package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class HomeController {

    @FXML private Button suplliersBtn;
    @FXML private Button productsBtn;
    @FXML private Button qoutationsBtn;
    @FXML private Button invoicesBtn;
    @FXML private Button documentsBtn;

    public void homeSuppliersButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SuppliersPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*suplliersBtn.setStyle("-fx-background-radius: 5em;" +
                "-fx-min-width: 120px;" +
                "-fx-min-height: 120px;" +
                "-fx-max-width: 120px;" +
                "-fx-max-height: 120px;" +
                "-fx-background-color: -fx-body-color;" +
                "-fx-background-size: auto 100%;" +
                "-fx-border-color: transparent;" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-background-color: transparent;" +
                "-fx-font-family:\"Segoe UI\", Helvetica, Arial, sans-serif;" +
                "-fx-font-size: 1em; " +
                "-fx-text-fill: #828282;");*/
        suplliersBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("Suppliers.png"))));
    }

    public void homeProductsButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProductsPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void homeQuotationsButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("QuotationsPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void homeInvoicesButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("InvoicesPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void homeDocumentsButtonClick(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("DocumentPane.fxml"));
        try {
            Main.setStage(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
