package com.invoice.client;

public class ReloadCartridge {
	
	private String date;
	private String printer;
	private String workDescription;
	private double count;
	private double costUAH;
	private double total;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPrinter() {
		return printer;
	}
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	public String getWorkDescription() {
		return workDescription;
	}
	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public double getCostUAH() {
		return costUAH;
	}
	public void setCostUAH(double costUAH) {
		this.costUAH = costUAH;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
}
