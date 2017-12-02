package com.invoice.client;

public class Server {
	
	private String description;
	private double count;
	private double price;
	public double getPrice() {
		return price;
	}
	
	public static double mathExpressionToDouble(String expression) {

		double tmp = 1;
		if (!expression.contains("*")) {
			return tmp * Double.parseDouble(expression);
		} else {
			while (expression != null) {
				tmp = tmp * Double.parseDouble(expression.substring(0, expression.indexOf('*')));
				expression = expression.substring(expression.indexOf('*') + 1);
				if (!expression.contains("*")) {
					return tmp * Double.parseDouble(expression.substring(0, expression.length()));
				}
			}
			return tmp;
		}
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	public double getCount() {
		return count;
	}
	public void setCount(double count) {
		this.count = count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
