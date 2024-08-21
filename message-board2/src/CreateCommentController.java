import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class CreateCommentController {

    @FXML
    private TextField commentField;

    @FXML
    private Button commentSubmitBttn;


    @FXML
    private void submitComment() {
        if (commentField.getText().isBlank() || commentField.getText().isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR, "Comment cannot be blank");
            a.showAndWait();
            return;
        }


        handleCreateComment();
        commentField.setText("");
    }

    @FXML
    private void handleCreateComment() {
        String userName = UserSession.getInstance().getUserName();
        PostData postData = PostData.getInstance();
//        postData.getComments().add(commentField.getText());

        String action = "INSERT INTO COMMENTS (Content) VALUES (?)";
        String createCommentStm = "INSERT INTO CREATE_COMMENT (User_Name, Content_ID) VALUES (?, ?)";
        String postCommentStm = "INSERT INTO POST_COMMENTS (Post_ID, Content_ID) VALUES (?, ?)";
        try {
            Main.getConnection().setAutoCommit(false);
            PreparedStatement addComment = Main.getConnection().prepareStatement(action, Statement.RETURN_GENERATED_KEYS);
            addComment.setString(1, commentField.getText());
            addComment.executeUpdate();

            ResultSet generatedKeys = addComment.getGeneratedKeys();
            if (generatedKeys.next()) {
                int c_id = generatedKeys.getInt(1);
                PreparedStatement addCreateComment = Main.getConnection().prepareStatement(createCommentStm);
                addCreateComment.setString(1, userName);
                addCreateComment.setInt(2, c_id);
                addCreateComment.executeUpdate();

                PreparedStatement addPostComm = Main.getConnection().prepareStatement(postCommentStm);
                addPostComm.setInt(1,postData.getP_id());
                addPostComm.setInt(2, c_id);
                addPostComm.executeUpdate();


            } else {
                throw new SQLException("Could not generate Comment ID");
            }

            Main.getConnection().commit();
            Main.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            System.err.println("Could not add comment: " + e.getMessage());
            try {
                Main.getConnection().rollback();
            } catch (SQLException ex) {
                System.err.println("Could not rollback: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("postView.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the scene to the post view
            Stage stage = (Stage) commentField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();


        } catch (IOException e) {
            System.err.println("Could not load post: " + e.getMessage());
        }
    }
}
