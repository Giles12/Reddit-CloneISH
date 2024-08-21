import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class PostController {

    @FXML
    private Button upvoteButton;

    @FXML
    private Button downvoteButton;

    @FXML
    private Label votesLabel;

    @FXML
    private Text communityName;

    @FXML
    private Text postTitle;

    @FXML
    private Button viewButton;

    private int p_id;

    private String content;

    @FXML
    public void handleUpvote() {
        updateVote(p_id, UserSession.getInstance().getUserName(), 1);
    }

    @FXML
    public void handleDownvote() {
        updateVote(p_id, UserSession.getInstance().getUserName(), -1);
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> updateVotesLabel(p_id));
    }

    private void updateVote(int postId, String userName, int voteType) {
        String insertVoteQuery = "INSERT INTO POST_VOTES (User_Name, VoteType, Post_ID) VALUES (?, ?, ?)";
        String updateVoteQuery = "UPDATE POST_VOTES SET VoteType = VoteType + ? WHERE User_Name = ? AND Post_ID = ?";

        try {
            // Check if user already voted on this post
            String checkVoteQuery = "SELECT VoteType FROM POST_VOTES WHERE User_Name = ? AND Post_ID = ?";
            PreparedStatement checkStatement = Main.getConnection().prepareStatement(checkVoteQuery);
            checkStatement.setString(1, userName);
            checkStatement.setInt(2, postId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // User has already voted, update their vote
                PreparedStatement updateStatement = Main.getConnection().prepareStatement(updateVoteQuery);
                updateStatement.setInt(1, voteType);
                updateStatement.setString(2, userName);
                updateStatement.setInt(3, postId);
                updateStatement.executeUpdate();
            } else {
                // User has not voted, insert new vote
                PreparedStatement insertStatement = Main.getConnection().prepareStatement(insertVoteQuery);
                insertStatement.setString(1, userName);
                insertStatement.setInt(2, voteType);
                insertStatement.setInt(3, postId);
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateVotesLabel(postId);
    }

    private void updateVotesLabel(int postId) {
        int netVotes = calculateNetVotes(postId);
        votesLabel.setText(String.valueOf(netVotes));
//        if (votesLabel != null) {
//            votesLabel.setText(String.valueOf(netVotes));
//        } else {
//            System.err.println("votesLabel is not initialized properly.");
//        }
    }

    private int calculateNetVotes(int postId) {
        String query = "SELECT SUM(VoteType) AS NetVotes FROM POST_VOTES WHERE Post_ID = ?";
        try (PreparedStatement preparedStatement = Main.getConnection().prepareStatement(query)) {

            preparedStatement.setInt(1, postId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println(resultSet.getInt("NetVotes"));
                return resultSet.getInt("NetVotes");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setContents(String cName, String title, int p_id, String content) {
        this.p_id = p_id;
        this.content = content;
        postTitle.setText(title);
        this.communityName.setText(cName);

    }

    @FXML
    private void handlePostView() throws SQLException {
        List<String> comments = fetchComments(p_id);
        try {
            // Get the PostViewController and set the post details
            String title = postTitle.getText();
            String body = content; // Use the content from the setContents method

            PostData postData = PostData.getInstance();
            postData.setTitle(title);
            postData.setBody(body);
            postData.setP_id(p_id);
            postData.setComments(comments);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("postView.fxml"));
            Parent root = loader.load();

            System.out.println(p_id);

            // Get the current stage and set the scene to the post view
            Stage stage = (Stage) postTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error during loading post view: " + e.getMessage());
        }
    }

    private List<String> fetchComments(int postId) throws SQLException {
        List<String> comments = new ArrayList<>();

        String query = "SELECT c.Content FROM COMMENTS c " +
                "JOIN POST_COMMENTS pc ON c.Content_ID = pc.Content_ID " +
                "WHERE pc.Post_ID = ?";

        try (PreparedStatement statement = Main.getConnection().prepareStatement(query)) {
            statement.setInt(1, postId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                comments.add(resultSet.getString("Content"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching comments:" + e.getMessage());
        }

        return comments;
    }

}
