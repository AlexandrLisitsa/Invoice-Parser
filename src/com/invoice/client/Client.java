package com.invoice.client;

import java.util.ArrayList;

import com.invoice.client.Delivery;
import com.invoice.client.ReloadCartridge;

public class Client {
	
	private ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
	private ArrayList<ReloadCartridge> cartridges = new ArrayList<ReloadCartridge>();
	private ArrayList<Service> services = new ArrayList<Service>();
	private ArrayList<Server> servers = new ArrayList<Server>();
	private ArrayList<Addition> additions = new ArrayList<Addition>();
	private String location,clientName,upperActTitle,lowerActTitle;
	private double accrued;
	private double payd;
	private double discount;
	private boolean isAct;
	//реквизиты
	private ArrayList<String> requisites = new ArrayList<String>();
	
	
	public double getTotalAdditionsCost() {
		double x=0;
		for(Addition a:additions) {
			x+=a.getCount()*(a.getCostUAH());
		}
		return x;
	}
	
	public double getTotalCartridgeCostWith25() {
		double x=0;
		for(ReloadCartridge c:cartridges) {
			x+=c.getCount()*(c.getCostUAH()+25);
		}
		return x;
	}
	
	public double getDebt() {
		return accrued-payd;
	}
	
	public double getTotalDeliveriesCostWith10Percent() {
		double x=0;
		for(Delivery d:deliveries) {
			x+=d.getCount()*(d.getCostUAH()*1.1);
		}
		return x;
	}
	
	public double getTotalServiceCost() {
		double x = 0;
		for (Service s : services) {
			x+=s.getCount()*s.getPrice();
		}
		return x;
	}
	
	public double getTotalServersCost() {
		double x = 0;
		for (Server s : servers) {
			x+=s.getCount()*s.getPrice();
		}
		return x;
	}
	
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

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public ArrayList<Server> getServers() {
		return servers;
	}

	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}

	public ArrayList<Addition> getAdditions() {
		return additions;
	}

	public void setAdditions(ArrayList<Addition> additions) {
		this.additions = additions;
	}

	public boolean isAct() {
		return isAct;
	}

	public void setAct(boolean isAct) {
		this.isAct = isAct;
	}

	public ArrayList<String> getRequisites() {
		return requisites;
	}

	public void setRequisites(ArrayList<String> requisites) {
		this.requisites = requisites;
	}

}
