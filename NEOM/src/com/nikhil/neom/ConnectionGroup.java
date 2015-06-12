package com.nikhil.neom;

import java.util.ArrayList;

public class ConnectionGroup {
	public String AppName;
	public Integer processID;
	public String dataSND;
	public String dataRCV;
	private ArrayList<ConnectionChild> Items;

	public ArrayList<ConnectionChild> getItems() {
		return Items;
	}
	
	public void setItems(ArrayList<ConnectionChild> Items) {
        this.Items = Items;
    }
}
