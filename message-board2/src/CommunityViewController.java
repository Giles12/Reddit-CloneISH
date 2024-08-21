import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class CommunityViewController {


    @FXML
    private Label communityName;

    @FXML
    private Button backButton;

    @FXML
    private ListView<Node> communityPosts;


    @FXML
    private void initialize() {
        PostViewController postViewController = new PostViewController();
        postViewController.setFromFrontPage(false);

        CommunityData communityData = CommunityData.getInstance();
        String community = communityData.getName();
        communityName.setText(community);

        String query = "SELECT Title, Post_ID, Content FROM POST " +
                "WHERE Post_ID IN " +
                "(SELECT Post_ID FROM COMMUNITY_POSTS WHERE Community_Name = ?)";

        try (PreparedStatement commListStmt = Main.getConnection().prepareStatement(query)) {
            commListStmt.setString(1, community);
            ResultSet rs = commListStmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("Title");
                int postId = rs.getInt("Post_ID");
                String content = rs.getString("Content");

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("post.fxml"));
                Parent card = loader.load();
                PostController postController = loader.getController();
                postController.setContents(community, title, postId, content);
                communityPosts.getItems().add(card);

                System.out.println("Post loaded: " + title + ", " + postId); // Debugging log
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch posts: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to load post view: " + e.getMessage());
        }
    }



    @FXML
    private void goToCommunityPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("communityPage.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the scene to the post view
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.printf("Could not load community page. %s\n", e.getMessage());
        }
    }
}
