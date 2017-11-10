package com.invoice.client;

public class Addition {
	
	private String date;
	private String description;
	private double count;
	private double costUAH;


	public double getCostUAH() {
		return costUAH;
	}

	public void setCostUAH(double costUAH) {
		this.costUAH = costUAH;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
