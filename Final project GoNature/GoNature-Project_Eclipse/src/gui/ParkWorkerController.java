package gui;

import java.io.IOException;
import java.net.URL;


import java.util.ResourceBundle;
import client.ClientUI;
import client.NavigationManager;
import common.Alerts;
import common.ClientServerMessage;
import common.Operation;
import common.Usermanager;
import common.worker.GeneralParkWorker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ParkWorkerController implements Initializable {

	@FXML
	private JFXButton LogoutBtn;
    @FXML
    private JFXButton profileBtn;

    @FXML
    private JFXButton availableSpaceBtn;

    @FXML
    private JFXButton entrenceControlBtn;

    @FXML
    private JFXButton unorderedVisitAction;
    
    @FXML
    private BorderPane mainPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox vbox;


	@Override
	/**
	 * Loading loggedin worker profile to the screen as soon as the page is loaded
	 * 
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		//Render user information when coming to the parkWorkerFrame screen
		
		try {
			loadProfileImmediately();
			//Parsing worker information to the screen
			GeneralParkWorker loggedInWorker = Usermanager.getCurrentWorker();
		
		}catch(Exception e) {
			
			Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when loading user information");
			somethingWentWrong.showAndWait();
		
		}
		}

	   private void loadProfileImmediately() {
	        try {
	        	loadProfileOfWorker(null); 
	        } 
	         catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }



    


	   /**
		 * Transitioning to the profile.fxml
		 * 
		 * @param event (Listening for an event) 
		 */
	public void loadProfileOfWorker(ActionEvent click) throws Exception{
		//Loading profile of the current park worker
		try {
			 NavigationManager.openPageInCenter(mainPane,"Profile.fxml");
		 } catch(Exception e) {
			 Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when loading profile");
				somethingWentWrong.showAndWait();
			 e.printStackTrace();
		 }
	    }
	
	    
	/**
	 * Transitioning to the ParkWorkerAvailableSpace.fxml, the check available space in park functionality.
	 * 
	 * @param event (Listening for an event)
	 */
    public void availableSpaceBtnAction(ActionEvent click) throws Exception{
    	//Displaying the available space in the park
    	try {
   		 NavigationManager.openPageInCenter(mainPane,"ParkWorkerAvailableSpace.fxml");
   	 } catch(Exception e) {
   		 Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when loading checking available space");
   			somethingWentWrong.showAndWait();
   		 e.printStackTrace();
   	 }

  }
    
    /**
	 * Transitioning to the ParkWorkerEntrenceControl.fxml the entrence control system.
	 * 
	 * @param event (Listening for an event)
	 */
    public void entrenceControlAction(ActionEvent click) throws Exception{
    	//Loading the entrenceControl page
    	try {
      		 NavigationManager.openPageInCenter(mainPane,"ParkWorkerEntrenceControl.fxml");
      	 } catch(Exception e) {
      		 Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when loading parkworker entrence control");
      			somethingWentWrong.showAndWait();
      		 e.printStackTrace();
      	 }
    }
    
    /**
	 * Transitioning to the ParkWorkerUnorderedVisit.fxml the unordered visit system.
	 * 
	 * @param event (Listening for an event)
	 */
    public void unorderedVisitAction(ActionEvent click) throws Exception{
    	//Loading unordered visit system page
    	try {
     		 NavigationManager.openPageInCenter(mainPane,"ParkWorkerUnorderedVisit.fxml");
     	 } catch(Exception e) {
     		 Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when loading unordered visit system");
     			somethingWentWrong.showAndWait();
     		 e.printStackTrace();
     	 }
    }

    /**
	 * Transitioning to the HomePageFrame.fxml the main page frame.
	 * 
	 * @param event (Listening for an event)
	 */
    public void backBtnAction(ActionEvent click) throws Exception{
    		//Loading main login screen when clicking on the back button
    	try {
	    		if(Usermanager.getCurrentWorker() != null) {
	    			ClientServerMessage requestToLogout = new ClientServerMessage(Usermanager.getCurrentWorker(), Operation.PATCH_GENERALPARKWORKER_SIGNEDOUT);
	    			ClientUI.clientControllerInstance.sendMessageToServer(requestToLogout);
					Usermanager.setCurrentWorker(null);

	    		}
	    		
	    		//Changing page back to main menu
	    		NavigationManager.openPage("HomePageFrame.fxml", click, "Home Page", true, true);
    		
    		}catch(Exception e) {
    			Alerts somethingWentWrong = new Alerts(Alerts.AlertType.ERROR, "ERROR","", "Something went wrong when trying to return to main menu");
    			somethingWentWrong.showAndWait();
    		}
    }



			
	}


