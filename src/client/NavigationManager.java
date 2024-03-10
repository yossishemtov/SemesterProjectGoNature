package client;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationManager {
    private static SystemClient systemClient;

    // Initialize with SystemClient instance
    public static void initialize(SystemClient client) {
        systemClient = client;
    }

    /**
     * Opens a new page and optionally hides the current window.
     * @param nameOfPage The FXML file name of the page to be opened.
     * @param event The event that triggered the page opening (can be null if not applicable).
     * @param title The title for the new stage.
     * @param hideCurrent Determines whether the current stage should be hidden.
     * @throws IOException If an error occurs during loading the FXML.
     */
    public static void openPage(String nameOfPage, Event event, String title, boolean hideCurrent) throws IOException {
        Stage currentStage = null;
        if (event != null && event.getSource() instanceof Node) {
            currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        }
        openPage(nameOfPage, currentStage, title, hideCurrent);
    }

    /**
     * Opens a new page and optionally hides the current window.
     * @param nameOfPage The FXML file name of the page to be opened.
     * @param currentStage The current Stage that might be hidden (can be null if not applicable).
     * @param title The title for the new stage.
     * @param hideCurrent Determines whether the current stage should be hidden.
     * @throws IOException If an error occurs during loading the FXML.
     */
    public static void openPage(String nameOfPage, Stage currentStage, String title, boolean hideCurrent) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/gui/" + nameOfPage));
        Parent pane = loader.load();
        Scene scene = new Scene(pane);

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle(title);

        if (currentStage != null && hideCurrent) {
            currentStage.hide(); // Hide the current stage
        }
        newStage.show(); // Show the new stage

        // Handling the closing of the window
        newStage.setOnCloseRequest(e -> {
            // Perform any cleanup, including closing connections
            if (systemClient != null) {
                systemClient.quit();
            }
        });
    }

}
