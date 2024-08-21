import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PostViewController {

    private static Boolean fromFrontPage;

    @FXML
    private Label postTitle;

    @FXML
    private TextArea postBody;

    @FXML
    private ListView<String> commentsList;

    @FXML
    private Button backButton;

    @FXML
    private Button replyButton;

    private int p_id;

    public void setFromFrontPage(Boolean fromFrontPage) { this.fromFrontPage = fromFrontPage; }

    // Initialize method to set up the initial state if needed
    @FXML
    private void initialize() {
        backButton.setOnAction(event -> handleBackButton());
        replyButton.setOnAction(event -> handleReplyButton());
        loadPost();
    }

    private void loadPostData() {
        PostData postData = PostData.getInstance();
        postTitle.setText(postData.getTitle());
        postBody.setText(postData.getBody());
        commentsList.getItems().clear();
        commentsList.getItems().addAll(postData.getComments());
    }

    private void loadPost() {
        commentsList.getItems().clear();
        PostData postData = PostData.getInstance();
        postTitle.setText(postData.getTitle());
        postBody.setText(postData.getBody());
        int postId = postData.getP_id();
        String query = "SELECT c.Content FROM COMMENTS c JOIN POST_COMMENTS pc ON c.Content_ID = pc.Content_ID WHERE pc.Post_ID = ?";
        try (PreparedStatement statement = Main.getConnection().prepareStatement(query)) {
            statement.setInt(1, postId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                commentsList.getItems().add(rs.getString("Content"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading comments: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButton() {
        if (fromFrontPage) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("frontPage.fxml"));
                Parent root = loader.load();

                // Get the current stage and set the scene to the post view
                Stage stage = (Stage) postBody.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.err.println("Failed to load frontpage. " + e.getMessage());
            }
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("communityView.fxml"));
                Parent root = loader.load();

                // Get the current stage and set the scene to the post view
                Stage stage = (Stage) postBody.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.err.println("Failed to load community view. " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleReplyButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createComment.fxml"));
            Parent root = loader.load();

            CreateCommentController createCommentController = loader.getController();


            // Get the current stage and set the scene to the post view
            Stage stage = (Stage) postTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load create comment. " + e.getMessage());
        }
    }

    // Method to set post details (this can be called from the front page controller)
//    public void setPostDetails(String title, String body, int p_id, List<String> comments) {
//        PostData postData = PostData.getInstance();
//        postData.setTitle(title);
//        postData.setBody(body);
//        postData.setP_id(p_id);
//        postData.setComments(comments);
//    }
}
