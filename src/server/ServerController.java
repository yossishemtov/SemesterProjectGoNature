package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import DB.MySqlConnector;
import client.NavigationManager;
import common.Alerts;
import entities.ClientConnectionStatus;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ServerController {

	BackEndServer sv;
//	private Map<String, ClientConnectionStatus> statusMap = new HashMap<>();

	@FXML
	private JFXTextField PortTxt;

	@FXML
	private JFXTextField DBUserNameTxt;

	@FXML
	private JFXPasswordField PasswordTxt;

	@FXML
	private VBox TableViewContainer;

	@FXML
	private TableView<ClientConnectionStatus> connStatusTable;

	@FXML
	private TableColumn<ClientConnectionStatus, String> IPCol;

	@FXML
	private TableColumn<ClientConnectionStatus, String> HostCol;

	@FXML
	private TableColumn<ClientConnectionStatus, String> StatusCol;

	@FXML
	private TableColumn<ClientConnectionStatus, String> StTimeCol;

	@FXML
	private TextArea logTextArea;

	@FXML
	private JFXButton startserverBtn;

	@FXML
	private JFXButton StopserverBtn;

	@FXML
	private ImageView logoImage;

	@FXML
	private Circle circleStatus;

	@FXML
	void serveStopAction(ActionEvent event) {
		closeConnection();
	}

	public void updateConnectionStatusTable(Collection<ClientConnectionStatus> statuses) {
		connStatusTable.getItems().setAll(statuses);
		// Refresh the table to ensure UI is up-to-date
		connStatusTable.refresh();
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader serverGui = new FXMLLoader(getClass().getResource("ServerGUI.fxml"));
			Parent parent = serverGui.load();
			ServerController control = serverGui.getController();

			Scene scene = new Scene(parent);

//			Image image = new Image("../common/images/1.png");
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("/common/images/1.png"));
			primaryStage.setOnCloseRequest(e -> control.closeConnection());
			primaryStage.setTitle("GoNature Server");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getPort() {
		return PortTxt.getText();
	}

	private String getuserNameOfDB() {
		return DBUserNameTxt.getText();
	}

	private String getPasswordOfDB() {
		return PasswordTxt.getText();
	}

	private boolean isVaiildLogin() {
		Integer port;
		String dbUserName;
		String dbPass;
		port = Integer.parseInt(getPort());

		dbUserName = getuserNameOfDB();
		dbPass = getPasswordOfDB();
		if (port == null || port < 0 || port > 65535 || dbPass.equals("") || dbUserName.equals("")) {
			Alerts erorAlert = new Alerts(Alerts.AlertType.ERROR, "Invalid Input", "Warning",
					"The value entered is invalid. Please try again.");
			erorAlert.showAndWait();
			return false;
		}
		return true;

	}

	// mouse click on start server button
	@FXML
	void serveStartAction(ActionEvent event) {
		Integer port;
		String dbUserName;
		String dbPass;

		port = Integer.parseInt(getPort()); // Set port to 555
		dbUserName = this.getuserNameOfDB();
		dbPass = this.getPasswordOfDB();

		if (isVaiildLogin()) {
			try {

				sv = new BackEndServer(port, this, dbUserName, dbPass);

				// Start server
				sv.listen();
				circleStatus.setFill(Color.GREEN); // Assume circleStatus is a GUI element indicating server status
				logTextArea.setText("Server started listening."); // logTextArea for logging text to GUI
				System.out.println("Server started listening.");

			} catch (java.net.BindException b) {
				// This block catches the BindException specifically
				 Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR", "",
						 "Error: Port " + port + " is already in use. Please choose a different port.");
					somethingWentWrong.showAndWait();				logTextArea.setText("Error: Port " + port + " is already in use. Please choose a different port.");
				System.err.println("Error: Port " + port + " is already in use. Please choose a different port.");
			} catch (IOException e) {
				// Catch other IOExceptions here
				logTextArea.setText("Server failed to start.");
				System.err.println("Server failed to start.");
				e.printStackTrace();
			}
		}
	}

	@FXML
	protected void initialize() {
		PortTxt.setText("5555");
		DBUserNameTxt.setText("root"); // Assuming you might want to set the username to 'root' as well

		//redirectSystemStreams(); // Redirect System.out and System.err

		HostCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getHost()));
		IPCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getIp()));
		StatusCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStatus()));
		StTimeCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getStartTime()));
		PortTxt.setText("5555");
		DBUserNameTxt.setText("root");
		PasswordTxt.setText("root");
		ObservableList<ClientConnectionStatus> connectionStatuses = FXCollections.observableArrayList();
		connStatusTable.setItems(connectionStatuses);

	}

	// Method to redirect output streams to the TextArea
	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				appendText(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				appendText(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	// Method to append text to the TextArea in a thread-safe manner
	private void appendText(String str) {
		Platform.runLater(() -> logTextArea.appendText(str));
	}

	public void closeConnection() {
		try {
			if (sv != null && sv.isListening())
				circleStatus.setFill(Color.RED);
			sv.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

}
