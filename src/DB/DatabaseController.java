package DB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import common.*;
import common.worker.*;

/**
 * This class is responsible for managing database operations related to orders.
 */
public class DatabaseController {
	private MySqlConnector connector;
	private Connection connectionToDatabase;

	/**
	 * Constructs a DatabaseController object with specified user credentials.
	 *
	 * @param username the database username
	 * @param password the database password
	 */
	public DatabaseController(String username, String password) {
		this.connector = new MySqlConnector(username, password);
		connectionToDatabase = this.connector.getDbConnection();
	}
	
	

	public boolean insertChangeRequest(ChangeRequest request) {
	    String query = "INSERT INTO `changerequests` (parkName, parkNumber, maxVisitors, gap, staytime, approved) VALUES (?, ?, ?, ?, ?, ?)";
	    try (
	         PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	        
	        ps.setString(1, request.getParkName());
	        ps.setInt(2, request.getParkNumber());
	        ps.setInt(3, request.getMaxVisitors());
	        ps.setDouble(4, request.getGap());
	        ps.setInt(5, request.getStaytime());
	        ps.setString(6, request.getApproved()); // Assuming you handle the enum to string conversion
	        
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	public ArrayList<ChangeRequest> getChangeRequestsWaitingForApproval(GeneralParkWorker worker) {
		ArrayList<ChangeRequest> requests = new ArrayList<>();
	    String query = "SELECT * FROM `changerequests` WHERE parkNumber = ? AND approved = 'WAITING_FOR_APPROVAL'";
	    
		// Determine the query based on the worker's role
	    if ("Department Manager".equals(worker.getRole())) {
	        // For department managers: fetch requests waiting for approval for a specific park
	        query = "SELECT * FROM `changerequests` WHERE parkNumber = ? AND approved = 'WAITING_FOR_APPROVAL'";
	    } else if ("Park Manager".equals(worker.getRole())) {
	        // For park managers: fetch all requests, regardless of approval status
	        query = "SELECT * FROM `changerequests` WHERE parkNumber = ?";
	    } 
	    System.out.println(query);
	    try (
	         PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	        
	        ps.setInt(1, worker.getWorksAtPark());
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ChangeRequest request = new ChangeRequest(
	                        rs.getInt("id"),
	                        rs.getString("parkName"),
	                        rs.getInt("parkNumber"),
	                        rs.getInt("maxVisitors"),
	                        rs.getInt("gap"),
	                        rs.getInt("staytime"),
	                        rs.getString("approved"));
	                requests.add(request);
	            }
	        }
	    }  catch (SQLException e) {
	        System.out.println("SQLException occurred");
	        e.printStackTrace();
	    } catch (Exception e) {
	        System.out.println("General exception occurred");
	        e.printStackTrace();
	    }
	    
	    System.out.println("not error in");

	    return requests.isEmpty() ? null : requests; // Return null if the list is empty
	}
	
	
	public boolean updateChangeRequestStatus(ChangeRequest request) {
	    String query = "UPDATE `changerequests` SET approved = ? WHERE id = ?";
	    try (
	         PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	        
	        ps.setString(1, request.getApproved()); // Use the request's approved status
	        ps.setInt(2, request.getId()); // Use the request's id
	        
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}





	/**
     * Updates a traveler's record to mark them as a guide.
     *
     * @param travelerId the ID of the traveler to be updated.
     * @return true if the update is successful, false otherwise.
     */
    public boolean ChangeTravelerToGuide(Traveler traveler) {
     

        String query = "UPDATE `travler` SET GroupGuide = 1 WHERE id = ?";

        try (PreparedStatement pstmt = connectionToDatabase.prepareStatement(query)) {
            pstmt.setInt(1, traveler.getId());

            int affectedRows = pstmt.executeUpdate();

            // Check if the update was successful
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        } 
    }

	
	 public boolean insertNewGroupGuide(Traveler traveler) {
	        // Assuming you have a method getConnection() that returns a Connection object.
	     

	        String query = "INSERT INTO `travler` (id, firstName, lastName, email, phoneNumber, GroupGuide, isloggedin) VALUES (?, ?, ?, ?, ?, ?, ?)";

	        try (PreparedStatement pstmt = connectionToDatabase.prepareStatement(query)) {
	            pstmt.setInt(1, traveler.getId());
	            pstmt.setString(2, traveler.getFirstName());
	            pstmt.setString(3, traveler.getLastName());
	            pstmt.setString(4, traveler.getEmail());
	            pstmt.setString(5, traveler.getPhoneNum());
	            pstmt.setInt(6, traveler.getIsGroupGuide()); // Assuming 1 for guide, 0 for not a guide.
	            pstmt.setInt(7, 0); // Assuming isloggedin default to 0.

	            int affectedRows = pstmt.executeUpdate();

	            // Check if the insert was successful
	            if (affectedRows > 0) {
	                return true;
	            }
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        } 

	        return false;
	    }
	
	
	// Method to get a Park object by parkNumber
	 public Park getParkDetails(int parkNumber) {
	     Park park = null;
	     String query = "SELECT * FROM `park` WHERE parkNumber = ?";

	     try (PreparedStatement preparedStatement = connectionToDatabase.prepareStatement(query)) {
	         preparedStatement.setInt(1, parkNumber);

	         ResultSet resultSet = preparedStatement.executeQuery();

	         if (resultSet.next()) {
	             System.out.println("in");

	             String name = resultSet.getString("name");
	             Integer maxVisitors = resultSet.getInt("maxVisitors");
	             Integer capacity = resultSet.getInt("capacity");
	             Integer currentVisitors = resultSet.getInt("currentVisitors");
	             String location = resultSet.getString("location");
	             Integer staytime = resultSet.getInt("staytime");
	             Integer workersAmount = resultSet.getInt("workersAmount");
	             Integer managerID = resultSet.getInt("managerId");
	             Integer workingTime = resultSet.getInt("workingTime");
	             Integer gap = resultSet.getInt("gap"); // Retrieve gap from resultSet

	             park = new Park(name, parkNumber, maxVisitors, capacity, currentVisitors, location, staytime, workersAmount, gap, managerID, workingTime);
	         }
	     } catch (SQLException e) {
	         e.printStackTrace();
	     }
	     return park;
	 }

	
	

    public Traveler getTravelerDetails(Traveler travelerFromClient) {
        String query = "SELECT firstName, lastName, email, phoneNumber, GroupGuide, isloggedin FROM `travler` WHERE id = ?";
        Traveler traveler = null; // Initialize to null

        try (PreparedStatement preparedStatement = connectionToDatabase.prepareStatement(query)) {
            preparedStatement.setInt(1, travelerFromClient.getId());


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Instantiate a new Traveler object with the retrieved details
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    String email = resultSet.getString("email");
                    String phoneNum = resultSet.getString("phoneNumber");
                    Integer isGrupGuide = resultSet.getInt("GroupGuide");
                    Integer isloggedin = resultSet.getInt("isloggedin"); // Fetch isloggedin status
                    traveler = new Traveler(travelerFromClient.getId(), firstName, lastName, email, phoneNum, isGrupGuide, isloggedin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider better exception handling
        }
        return traveler; // Returns the new Traveler object or null if not found
    }

	// Get GeneralParkWorkerDetails
	// check GetGeneralParkWorker login details and return the data,if not exist return empty ArrayList of type generalParkWorker
	// WorkerId | firstName | lastName | email | role | userName | password | worksAtPark
	public GeneralParkWorker getGeneralParkWorkerDetails(GeneralParkWorker worker) {
	    // Removed unused ArrayList<Order>
		System.out.println("worker username: " + worker.getUserName() + "worker password: " + worker.getPassword());
	    System.out.println("in db");
	    
	    GeneralParkWorker generalParkWorker = null;

	    String query = "SELECT * FROM `generalparkworker` WHERE userName = ? AND password= ?";

	    try (PreparedStatement preparedStatementInstance = connectionToDatabase.prepareStatement(query)) {
	        preparedStatementInstance.setString(1, worker.getUserName());
	        preparedStatementInstance.setString(2, worker.getPassword());
	        ResultSet returnedStatement = preparedStatementInstance.executeQuery();

	        while (returnedStatement.next()) {

	            Integer workerId = returnedStatement.getInt(1);
	            String firstName = returnedStatement.getString(2);
	            String lastName = returnedStatement.getString(3);
	            String email = returnedStatement.getString(4);
	            String role = returnedStatement.getString(5);
	            String userName = returnedStatement.getString(6);
	            String password = returnedStatement.getString(7);
	            Integer worksAtPark = returnedStatement.getInt(8);
	            System.out.println(role);
	            
                generalParkWorker = new GeneralParkWorker(workerId, firstName, lastName, email, role, userName, password, worksAtPark);

	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null; // Consider throwing a custom exception
	    }
	
//		System.out.println(generalParkWorker.toString());

	    return generalParkWorker; // This will return an empty list if there were no records found
	}

	
	
	   /**
     * Retrieves all orders for a given traveler from the database.
     * @param traveler The traveler whose orders are to be retrieved.
     * @return An ArrayList of Order objects.
     */
	public ArrayList<Order> getOrdersDataFromDatabase(Traveler traveler) {
	    ArrayList<Order> orderDataForClient = new ArrayList<>();
	    // Ensure the query reflects your actual database table and column names
	    String query = "SELECT orderId, travelerId, parkNumber, amountOfVisitors, price, visitorEmail, date, TelephoneNumber, visitTime, orderStatus, typeOfOrder FROM orders WHERE travelerId = ?";

	    try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	        ps.setInt(1, traveler.getId());
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Integer orderId = rs.getInt("orderId");
	            // Integer travelerId = rs.getInt("travelerId"); // Not used in the Order constructor directly
	            Integer parkNumber = rs.getInt("parkNumber");
	            Integer amountOfVisitors = rs.getInt("amountOfVisitors");
	            Float price = rs.getFloat("price");
	            String visitorEmail = rs.getString("visitorEmail");
	            LocalDate date = rs.getDate("date").toLocalDate();
	            String telephoneNumber = rs.getString("TelephoneNumber"); // Assuming you have a field to store this in Order
	            LocalTime visitTime = rs.getTime("visitTime").toLocalTime();
	            String statusStr = rs.getString("orderStatus"); 
	            String typeOfOrderStr = rs.getString("typeOfOrder");

	            // Assuming Order constructor is updated to accept all the necessary fields including telephoneNumber
	            Order order = new Order(orderId, traveler.getId(), parkNumber, amountOfVisitors, price, visitorEmail, date, visitTime, statusStr, typeOfOrderStr, telephoneNumber);
	            orderDataForClient.add(order);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return orderDataForClient; // This will return an empty list if there were no records found or an error occurred
	}


	

		 /**
	     * Inserts a new reservation (order) into the database.
	     * @param traveler The traveler making the reservation.
	     * @param order The details of the order being made.
	     * @return true if insertion was successful, false otherwise.
	     */
		public Boolean insertTravelerOrder(Order order) {
		    // Adjusting the query to match the database schema order provided
		    String query = "INSERT INTO `order` (orderId, travelerId, parkNumber, amountOfVisitors, price, visitorEmail, date, TelephoneNumber, visitTime, orderStatus, typeOfOrder) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		    try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
		        // Set parameters based on the Order object fields, in the order specified
		        ps.setInt(1, order.getOrderId());
		        ps.setInt(2, order.getVisitorId());
		        ps.setInt(3, order.getParkNumber());
		        ps.setInt(4, order.getAmountOfVisitors());
		        ps.setFloat(5, order.getPrice());
		        ps.setString(6, order.getVisitorEmail());
		        ps.setDate(7, java.sql.Date.valueOf(order.getDate()));
		        ps.setString(8, order.getTelephoneNumber()); // Assuming getTelephoneNumber() method exists
		        ps.setTime(9, java.sql.Time.valueOf(order.getVisitTime()));
		        ps.setString(10, order.getOrderStatus()); // Using the enum's name as the DB value
		        ps.setString(11, order.getTypeOfOrder()); // Similarly here

		        int affectedRows = ps.executeUpdate();
		        if (affectedRows > 0) {
		            System.out.println("Order inserted successfully.");
		            return true;
		        } else {
		            System.out.println("A problem occurred and the order was not inserted.");
		            return false;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}




	    /**
	     * Updates the status of an existing order in the database.
	     * @param order The order object containing the order ID and the new status.
	     * @return true if the update was successful, false otherwise.
	     */
		public Boolean updateOrderStatus(Order order) {
			String updateQuery = "UPDATE 'order' SET status = ? WHERE orderId = ?";

			try (PreparedStatement ps = connectionToDatabase.prepareStatement(updateQuery)) {
				ps.setString(1, order.getOrderStatus()); // Assuming getStatus() returns the new status of the order
				ps.setInt(2, order.getOrderId()); // Assuming getOrderId() returns the ID of the order to be updated

				int affectedRows = ps.executeUpdate();
				if (affectedRows > 0) {
					System.out.println("Order status updated successfully.");
					return true;
				} else {
					System.out.println(
							"No order was found with the provided ID, or the status is already set to the new value.");
				}
			} catch (SQLException e) {
				System.out.println("An error occurred while updating the order status:");
				e.printStackTrace();
			}
			return false;
		}
		
		
	    /**
	     * Deletes an existing order from the database based on its ID.
	     * @param orderId The ID of the order to delete.
	     * @return true if the deletion was successful, false otherwise.
	     */
	    public Boolean deleteOrder(int orderId) {
	        String deleteQuery = "DELETE FROM `order` WHERE orderId = ?";

	        try (PreparedStatement ps = connectionToDatabase.prepareStatement(deleteQuery)) {
	            ps.setInt(1, orderId);

	            int affectedRows = ps.executeUpdate();
	            return affectedRows > 0;
	        } catch (SQLException e) {
	            System.out.println("An error occurred while deleting the order:");
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	    
	    public Boolean patchParkParameters(ChangeRequest changeRequest) {
	        // Assuming capacity is a column in your database that should be updated based on gap
	        // Calculate the new capacity based on the provided gap and maxVisitors
	        
	        // Update only the fields that are affected by a change request
	        String query = "UPDATE `park` SET maxVisitors = ?, stayTime = ?, gap = ? WHERE parkNumber = ?";

	        try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	            ps.setInt(1, changeRequest.getMaxVisitors());
	            ps.setInt(2, changeRequest.getStaytime());
	            ps.setInt(3, changeRequest.getGap());
	            ps.setInt(4, changeRequest.getParkNumber());

	            int affectedRows = ps.executeUpdate();
	            return affectedRows > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    
	    /**
	     * Updates the status of generalparkworker to signedin
	     * @param GeneralParkWorker to sign
	     * @return true if the signedin was successful, false otherwise.
	     */
	    public Boolean changeSingedInOfGeneralParkWorker(GeneralParkWorker signedParkWorker) {
	    	String query = "UPDATE generalparkworker SET isloggedin = 1 WHERE workerid = ?";
	    	
	    	try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)){
	    		ps.setInt(1, signedParkWorker.getWorkerId());
	    		int affectedRows = ps.executeUpdate();
	    		
	    		return affectedRows > 0;
	    		
	    	}catch(SQLException e) {
	    		e.printStackTrace();
	    		return false;
	    	}
	    	
	    }
	    
	    /**
	     * Updates the status of generalparkworker to signedout
	     * @param GeneralParkWorker to sign
	     * @return true if the signed out was successful, false otherwise.
	     */
	    public Boolean changeSignedOutOfGeneralParkWorker(GeneralParkWorker signedParkWorker) {
	    	String query = "UPDATE `generalparkworker` SET isloggedin = 0 WHERE workerid = ?";
	    	
	    	try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)){
	    		ps.setInt(1, signedParkWorker.getWorkerId());
	    		int affectedRows = ps.executeUpdate();
	    		
	    		return affectedRows > 0;
	    		
	    	}catch(SQLException e) {
	    		e.printStackTrace();
	    		return false;
	    	}
	    	
	    }
	    
	    /**
	     * Gets the status of loggedin of generalparkworker
	     * @param GeneralParkWorker
	     * @return isloggedin of generalparkworker
	     */
	    public Boolean getSignedinStatusOfGeneralParkWorker(GeneralParkWorker signedParkWorker) {
	    	//Return the status of isloggedin of generalparkworker
	    	String query = "SELECT isloggedin FROM generalparkworker WHERE workerid = ?";
	     
	    	
	    	try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)){
	    		ps.setInt(1, signedParkWorker.getWorkerId());
	    		ResultSet rs = ps.executeQuery();
	    		
	    		if (rs.next()) {
	                // Retrieve the value of isloggedin from the ResultSet
	                int isLoggedIn = rs.getInt("isloggedin");
	                System.out.print(isLoggedIn);
	                 if(isLoggedIn == 0)
	                	 return false;
	                 
	                 return true;
	            } else {
	                // No rows returned, worker not found or not signed in
	                return true;
	            }
	    		
	    	}catch(SQLException e) {
	    		e.printStackTrace();
	    		return false;
	    	}
	    	
	    	
	    }
	    
	    /**
	     * Gets the amount of visitors in the park where the parkworker works at
	     * @param parkworker the park worker information
	     * @return park information if successful and null if not found
	     */

	    public Park getAmountOfVisitorsByParkWorker(GeneralParkWorker parkworker) {
	    	//Querying for the park information with the park number associated with the park worker
	    	String query = "Select * FROM park WHERE parkNumber = ?";
	    	Park fetchedPark = null;
	    	
	    	try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	            ps.setInt(1, parkworker.getWorksAtPark());
	            ResultSet rs = ps.executeQuery(); // Use executeQuery for SELECT statements


	  
	                if (rs.next()) { // Use if instead of while if you expect or require a single result
	                    fetchedPark = new Park(
	                        rs.getString("name"), 
	                        rs.getInt("parkNumber"), 
	                        rs.getInt("maxVisitors"), 
	                        rs.getInt("capacity"), 
	                        rs.getInt("currentVisitors"), 
	                        rs.getString("location"), 
	                        rs.getInt("staytime"), 
	                        rs.getInt("workersAmount"), 
	                        rs.getInt("gap"), // Assuming you have a column for gap in your DB
	                        rs.getInt("managerID"), // Assuming managerID is stored directly as an integer
	                        rs.getInt("workingTime")
	                    );
	                }
	            }
	         catch (SQLException e) {
	            e.printStackTrace();
	            // Consider logging this exception or handling it more gracefully
	        }

	        return fetchedPark;
	    }
	    
