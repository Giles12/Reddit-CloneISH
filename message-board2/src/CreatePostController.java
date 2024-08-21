import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;


public class CreatePostController  {
    @FXML
    private TextField postTitle;

    @FXML
    private TextArea postBody;

    @FXML
    private Button postSubmitBttn;

    @FXML
    private Button cancelBttn;

    @FXML
    private ListView<String> communitiesList;

    private String community = "";

    @FXML
    public void initialize() {
        loadCommunities();
        communitiesList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            community = communitiesList.getSelectionModel().getSelectedItem();

        });
    }

    private void loadCommunities() {
        communitiesList.getItems().clear();
        String query = "SELECT Community_Name FROM COMMUNITY WHERE Community_Name IN (SELECT Community_Name FROM COMMUNITY_MEMBERS WHERE User_Name = ?)";
        try (PreparedStatement statement = Main.getConnection().prepareStatement(query)) {

            statement.setString(1, UserSession.getInstance().getUserName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                communitiesList.getItems().add(rs.getString("Community_Name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading communities: " + e.getMessage());
        }
    }

    @FXML
    private void submitPost() {

        if (postTitle.getText().isBlank() || postTitle.getText().isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR, "Title cannot be blank");
            a.showAndWait();
            return;
        }

        if (community.isEmpty() || community.isBlank()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "You must be part of a community to post");

            a.showAndWait();
            return;     
        }
        handleSubmitPost();
        postTitle.setText("");
        postBody.setText("");
    }

    @FXML
    private void handleReturn() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontPage.fxml")));

            Stage stage = (Stage)postBody.getScene().getWindow();
            stage.getScene().setRoot(root);


        } catch (IOException e) {
            System.err.println("Could not load community page: " + e.getMessage());
        }
    }

    private void handleSubmitPost() {
        String userName = UserSession.getInstance().getUserName();

        String action = "INSERT INTO POST (Title, Content) VALUES (?,?)";
        String createPostStm = "INSERT INTO CREATE_POST (User_Name, Post_ID) VALUES (?, ?)";
        String communityPostStm = "INSERT INTO COMMUNITY_POSTS (Post_ID, Community_Name) VALUES (?, ?)";
        try {
            Main.getConnection().setAutoCommit(false);
            PreparedStatement addPost = Main.getConnection().prepareStatement(action, Statement.RETURN_GENERATED_KEYS);
            addPost.setString(1, postTitle.getText());
            addPost.setString(2, postBody.getText());
            addPost.executeUpdate();

            ResultSet generatedKeys = addPost.getGeneratedKeys();
            if (generatedKeys.next()) {
                int p_id = generatedKeys.getInt(1);
                PreparedStatement addCreatePost = Main.getConnection().prepareStatement(createPostStm);
                addCreatePost.setString(1, userName);
                addCreatePost.setInt(2, p_id);
                addCreatePost.executeUpdate();

                PreparedStatement addCommPost = Main.getConnection().prepareStatement(communityPostStm);
                addCommPost.setInt(1,p_id);
                addCommPost.setString(2, community);
                addCommPost.executeUpdate();


            } else {
                throw new SQLException("Could not generate Post ID");
            }

            Main.getConnection().commit();
            Main.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            System.err.println("Could not add post: " + e.getMessage());
            try {
                Main.getConnection().rollback();
            } catch (SQLException ex) {
                System.err.println("Could not rollback: " + e.getMessage());
            }
        }
    }
}
