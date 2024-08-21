import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class CreateCommunityController {

    @FXML
    private TextField communityNameField;

    @FXML
    private void handleCreateCommunity() {
        String communityName = communityNameField.getText();
        if (communityName.isEmpty()) {
            showAlert("Error", "Community name cannot be empty", Alert.AlertType.ERROR);
            return;
        }

        // do not create a local variable for the connection
        try  {

            // Add community to COMMUNITY table
            String insertCommunity = "INSERT INTO COMMUNITY (Community_Name) VALUES (?)";
            PreparedStatement psCommunity = Main.getConnection().prepareStatement(insertCommunity);
            psCommunity.setString(1, communityName);
            psCommunity.executeUpdate();


            // Add user as moderator to COMMUNITY_MODERATOR
            String insertModerator = "INSERT INTO COMMUNITY_MODERATOR (User_Name, Community_Name) VALUES (?, ?)";
            PreparedStatement psModerator = Main.getConnection().prepareStatement(insertModerator);
            psModerator.setString(1, UserSession.getInstance().getUserName());  // Assuming UserSession manages logged in user info
            psModerator.setString(2, communityName);
            psModerator.executeUpdate();


            showAlert("Success", "Community created and you are now a moderator!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage(), Alert.AlertType.ERROR);
            try {
                Main.getConnection().rollback();
            } catch (SQLException ex) {
                System.err.println("Could not rollback. " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleReturn() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("communityPage.fxml")));

            Stage stage = (Stage)communityNameField.getScene().getWindow();
//            stage.setScene(new Scene(root));
            stage.getScene().setRoot(root);


        } catch (IOException e) {
            System.err.println("Could not load community page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
