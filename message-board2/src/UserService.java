import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UserService {

    private UserSession userSession;

    public UserService(UserSession userSession) {
        this.userSession = userSession;
    }

    // Check if the user is a member of the community
    public boolean isUserMemberOfCommunity(String community) {
        Set<String> userCommunities = userSession.getUserCommunities();
        return userCommunities.contains(community);
    }

    // User joins a community
    public void joinCommunity(String community) {
        String username = userSession.getUserName();
        try {
            // Add to the COMMUNITY_MEMBERS table
            String query = "INSERT INTO COMMUNITY_MEMBERS (User_Name, Community_Name) VALUES (?, ?)";
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, community);
            preparedStatement.executeUpdate();

            // Update the user session
            Set<String> userCommunities = userSession.getUserCommunities();
            userCommunities.add(community);
            userSession.setUserCommunities(userCommunities);
        } catch (SQLException e) {
            System.err.println("Failed to join " + community + ". " + e.getMessage());
        }
    }

    // User leaves a community
    public void leaveCommunity(String community) {
        String username = userSession.getUserName();
        try {
            // Remove from the COMMUNITY_MEMBERS table
            String query = "DELETE FROM COMMUNITY_MEMBERS WHERE User_Name = ? AND Community_Name = ?";
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, community);
            preparedStatement.executeUpdate();

            // Update the user session
            Set<String> userCommunities = userSession.getUserCommunities();
            userCommunities.remove(community);
            userSession.setUserCommunities(userCommunities);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Other user-related methods
    public Set<String> loadUserCommunities(String username) {
        Set<String> communities = new HashSet<>();
        try {
            // Fetch user communities from the COMMUNITY_MEMBERS and COMMUNITY_MODERATOR tables
            String query = "SELECT Community_Name FROM COMMUNITY_MEMBERS WHERE User_Name = ? " +
                    "UNION " +
                    "SELECT Community_Name FROM COMMUNITY_MODERATOR WHERE User_Name = ?";
            PreparedStatement preparedStatement = Main.getConnection().prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                communities.add(resultSet.getString("Community_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return communities;
    }
}
