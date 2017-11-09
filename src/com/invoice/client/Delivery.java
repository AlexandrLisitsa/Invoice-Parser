package com.invoice.client;

public class Delivery {
	
	private String date;
	private String goods;
	private double count;
	private double costUSD;
	private double course;
	private double costUAH;
	
	public double getCostUSD() {
		return costUSD;
	}
	public void setCostUSD(double costUSD) {
		this.costUSD = costUSD;
	}
	public double getCostUAH() {
		return costUAH;
	}
	public void setCostUAH(double costUAH) {
		this.costUAH = costUAH;
	}
	public double getCourse() {
		return course;
	}
	public void setCourse(double course) {
		this.course = course;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
