package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;

import client.ClientUI;
import common.Alerts;
import common.ClientServerMessage;
import common.GetInstance;
import common.Operation;
import common.Order;
import common.OrderChecker;
import common.Traveler;
import common.WaitingList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class WaitingListController implements Initializable {

	@FXML
	private AnchorPane RePane;

	@FXML
	private Button btnSubmit;

	@FXML
	private Button btnWaiting;

    @FXML
    private Label Timetxt;
    
	@FXML
	private JFXDatePicker txtDate;

	@FXML
	private JFXComboBox<String> TimeComboBox;
	
    @FXML
    private JFXListView<String> DatesToPick;
    
    @FXML
    private AnchorPane loadingPane;
	
    private Order order;
	private Traveler traveler; // For email,msgs usage
	private static boolean isNewOrder;
	private static ArrayList<Object> anotherDates = new ArrayList<>();
	private static int setDateFromWaitList = 0;

	/**
     * Getter for the flag indicating whether to set the date from the wait list.
     * @return 1 if the date should be set from the wait list, 0 otherwise.
     */
	public static int getSetDateFromWaitList() {
		return setDateFromWaitList;
	}

	public static void setSetDateFromWaitList(int setDateFromWaitList) {
		WaitingListController.setDateFromWaitList = setDateFromWaitList;
	}

	/**
     * Getter for the list of alternative dates.
     * @return The list of alternative dates.
     */
	public static ArrayList<Object> getAnotherDates() {
		return anotherDates;
	}

	public static void setAnotherDates(ArrayList<Object> anotherDates) {
		WaitingListController.anotherDates = anotherDates;
	}
	
	/**
     * Initializes the waiting list view.
     */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Hide other elements initially
	    DatesToPick.setVisible(false);
	    // Show loading pane
	    loadingPane.setVisible(true);
	    Task<Boolean> task = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				getAlternativeDates();
				return true;
			}
		};

		new Thread(task).start();

		task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				// Hide loading pane
	            loadingPane.setVisible(false);
	            // Show other elements
	            DatesToPick.setVisible(true);			}
		});

	}

	/**
     * Handles the action event for entering the waiting list.
     * @param event The action event.
     * @throws IOException If an I/O error occurs.
     */
	@FXML
	void EnterWaitingList(ActionEvent event) throws IOException {

		ClientServerMessage<?> OrderAttempt = new ClientServerMessage<>(order, Operation.POST_TRAVLER_ORDER);
		ClientUI.clientControllerInstance.sendMessageToServer(OrderAttempt);
	    ClientServerMessage<?> isNewOrderMsg = ClientUI.clientControllerInstance.getData();
    	isNewOrder = (Boolean) isNewOrderMsg.getFlag();
    	
		if (isNewOrder) { //Checking if the traveler has an order already
//			ClientServerMessage<?> TravelerID = new ClientServerMessage<>(traveler.getId(), Operation.GET_RECENT_ID_TRAVELER);
//			ClientUI.clientControllerInstance.sendMessageToServer(TravelerID);
//		    ClientServerMessage<?> TravelerMsg = ClientUI.clientControllerInstance.getData();
//	    	recentOrder = (Order) TravelerMsg.getDataTransfered();

		//	if (recentOrder != null) {
			// מה שבהערות למעלה זה עבור שליחת הודעות ואימיילים
			String visitDate = order.getDate().toString();
			String visitTime = order.getVisitTime().toString();
			String parkName = order.getParkName();
		    Integer waitingId = 1;

			ClientServerMessage<?> getLast = new ClientServerMessage<>(null, Operation.GET_LAST_WAITINGLIST);
			ClientUI.clientControllerInstance.sendMessageToServer(getLast);
			ClientServerMessage<?> waitingIdMsg = ClientUI.clientControllerInstance.getData();
			waitingId = (Integer) waitingIdMsg.getDataTransfered();
			if (waitingId != null) {
				waitingId++;
			}
			
			ClientServerMessage<?> findPlace = new ClientServerMessage<>(new ArrayList<String>
			(Arrays.asList(parkName, visitDate, visitTime)), Operation.FIND_PLACE_IN_WAITING_LIST);
			ClientUI.clientControllerInstance.sendMessageToServer(findPlace);
		    ClientServerMessage<?> placeMsg = ClientUI.clientControllerInstance.getData();
		    Integer rightPlace = (Integer) placeMsg.getDataTransfered();

	    	
			WaitingList waiting = new WaitingList(order.getOrderId(), order.getVisitorId(),
					order.getParkNumber(), order.getAmountOfVisitors(), order.getPrice(),
					order.getVisitorEmail(), order.getDate(), order.getVisitTime(), order.getOrderStatus(),
					order.getTypeOfOrder(), order.getTelephoneNumber(), order.getParkName(), waitingId, rightPlace);

			ClientServerMessage<?> waitingAttempt = new ClientServerMessage<>(waiting, Operation.POST_NEW_WAITING_LIST);
			ClientUI.clientControllerInstance.sendMessageToServer(waitingAttempt);
		    ClientServerMessage<?> isNewWaitingMsg = ClientUI.clientControllerInstance.getData();
	    	Boolean isNewWaiting = (Boolean) isNewWaitingMsg.getFlag();
			
	    	if(isNewWaiting) {
	        	new Alerts(AlertType.CONFIRMATION, "Waiting List", "Successfully entered waiting list",
	        			"You're successfully entered waiting list ").showAndWait();
	        	Stage stage = (Stage) btnWaiting.getScene().getWindow();
	    		Parent root = FXMLLoader.load(getClass().getResource("/gui/HomePageFrame.fxml")); // תחליטו אתם לאן
	    		stage.setScene(new Scene(root));
	    	}

			}
		}

	/**
     * Retrieves alternative dates for the waiting list.
     */
	private void getAlternativeDates() {

		ArrayList<String> availableDatesList = getAvailableDatesList();
		DatesToPick.getItems().addAll(availableDatesList);

	}

	/**
     * Gets the list of available alternative dates.
     * @return The list of available alternative dates.
     */
	private ArrayList<String> getAvailableDatesList() {
		Order tempOrder = new Order(order.getOrderId(), order.getVisitorId(), order.getParkNumber(), order.getAmountOfVisitors(),
				order.getPrice(),order.getVisitorEmail(), order.getDate(), order.getVisitTime(), order.getOrderStatus(),
				order.getTypeOfOrder(), order.getTelephoneNumber(), order.getParkName());
		LocalDate originalDate = order.getDate();


		LocalTime currentTime = LocalTime.parse("08:00");
		LocalDate currentDate = originalDate;
		int counter = 0;
		ArrayList<String> availableDatesList = new ArrayList<String>();
		while (counter != 10) {
			tempOrder.setDate(currentDate);
			tempOrder.setVisitTime(currentTime);;
			
			if (OrderChecker.isDateAvailable(tempOrder)) {
				availableDatesList.add(tempOrder.getDate().toString() + ", " + tempOrder.getVisitTime().toString());
				counter++;
			}

			LocalTime hour = currentTime.plusHours(1);

			if (hour.isAfter(LocalTime.of(17, 0))) {
				currentTime = LocalTime.parse("08:00");

				// Date after adding the days to the given date
				currentDate = currentDate.plusDays(1);
			} else {
				currentTime = currentTime.plusHours(1);
			}

		}

		return availableDatesList;
	}
	
	/**
     * Submits the selected date and time from the waiting list.
     */
	@FXML
	private void SubmitOrderbtn() {
		WaitingListController.setSetDateFromWaitList(1);
		String newDateAndTime = DatesToPick.getSelectionModel().getSelectedItem();
		String newDate = newDateAndTime.split(",")[0];
		String newTime = newDateAndTime.split(",")[1].trim();
		anotherDates.add(newDate);
		anotherDates.add(newTime);
		OrderAVisitController Ordercon = GetInstance.getInstance().getOrderC();
		Ordercon.getPaneOrder().setDisable(false);
		Ordercon.initialize(Ordercon.getLocation(), Ordercon.getResources());
		Stage stage2 = (Stage) btnSubmit.getScene().getWindow();
		stage2.close();

	}

	/**
     * Sets the time for the reschedule order in OrderVisit screen.
     */
	public void setTimeLabel(String DateAndTime) {
		this.Timetxt.setText(DateAndTime);
	}

	/**
     * Sets the order for the waiting list.
     * @param order The order to set.
     */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
     * Sets the traveler for the waiting list.
     * @param traveler The traveler to set.
     */
	public void setTraveler(Traveler traveler) {
		this.traveler = traveler;
	}



}







