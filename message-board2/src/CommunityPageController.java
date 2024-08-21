import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommunityPageController {

    @FXML
    private ListView<String> communityList;
    @FXML
    private TextField searchBar;
    @FXML
    private HBox buttonBox;
    @FXML
    private Button joinButton;
    @FXML
    private Button leaveButton;
    @FXML
    private Button viewButton;
    @FXML
    private Button createCommunityButton;
    @FXML
    private Button backToFrontPageBttn;

    private UserService userService;

    @FXML
    private void initialize() {
        this.userService = new UserService(UserSession.getInstance());

        loadCommunities("");
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            loadCommunities(newValue);
        });
    }

    private void loadCommunities(String searchQuery) {
        communityList.getItems().clear();
        //String query = "SELECT Community_Name FROM COMMUNITY WHERE Community_Name LIKE ? AND Community_Name NOT IN (SELECT Community_Name FROM COMMUNITY_MEMBERS WHERE User_Name = ?)";
        String query = "SELECT Community_Name FROM COMMUNITY WHERE Community_Name LIKE ?";
        try (PreparedStatement statement = Main.getConnection().prepareStatement(query)) {
            statement.setString(1, "%" + searchQuery + "%");
            //statement.setString(2, UserSession.getInstance().getUserName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                communityList.getItems().add(rs.getString("Community_Name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading communities: " + e.getMessage());
        }
    }

    @FXML
    private void handleCommunityClick(MouseEvent event) {
        String selectedCommunity = communityList.getSelectionModel().getSelectedItem();
        if (selectedCommunity != null) {
            boolean isMember = userService.isUserMemberOfCommunity(selectedCommunity);
            buttonBox.setVisible(true);
            joinButton.setVisible(!isMember);
            leaveButton.setVisible(isMember);
        }
    }

    @FXML
    private void handleJoin() {
        String selectedCommunity = communityList.getSelectionModel().getSelectedItem();
        if (selectedCommunity != null) {
            userService.joinCommunity(selectedCommunity);
            handleCommunityClick(null); // Refresh button visibility
        }
    }

    @FXML
    private void handleLeave() {
        String selectedCommunity = communityList.getSelectionModel().getSelectedItem();
        if (selectedCommunity != null) {
            userService.leaveCommunity(selectedCommunity);
            handleCommunityClick(null); // Refresh button visibility
        }
    }

    @FXML
    private void handleView() {
        String selectedCommunity = communityList.getSelectionModel().getSelectedItem();
        if (selectedCommunity != null) {
            try {
                CommunityData communityData = CommunityData.getInstance();
                communityData.setName(selectedCommunity);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("communityView.fxml"));
                Parent root = loader.load();

                // Get the current stage and set the scene to the post view
                Stage stage = (Stage) buttonBox.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.err.println("Error during loading community view: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCreateCommunity() throws Exception {
        try {
            // Load the FXML file using the same method as Main.java
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("createCommunity.fxml")));
            Stage stage = (Stage) createCommunityButton.getScene().getWindow();
            stage.getScene().setRoot(root);
//            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            System.err.printf("Could not load create community page. %s\n", e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontPage.fxml")));
            Stage stage = (Stage) backToFrontPageBttn.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to load frontpage. " + e.getMessage());
        }
    }

//    private List<String> fetchPosts(String communityName) throws SQLException {
//        List<String> posts = new ArrayList<>();
//
//        String query = "SELECT Title, Post_ID, Content FROM POST " +
//                "WHERE Post_ID IN " +
//                "(SELECT Post_ID FROM COMMUNITY_POSTS WHERE Community_Name = ?)";
//
//        try (PreparedStatement statement = Main.getConnection().prepareStatement(query)) {
//            statement.setString(1, communityName);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                String title = resultSet.getString("Title");
//                int postId = resultSet.getInt("Post_ID");
//                String content = resultSet.getString("Content");
//
//                Post post = new Post(postId, title, content);
//                posts.add(post);
//            }
//        } catch (SQLException e) {
//            System.err.println("Error fetching posts: " + e.getMessage());
//        }
//
//        return posts;
//    }

}
