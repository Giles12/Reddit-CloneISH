import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static java.lang.System.exit;

public class Main extends Application {
    private Stage window;
    private static Connection connection = null;
    public static void main(String[] args) {
       
        // load the driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cne) {
            System.err.printf("Error could not find %s\n", cne.getMessage());
            exit(1);
        }

        // create the connection
        String host = "localhost";
        String dbName = "messageBoard";
        int port = 3306;

        String mysqlURL = "jdbc:mysql://" + host +
                ":" + port + "/" + dbName;


        // never hardcode credentials
        String uName = "root";
        String passwd = "Zk60yfk4";

        try {
            connection = DriverManager.getConnection(mysqlURL, uName, passwd);
            System.out.println("Connected to database");

        } catch (SQLException e) {
            System.err.println("Error connection could not be established. " + e.getMessage());
        }

        launch(args);
    }

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        URL firstPageUrl = getClass().getResource("firstpage.fxml");
        if (firstPageUrl == null) {
            System.err.println("Could not find firstpage.fxml");
        } else {
            System.out.println("Path to firstpage.fxml: " + firstPageUrl);
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("firstpage.fxml")));
        window = stage;
        window.setTitle("Message Board");
        window.setScene(new Scene(root));
        window.setMinWidth(600.0);
        window.setMinHeight(400.0);
        window.setResizable(false);
        window.centerOnScreen();
        window.show();


    }

    @Override
    public void stop() throws Exception {
        UserSession.clearSession();
        if (connection != null) {
            connection.close();
            System.out.println("Connection closed");
        }

        super.stop();
    }
}