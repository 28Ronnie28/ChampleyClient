package main;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import models.DataFile;

import java.util.List;
import java.util.Optional;

public class UserNotification {


    public static void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showConfirmationMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showMessage(Window parent, String heading, String message) {
        new CustomDialog(parent, heading, message, new JFXButton("Ok"));
    }

    public static String getText(String title, String message) {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static Boolean confirmationDialog(Window parent, String heading, String body) {
        CustomDialog customDialog = new CustomDialog(parent, heading, body, new JFXButton("Yes"), new JFXButton("Cancel"));
        return customDialog.showDialog() == 1;
    }

    public static int showLecturerContactMethod(Window parent) {
        CustomDialog customDialog = new CustomDialog(parent, "Contact Lecturer", "Do you want to contact lecturer by email or directly?", new JFXButton("Email"), new JFXButton("Direct Message"), new JFXButton("Cancel"));
        return customDialog.showDialog();
    }

}
