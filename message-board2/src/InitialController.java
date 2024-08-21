import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;


public class InitialController {

    @FXML
    private Button signupButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField userNameInput;

    @FXML
    private PasswordField passwordInput;


    private void gotoFrontPage() throws IOException{

        // Load the FXML file using the same method as Main.java
        FXMLLoader loader = new FXMLLoader(getClass().getResource("frontPage.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) passwordInput.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();


    }


    @FXML
    private void signUp() {
        String userName = userNameInput.getText();
        String password = passwordInput.getText();


        if (userName.isEmpty() || password.isEmpty() || userName.isBlank() || password.isBlank()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "UserName and Password fields cannot be blank");

            a.showAndWait();
            return;
        }
        String query = "INSERT INTO MB_USER (User_Name, User_Password, Join_Date) VALUES (?, ?, ?)";
        try {


            PreparedStatement signupStatement = Main.getConnection().prepareStatement(query);
            signupStatement.setString(1, userName);
            signupStatement.setString(2, password);
            signupStatement.setDate(3, Date.valueOf(LocalDate.now()));

            signupStatement.executeUpdate();

            login();


        } catch (SQLException e) {

            if (e.getMessage().contains("Duplicate")) {

                Alert a = new Alert(Alert.AlertType.ERROR, "Duplicate Username");

                a.showAndWait();
            } else {
                System.err.printf("Could not add %s. %s\n", userName, e.getMessage());
            }

        }

    }

    @FXML
    private void login() {
        String userName = userNameInput.getText();
        String password = passwordInput.getText();


        if (userName.isEmpty() || password.isEmpty() || userName.isBlank() || password.isBlank()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "UserName and Password fields cannot be blank");

            a.showAndWait();
            return;
        }

        String query = "SELECT * FROM MB_USER WHERE User_Name=? AND User_Password=?";
        try {


            PreparedStatement loginStatement = Main.getConnection().prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            loginStatement.setString(1, userName);
            loginStatement.setString(2, password);

            ResultSet rs = loginStatement.executeQuery();

            if (!rs.next()) {

                Alert a = new Alert(Alert.AlertType.WARNING, "User: " + userName + " does not exist or incorrect password");

                a.showAndWait();

            } else {
                rs.beforeFirst();
                UserSession.createInstance(userName); // Initialize user session
//                gotoCommunityPage();
                gotoFrontPage();
                //System.out.println(rs.getMetaData().getColumnName(1));
            }


        } catch (SQLException | IOException e) {

            System.err.printf("Could not add %s. %s\n", userName, e.getMessage());


        }
    }

    @FXML
    private void gotoCommunityPage() throws IOException {
//        URL communityPageUrl = getClass().getResource("communityPage.fxml");
//        if (communityPageUrl == null) {
//            System.err.println("Could not find communityPage.fxml");
//        } else {
//            System.out.println("Path to communityPage.fxml: " + communityPageUrl);
//        }


        // Load the FXML file using the same method as Main.java
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("communityPage.fxml")));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.getScene().setRoot(root);


    }
}
