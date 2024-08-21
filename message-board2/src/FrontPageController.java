import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class FrontPageController {

    @FXML
    private Button findCommBttn;

    @FXML
    private Button createPostBttn;

    @FXML
    private Button logoutButton;

    @FXML
    private ListView<Node> frontPagePosts;


    @FXML
    private void initialize() {
        PostViewController postViewController = new PostViewController();
        postViewController.setFromFrontPage(true);

        String name = UserSession.getInstance().getUserName();

        String query = "SELECT Title, Post_ID, Content FROM POST WHERE POST_ID IN " +
                "(SELECT P.Post_ID FROM COMMUNITY_POSTS P WHERE P.Community_Name IN " +
                "(SELECT Community_Name From USER_FRONT_PAGE FP WHERE FP.User_Name = ?))";

        try {
            PreparedStatement fpListStmt = Main.getConnection().prepareStatement(query);
            fpListStmt.setString(1, name);
            ResultSet rs = fpListStmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString(1);
                int p_id = rs.getInt(2);
                String content = rs.getString(3);
                String cPost = "SELECT Community_Name FROM COMMUNITY_POSTS WHERE Post_ID = ?";
                PreparedStatement cPostStmnt = Main.getConnection().prepareStatement(cPost);
                cPostStmnt.setInt(1, p_id);
                ResultSet rs1 = cPostStmnt.executeQuery();
                if (rs1.next()){
                    String cName = rs1.getString(1);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("post.fxml"));

                    Parent card = loader.load();
                    PostController pController = loader.getController();
                    pController.setContents(cName, title, p_id, content);
                    frontPagePosts.getItems().add(card);
                } else {
                    throw new SQLException("Could not find community name.");
                }

                // add to list view

            }

        } catch (SQLException | IOException e) {
            System.err.println("Failed to fetch posts: " + e.getMessage());
        }
    }


    @FXML
    private void handleLogout() {
        try {
            UserSession.clearSession();
            // Redirect to the login page
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("firstpage.fxml")));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException | SQLException e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    @FXML
    private void goToCommunityPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("communityPage.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the scene to the post view
            Stage stage = (Stage) findCommBttn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.printf("Could not load community page. %s\n", e.getMessage());
        }
    }

    @FXML
    private void goToCreatePostView() {
        try {
            // Load the FXML file using the same method as Main.java
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("createPost.fxml")));
            Stage stage = (Stage) createPostBttn.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            System.err.printf("Could not load community page. %s\n", e.getMessage());
        }
    }
}
