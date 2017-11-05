package com.invoice;

public class Config {
	
	private String CartridgePath;
	private String DeliveryPath;
	private String InvoiceCreationPath;
	
	public String getCartridgePath() {
		return CartridgePath;
	}
	public void setCartridgePath(String cartridgePath) {
		CartridgePath = cartridgePath;
	}
	public String getDeliveryPath() {
		return DeliveryPath;
	}
	public void setDeliveryPath(String deliveryPath) {
		DeliveryPath = deliveryPath;
	}
	public String getInvoiceCreationPath() {
		return InvoiceCreationPath;
	}
	public void setInvoiceCreationPath(String invoiceCreationPath) {
		InvoiceCreationPath = invoiceCreationPath;
	}
	
}
