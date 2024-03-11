package common;

import java.io.Serializable;

public class Park  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer parkNumber;
	private Integer maxVisitors;
	private Integer capacity;
	private Integer currentVisitors;
	private String location;
	private Integer staytime;
	private Integer workersAmount;
	private Integer managerID;
	private Integer workingTime;
	
	public Park(String name, Integer parkNumber, Integer maxVisitors, Integer capacity, Integer currentVisitors,
			String location, Integer staytime, Integer workersAmount, Integer managerID, Integer workingTime) {
		super();
		this.name = name;
		this.parkNumber = parkNumber;
		this.maxVisitors = maxVisitors;
		this.capacity = capacity;
		this.currentVisitors = currentVisitors;
		this.location = location;
		this.staytime = staytime;
		this.workersAmount = workersAmount;
		this.managerID = managerID;
		this.workingTime = workingTime;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getParkNumber() {
		return parkNumber;
	}
	public void setParkNumber(Integer parkNumber) {
		this.parkNumber = parkNumber;
	}
	public Integer getMaxVisitors() {
		return maxVisitors;
	}
	public void setMaxVisitors(Integer maxVisitors) {
		this.maxVisitors = maxVisitors;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Integer getCurrentVisitors() {
		return currentVisitors;
	}
	public void setCurrentVisitors(Integer currentVisitors) {
		this.currentVisitors = currentVisitors;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Integer getStaytime() {
		return staytime;
	}
	public void setStaytime(Integer staytime) {
		staytime = staytime;
	}
	public Integer getWorkersAmount() {
		return workersAmount;
	}
	public void setWorkersAmount(Integer workersAmount) {
		this.workersAmount = workersAmount;
	}
	public Integer getManagerid() {
		return managerID;
	}
	public void setManagerid(Integer managerid) {
		this.managerID = managerid;
	}
	public Integer getWorkingTime() {
		return workingTime;
	}
	public void setWorkingTime(Integer workingTime) {
		this.workingTime = workingTime;
	}
	
}