	    public Park getAmountOfVisitors(GeneralParkWorker worker) {
	        String query = "SELECT * FROM park WHERE parkNumber = ?"; 

	        Park park = null;
	        try (PreparedStatement ps = connectionToDatabase.prepareStatement(query)) {
	            ps.setInt(1, worker.getWorksAtPark());
	            try (ResultSet rs = ps.executeQuery()) { 

	                if (rs.next()) { 
	                    park = new Park(
	                        rs.getString("name"), 
	                        rs.getInt("parkNumber"), 
	                        rs.getInt("maxVisitors"), 
	                        rs.getInt("capacity"), 
	                        rs.getInt("currentVisitors"), 
	                        rs.getString("location"), 
	                        rs.getInt("staytime"), 
	                        rs.getInt("workersAmount"),
	                        rs.getInt("gap"), 
	                        rs.getInt("managerID"), 
	                        rs.getInt("workingTime")
	                    );
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace(); 
	        }
	        return park;
	    }
	    
	    
	    public VisitorsReport getTotalNumberOfVisitorsReport(GeneralParkWorker worker) {
	        // Adjusted query to include filtering by park number
	 
	        String query = "SELECT typeOfOrder, SUM(amountOfVisitors) AS totalVisitors FROM `order` WHERE parkNumber = ? GROUP BY typeOfOrder";
	        VisitorsReport report=null;

	        Integer totalIndividuals = 0;
	        Integer totalGroups = 0;
	        Integer totalFamilies = 0; // Initialize total for family visitors

	        try (PreparedStatement statement = connectionToDatabase.prepareStatement(query)) {
	            // Set the park number in the query
	            statement.setInt(1, worker.getWorksAtPark());

	            try (ResultSet resultSet = statement.executeQuery()) {
	                while (resultSet.next()) {
	                    String typeOfOrder = resultSet.getString("typeOfOrder");
	                    int totalVisitors = resultSet.getInt("totalVisitors");
	                    System.out.println("Order Type: " + typeOfOrder + ", Total Visitors: " + totalVisitors);
	                    System.out.println("Order Type Raw: [" + typeOfOrder + "], Length: " + typeOfOrder.length());
	                    
	                    if (typeOfOrder.trim().equalsIgnoreCase("GUIDEDGROUP")) {
	                        System.out.println("Match found after trimming and case-insensitive check");
	                    } else {
	                        System.out.println("No match found");
	                    }
	                    typeOfOrder=typeOfOrder.trim();
	                    switch (typeOfOrder) {
	                        case "SOLO":
	                            totalIndividuals += totalVisitors;
	                            break;
	                        case "GUIDEDGROUP":
	    	                    System.out.println("in Order Type: " );

	                            totalGroups += totalVisitors;
	                            break;
	                        case "FAMILY":
	                            totalFamilies += totalVisitors;
	                            break;
	                        default:
	                            // Handle any unexpected typeOfOrder
	                            break;
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            System.err.println("An error occurred while fetching the total number of visitors report: " + e.getMessage());
	            e.printStackTrace();
	        }
	        report=new VisitorsReport(totalIndividuals, totalGroups, totalFamilies,totalIndividuals+ totalGroups+totalFamilies);
	        System.out.println(report.toString());
	        return report; // Pass the new total as well
	    }


	    


}


