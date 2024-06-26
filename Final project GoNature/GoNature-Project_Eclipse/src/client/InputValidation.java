package client;

import common.Alerts;

/**
 * Provides methods for input validation in the client application.
 */
public class InputValidation {

	/**
	 * Validates a visitor ID.
	 *
	 * @param visitorID the visitor ID to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts ValidateVisitorID(String visitorID) {
        // Check if the visitor ID is empty
        if (visitorID.equals("")) {
            return new Alerts(Alerts.AlertType.ERROR, "Invalid ID", "", "You need to insert your ID");
        }
        // Check if the visitor ID starts with a negative sign
        else if (visitorID.startsWith("-")) {
            return new Alerts(Alerts.AlertType.ERROR, "Invalid ID", "", "ID must be a non-negative number");
        }

        // Check if the visitor ID does not contain 9 digits
        else if (!visitorID.matches("\\d{9}")) {
            return new Alerts(Alerts.AlertType.ERROR, "Invalid ID", "", "Your ID must contain 9 digits");
        }
        // ID is valid
        else {
        	 return new Alerts(Alerts.AlertType.INFORMATION, "ID Validation", "", "Valid ID");
        }
    }
	
	/**
	 * Validates an email address.
	 *
	 * @param email the email address to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateEmail(String email) {
	    // Check if the email is empty
	    if (email.isEmpty()) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Email", "", "Email cannot be empty");
	    }
	    
	    // Check if the email matches the expected format
	    else if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Email", "", "Invalid email format");
	    }

	    // Email is valid
	    else {
	        return new Alerts(Alerts.AlertType.INFORMATION, "Valid Email", "", "Valid Email");
	    }
	}
	
	/**
	 * Validates the number of visitors for a family order.
	 *
	 * @param amountOfVisitors the number of visitors to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateFamilyVisitors(String amountOfVisitors) {
		 if (amountOfVisitors.isEmpty()) {
		        return new Alerts(Alerts.AlertType.ERROR, "Invalid amountofvisitors", "", "amountofvisitors cannot be empty");
		    }
		    
		
		//Checks if order visitors is between 1-6
		if (!amountOfVisitors.matches("[1-6]")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Visitors Number", "", "Amount of visitors for family order must be between 1-6");
		}else {
			return new Alerts(Alerts.AlertType.INFORMATION, "Valid Amount Of Visitors", "", "Valid Amount Of Visitors");
		}
		
		
	}
	
	/**
	 * Validates the number of visitors for a solo order.
	 *
	 * @param amountOfVisitors the number of visitors to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateSoloVisitor(String amountOfVisitors) {
		if (amountOfVisitors.isEmpty()) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid amountofvisitors", "", "amountofvisitors cannot be empty");
	    }
		
		//Checks if order visitors is between 1-6
		if (!amountOfVisitors.matches("[1]")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Visitors Number", "", "Amount of visitors for solo order must be 1");
		}else {
			return new Alerts(Alerts.AlertType.INFORMATION, "Valid Amount Of Visitors", "", "Valid Amount Of Visitors");
		}
		
		
	}
	
	/**
	 * Validates the type of order.
	 *
	 * @param typeOfOrder the type of order to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateTypeOfOrder(String typeOfOrder) {
		if (typeOfOrder.isEmpty()) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid typeoforder", "", "typeoforder cannot be empty");
	    }
		
		
		//Checks the type of order, if any of the three options available
		
		if (typeOfOrder.equals("FAMILY") || typeOfOrder.equals("SOLO") || typeOfOrder.equals("GUIDEDGROUP")) {
			return new Alerts(Alerts.AlertType.INFORMATION, "Valid type of order", "", "Valid type of order");
		}else {
			return new Alerts(Alerts.AlertType.ERROR, "Invalid TypeOfOrder", "", "Type of order is not valid");
		}
		
		
	}
	
	/**
	 * Validates the number of visitors for a group guided order.
	 *
	 * @param amountOfVisitors the number of visitors to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateGroupGuideVisitors(String amountOfVisitors) {
		if (amountOfVisitors.isEmpty()) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid amountofvisitors", "", "amountofvisitors cannot be empty");
	    }
		
		//Checks if order visitors is between 1-15
		if (!amountOfVisitors.matches("[1-9]|1[0-5]?")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Visitors Number", "", "Amount of visitors for group order must be between 1-15");
		}else {
			return new Alerts(Alerts.AlertType.INFORMATION, "Valid Amount Of Visitors", "", "Valid Amount Of Visitors");
		}
		
		
	}
	
	/**
	 * Validates the price.
	 *
	 * @param priceToPay the price to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validatePrice(String priceToPay) {
		//Checks if order visitors is between 1-6
		if (!priceToPay.matches("\\d+(\\.\\d+)?")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Price", "", "Price must be a valid number");
	    }else {
			return new Alerts(Alerts.AlertType.INFORMATION, "Invalid Price", "", "Price must be a valid number");
		}
	}
	
	/**
	 * Validates a name or last name.
	 *
	 * @param nameOrLastname the name or last name to validate
	 * @return an Alerts object indicating the validation result
	 */
	public static Alerts validateNameOrLastname(String amountOfVisitors) {
		//Checks if order visitors is between 1-6
		if (!amountOfVisitors.matches("^[a-zA-Z]{1,10}$")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Name Or Last Name", "", "Name and Last name must not exceed 10 letters and contain no numbers");
		}else {
			return new Alerts(Alerts.AlertType.INFORMATION, "Valid name or lastname", "", "Valid name or lastname");
		}
		
		
	}
	
	
	/**
	 * Validates a password.
	 *
	 * @param password the password to validate
	 * @return an Alerts object indicating the validation result
	 */
	 public static Alerts validatePassword(String password) {
	        // Check if password is empty
	        if (password.equals("")) {
	            return new Alerts(Alerts.AlertType.ERROR, "Invalid Password", "", "Password cannot be empty.");
	        }
	        
	        // Password does not contain both letters and numbers
	        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).*$")) {
	            return new Alerts(Alerts.AlertType.ERROR, "Invalid Password", "", "Password must contain both letters and numbers.");
	        }
	        
	        // Password is valid
	        else {
	        	return new Alerts(Alerts.AlertType.INFORMATION, "Password Validation", "", "Valid password");
	        }
	 }
	 
	 /**
	  * Validates an order number.
	  *
	  * @param orderId the order ID to validate
	  * @return an Alerts object indicating the validation result
	  */
	 public static Alerts validateOrderNumber(String orderId) {
		 if(!orderId.matches("[1-9]\\d{0,8}|0")) {
	            return new Alerts(Alerts.AlertType.ERROR, "Invalid Order Id", "", "Order id must only contain numbers and not exceed 8 characters");
		 }else {
	        	return new Alerts(Alerts.AlertType.INFORMATION, "Order Id Validation", "", "Order Id Validation");
	        }
	 }
	 
	 /**
	  * Validates a phone number.
	  *
	  * @param phoneNumber the phone number to validate
	  * @return an Alerts object indicating the validation result
	  */
	 public static Alerts validatePhoneNumber(String phoneNumber) {
		    // Check for a valid phone number (11 digits starting with "05")
		    if (!phoneNumber.matches("^05[0-9]{8}$")) {
		        return new Alerts(Alerts.AlertType.ERROR, "Invalid Phone Number", "", "Phone number must start with '05' and contain 10 digits in total");
		    } else {
		        return new Alerts(Alerts.AlertType.INFORMATION, "Valid Phone Number", "", "Valid Phone Number");
		    }
		}

	 
 
	 /**
	  * Validates a username.
	  *
	  * @param username the username to validate
	  * @return an Alerts object indicating the validation result
	  */
	 public static Alerts validateUsername(String username) {
	    // Check if the username is empty
	    if (username.isEmpty()) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Username", "", "Username cannot be empty");
	    } 
	    
	    // Check if the first character of the username is not a letter
	    else if (!Character.isLetter(username.charAt(0))) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Username", "", "Username must start with a letter");
	    } 

	    // Check if the username has fewer than two characters
	    else if (username.length() < 2) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Username", "", "Username must have at least two characters");
	    } 
	    
	    // Check if the username contains special characters
	    else if (!username.matches("^[a-zA-Z0-9]*$")) {
	        return new Alerts(Alerts.AlertType.ERROR, "Invalid Username", "", "Username cannot contain special characters");
	    } 

	    // username is valid
	    else {
	        return new Alerts(Alerts.AlertType.INFORMATION, "Valid Username", "", "Valid Username");
	    }
	}

}