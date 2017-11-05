package com.invoice;

import java.util.ArrayList;

public class Client {
	
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	private ArrayList<ReloadCartridge> cartridges = new ArrayList<ReloadCartridge>();
	private String location;
	private String clientName;
	
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public ArrayList<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ArrayList<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ArrayList<ReloadCartridge> getCartridges() {
		return cartridges;
	}

	public void setCartridges(ArrayList<ReloadCartridge> cartridges) {
		this.cartridges = cartridges;
	}

}
