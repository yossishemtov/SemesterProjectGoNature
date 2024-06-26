package common;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientServerMessage<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//A generic class that is meant to handle data and command from server and client in a generic way
	private Object dataTransfered;
	private String command;
	private Boolean flag;
	
	
	
	
	
	public ClientServerMessage(Object dataTransfered, String command) {
		this.dataTransfered = dataTransfered;
		this.command = command;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> convertDataToArrayList() throws Exception{
		//Converting a generic data of some kind of objects to arrayList of them
		return (ArrayList<T>)this.dataTransfered;
	}
	
	@Override
	public String toString() {
	    // Check for null directly without casting
	    String dataString = (dataTransfered != null) ? dataTransfered.toString() : "NULL";
	    return "Operation: " + command + " | Data: " + dataString;
	}

	
	@SafeVarargs
	public static<T> ArrayList<T> createDataArrayList(T... dataObject){
		//Generic method to create an arrayList from any kind of dataobjects provided
		
		ArrayList<T> data = new ArrayList<>();
		for(T param : dataObject) {
			data.add(param);
		}
		
		return data;
		
	}

	public Object getDataTransfered() {
		return dataTransfered;
	}

	public void setDataTransfered(Object dataTransfered) {
		this.dataTransfered = dataTransfered;
	}
	
	public void setflagFalse() {
		this.flag=false;
	}
	
	public void setflagTrue() {
		this.flag=true;
	}
	
	public Boolean getFlag() {
		return flag;
	}
	

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	
}
