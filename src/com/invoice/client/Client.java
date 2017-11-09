package com.invoice.client;

import java.util.ArrayList;

import com.invoice.client.Delivery;
import com.invoice.client.ReloadCartridge;

public class Client {
	
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	private ArrayList<ReloadCartridge> cartridges = new ArrayList<ReloadCartridge>();
	private ArrayList<Service> services = new ArrayList<Service>();
	private String location;
	private String clientName;
	private String upperActTitle;
	private String lowerActTitle;
	private double accrued;
	private double payd;
	
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

	public String getUpperActTitle() {
		return upperActTitle;
	}

	public void setUpperActTitle(String upperActTitle) {
		this.upperActTitle = upperActTitle;
	}

	public String getLowerActTitle() {
		return lowerActTitle;
	}

	public void setLowerActTitle(String lowerActTitle) {
		this.lowerActTitle = lowerActTitle;
	}

	public double getAccrued() {
		return accrued;
	}

	public void setAccrued(double accrued) {
		this.accrued = accrued;
	}

	public double getPayd() {
		return payd;
	}

	public void setPayd(double payd) {
		this.payd = payd;
	}

	public ArrayList<Service> getServices() {
		return services;
	}

	public void setServices(ArrayList<Service> services) {
		this.services = services;
	}

}
