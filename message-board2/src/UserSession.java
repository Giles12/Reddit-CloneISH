import java.sql.*;
import java.time.Instant;
import java.util.Set;

public class UserSession {

    private static UserSession instance;

    private String userName;
    private int sid;
    private Set<String> userCommunities;
    private UserService userService;

    private UserSession(String userName) throws SQLException {
        this.userName = userName;
        this.userService = new UserService(this);
        this.userCommunities = userService.loadUserCommunities(userName);
        createSession();
    }

    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User session has not been initialized");
        }
        return instance;
    }


    public Set<String> getUserCommunities() {
        return userCommunities;
    }

    public void setUserCommunities(Set<String> userCommunities) {
        this.userCommunities = userCommunities;
        // Additional logic to persist this change in the database, if needed
    }

    public static void createInstance(String userName) throws SQLException {
        if (instance == null) {
            instance = new UserSession(userName);
        } else {
            throw new IllegalStateException("User session is already initialized");
        }
    }

    public static void clearSession() throws SQLException {
        if (instance != null) {
            instance.endSession();
            instance = null;
        }
    }

    public String getUserName() {
        return userName;
    }

    private void createSession() throws SQLException {
        String query = "INSERT INTO MB_SESSION (User_Name, s_time) VALUES (?, ?)";
        PreparedStatement sessionStatement = Main.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        sessionStatement.setString(1, userName);
        sessionStatement.setTimestamp(2, currentTimestamp);
        sessionStatement.executeUpdate();

        // Retrieve the generated SID
        ResultSet generatedKeys = sessionStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            sid = generatedKeys.getInt(1);
            // Print user, timestamp, and SID
            System.out.println("User logged in: " + userName);
            System.out.println("Login timestamp: " + currentTimestamp);
            System.out.println("Session ID: " + sid);
        } else {
            System.out.println("User logged in: " + userName);
            System.out.println("Login timestamp: " + currentTimestamp);
            System.out.println("Session ID could not be retrieved.");
        }
    }

    private void endSession() throws SQLException {
        String query = "UPDATE MB_SESSION SET e_time = ? WHERE User_Name = ? AND SID = ? AND e_time IS NULL";
        PreparedStatement endSessionStatement = Main.getConnection().prepareStatement(query);
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        endSessionStatement.setTimestamp(1, currentTimestamp);
        endSessionStatement.setString(2, userName);
        endSessionStatement.setInt(3, sid);
        int rowsAffected = endSessionStatement.executeUpdate();

        if (rowsAffected > 0) {
            // Print user and timestamp to the console
            System.out.println("User logged out: " + userName);
            System.out.println("Logout timestamp: " + currentTimestamp);
            System.out.println("Session ID: " + sid);
        } else {
            System.out.println("No active session found for user: " + userName + " with Session ID: " + sid);
        }
    }
}