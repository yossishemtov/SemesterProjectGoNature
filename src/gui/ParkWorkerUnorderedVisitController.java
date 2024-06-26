package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;

import client.ClientUI;
import client.InputValidation;
import client.NavigationManager;
import common.Alerts;
import common.ClientServerMessage;
import common.Operation;
import common.Order;
import common.Park;
import common.Traveler;
import common.Usermanager;
import common.Visit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;


public class ParkWorkerUnorderedVisitController implements Initializable{
	
	@FXML
    private Label allowedVisitorsLabel;

    @FXML
    private JFXTextField travelerIdField;

    @FXML
    private JFXTextField amountOfVisitorsField;

    @FXML
    private JFXTextField visitorEmailField;

    @FXML
    private JFXTextField phoneNumberField;

    @FXML
    private SplitMenuButton menuField;

    @FXML
    private MenuItem familyMenuFeild;

    @FXML
    private MenuItem soloMenuField;

    @FXML
    private Label priceLabel;
    
    @FXML
    private JFXTextField firstNameField;

    @FXML
    private JFXTextField lastNameField;

    
    private static Park currentParkDetails;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialize the display of amount of allowed unordered visits 
		updateScreenAllowedUnorderedVisits();
	}
	
	public boolean chooseEntrencePlan(ActionEvent click) throws Exception{
		
		try {
				
			//Calculate entrance price when choosing Type Of Order
			Integer amountOfVisitors = Integer.parseInt(amountOfVisitorsField.getText());
			Alerts visitorsValidate = InputValidation.validateFamilyVisitors(amountOfVisitors.toString());
			
			//If amount of visitors is not between 1-6
			if(visitorsValidate.getAlertType().toString().equals("INFORMATION")) {
				//Renders the calculated price into screen
				Float calculatedPrice = (float) (100*amountOfVisitors);
	
				priceLabel.setText(calculatedPrice.toString());
				return true;
			}else {
				visitorsValidate.showAndWait();
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Please enter valid amount of visitors");
			somethingWentWrong.showAndWait();
		}
		
		return false;
		
	}
	
	
	public void orderButtonAction (ActionEvent click) throws Exception{
				
		try {
			//Parse Information about the order
			String firstNameVisitor = firstNameField.getText();
			String lastNameVisitor = lastNameField.getText();
			
			Integer travelerId = 11;
			if(!travelerIdField.getText().isEmpty()) {				
				travelerId = Integer.parseInt(travelerIdField.getText());
			}
			Integer amountOfVisitors = 10;
			if(!amountOfVisitorsField.getText().isEmpty()) {				
				amountOfVisitors = Integer.parseInt(amountOfVisitorsField.getText());
			}
			
			String visitorEmail = (visitorEmailField.getText());
			String phoneNumber = (phoneNumberField.getText());
			String typeOfOrder = (menuField.getText());
			Integer parkNumber = currentParkDetails.getParkNumber();
			LocalDate today = LocalDate.now();
			LocalTime timeNow = LocalTime.now();
			String orderStatus = "UNORDERED";
			
			
			//Need to Implement checks
			Alerts alertID = InputValidation.ValidateVisitorID(travelerId.toString()); 
			Alerts visitorsValidate = InputValidation.validateFamilyVisitors(amountOfVisitors.toString());
			Alerts emailValidation = InputValidation.validateEmail(visitorEmail);
			Alerts validPhoneNuber = InputValidation.validatePhoneNumber(phoneNumber);
			Alerts checkName = InputValidation.validateNameOrLastname(firstNameVisitor);
			Alerts checkLastName = InputValidation.validateNameOrLastname(lastNameVisitor);
			
			
			
			
			
		
		//validates name and lastname 
		if(checkName.getAlertType().toString().equals("INFORMATION") && checkLastName.getAlertType().toString().equals("INFORMATION")) {
			
			//validates id of traveler
			if(alertID.getAlertType().toString().equals("INFORMATION")) {
				
				//Validates amount of visitors
				if(visitorsValidate.getAlertType().toString().equals("INFORMATION")) {
					
					//Check if can perform unordered visit (if unordered visits amount in db is less than amount of visitors in order)
					if((numberOfAllowedUnorderedVisits() < amountOfVisitors)) {
						Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "This amount of visitors is more than allowed unordered visitors!");
						return;
					}
					
					//Validate email address
					if(emailValidation.getAlertType().toString().equals("INFORMATION")) {
						
						//Validate phone number
						if(validPhoneNuber.getAlertType().toString().equals("INFORMATION")) {
							
							//Post unordered visit as a traveler in the system
							
							//Check if Traveler already exists in the system
							Traveler checkTravelerInformation = new Traveler(travelerId, firstNameVisitor, lastNameVisitor, visitorEmail, phoneNumber, 0, 0);
							ClientServerMessage checkIfTravelerExists = new ClientServerMessage(checkTravelerInformation ,Operation.GET_TRAVLER_INFO);
							ClientUI.clientControllerInstance.sendMessageToServer(checkIfTravelerExists);

							
							//If traveler does not exist, register him
							if(ClientUI.clientControllerInstance.getData().getDataTransfered() == null) {
								ClientServerMessage sendRegistrationRequestToServer = new ClientServerMessage(checkTravelerInformation ,Operation.POST_NEW_TRAVLER);
								ClientUI.clientControllerInstance.sendMessageToServer(sendRegistrationRequestToServer);
								
							}

							
							//Receive last order number from db
							ClientServerMessage lastOrderFromServMessage = new ClientServerMessage(null ,Operation.GET_LAST_ORDER_ID);
							ClientUI.clientControllerInstance.sendMessageToServer(lastOrderFromServMessage);
							Integer lastOrderNumber = (Integer) ClientUI.clientControllerInstance.getData().getDataTransfered() + 1;
							
							//checks for family plan if visitor is more than 1
							if(amountOfVisitors > 1) {
								typeOfOrder = "FAMILY";
							}
							
							//Creating new order
							Float calculatedPrice = (float) (100*amountOfVisitors);
							Order newUnorderedOrder = new Order(lastOrderNumber, travelerId, currentParkDetails.getParkNumber(), amountOfVisitors, calculatedPrice,
									visitorEmail,LocalDate.now(), LocalTime.now(), orderStatus, typeOfOrder, phoneNumber,currentParkDetails.getName());
							
							//Posting new order in the server
							ClientServerMessage postUnorderedorder = new ClientServerMessage(newUnorderedOrder, Operation.POST_TRAVLER_ORDER);
							ClientUI.clientControllerInstance.sendMessageToServer(postUnorderedorder);
							
							//calculate time of exit from park
							LocalTime timeToExit = LocalTime.now().plusHours(currentParkDetails.getStaytime());
							//Post new visit in visits table
							createAndPostToServerVisitForUnordered(newUnorderedOrder, timeToExit);
							
							if(ClientUI.clientControllerInstance.getData().getFlag() == true) {
								
								//Append visits to park and deduct from unordered visits park column
								AppendUnorderedVisitsAndVisitorsToPark(newUnorderedOrder);
								
								//If process was successful
								ParkWorkerEntrenceControlController.orderToEnterOrExit = newUnorderedOrder;
								
								//Show Traveler bill
				    			NavigationManager.openPage("ParkWorkerShowBill.fxml", click, "Traveler Bill", false,false);
							}else {
								//If posting visit wasn't successful
								Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Could not post a visit for the unordered visit");
								somethingWentWrong.showAndWait();
							}
							
							
							
								
							}else {
								validPhoneNuber.showAndWait();
							}
							
						}else {
							emailValidation.showAndWait();
						}
						
					}else {
						visitorsValidate.showAndWait();
					}
				}else {
					alertID.showAndWait();
				}
			}else {
				checkName.showAndWait();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong with the Unordered Vists System");
			somethingWentWrong.showAndWait();
		}
		
		//Updates number of allowed unordered visits to the screen
		updateScreenAllowedUnorderedVisits();
		
	}
	
	//Create a new visit in the visit table
	public void createAndPostToServerVisitForUnordered(Order unorderedOrder, LocalTime timeOfExit) {
		LocalDate currentDate = unorderedOrder.getDate();
		LocalTime timeOfEnter = unorderedOrder.getVisitTime();
		Integer parkNumberOfVisit = unorderedOrder.getParkNumber();
		Integer orderIdOfVisit = unorderedOrder.getOrderId();
		
		//Post the visit to the db
		Visit createVisitForEntrence = new Visit(currentDate, timeOfEnter, timeOfExit, parkNumberOfVisit, orderIdOfVisit);
		ClientServerMessage addNewVisitMessage = new ClientServerMessage(createVisitForEntrence ,Operation.POST_NEW_VISIT);
		ClientUI.clientControllerInstance.sendMessageToServer(addNewVisitMessage);
	}
	
	//deduct number of visitors to the unordered visits column and append to overall park visitors
	public void AppendUnorderedVisitsAndVisitorsToPark(Order orderOfUnorderedVisit) {
		//Receive park information
		ClientServerMessage getParkDetails = new ClientServerMessage(orderOfUnorderedVisit.getParkNumber(), Operation.GET_PARK_DETAILS);
		ClientUI.clientControllerInstance.sendMessageToServer(getParkDetails);
		Park receivedParkInformationFromServer = (Park) ClientUI.clientControllerInstance.getData().getDataTransfered();
		
		
		//Append amount of visitors from park
		//Change Park amount of visitors
    	Integer visitorsWithAppendedWelcomedVisitors = receivedParkInformationFromServer.getCurrentVisitors() + orderOfUnorderedVisit.getAmountOfVisitors();
    	receivedParkInformationFromServer.setCurrentVisitors(visitorsWithAppendedWelcomedVisitors);
    	
    	
    	//Change the allowed unordered visits in the park
    	Integer newUnorderedVisits = receivedParkInformationFromServer.getUnorderedVisits() - orderOfUnorderedVisit.getAmountOfVisitors();
		receivedParkInformationFromServer.setUnorderedVisits(newUnorderedVisits);
		
		//Send the change of allowed unordered visits in the park to the db
		ClientServerMessage changeParkUnorderedVisitors = new ClientServerMessage(receivedParkInformationFromServer, Operation.PATCH_PARK_UNORDEREDVISITS);
		ClientUI.clientControllerInstance.sendMessageToServer(changeParkUnorderedVisitors);
		
		//Change the amount of visitors in the park
		ClientServerMessage changeAmountOfVisitors = new ClientServerMessage(receivedParkInformationFromServer, Operation.PATCH_PARK_VISITORS);
		ClientUI.clientControllerInstance.sendMessageToServer(changeAmountOfVisitors);
	}
    
	//returnes the number of Unordered visits allowed
	public Integer numberOfAllowedUnorderedVisits() {
		Integer currentPark = Usermanager.getCurrentWorker().getWorksAtPark();
		ClientServerMessage getParkAllowedUnorderedVists = new ClientServerMessage(currentPark, Operation.GET_PARK_UNORDEREDVISITS);
		ClientUI.clientControllerInstance.sendMessageToServer(getParkAllowedUnorderedVists);
		
		//Received allowedUnorderedVists from the server
		Integer allowedUnorderedVists = (Integer) ClientUI.clientControllerInstance.getData().getDataTransfered();
		allowedVisitorsLabel.setText(Integer.toString(allowedUnorderedVists));

		return allowedUnorderedVists;
	}
	
	//Updates the amount of allowed unordered visits in the screen
	public void updateScreenAllowedUnorderedVisits() {
		//Receive amount of allowed unordered visits
				Integer currentPark = Usermanager.getCurrentWorker().getWorksAtPark();
				
				//Received allowedUnorderedVists from the server
				Integer allowedUnorderedVists = numberOfAllowedUnorderedVisits();
				allowedVisitorsLabel.setText(Integer.toString(allowedUnorderedVists));
				
				//Get park details (needed to calculate cost of entrance)
				ClientServerMessage parkVisitingDetails = new ClientServerMessage(currentPark, Operation.GET_PARK_DETAILS);
				ClientUI.clientControllerInstance.sendMessageToServer(parkVisitingDetails);
				
				//Received Park Information
				currentParkDetails = (Park) ClientUI.clientControllerInstance.getData().getDataTransfered();
	}
    
}
