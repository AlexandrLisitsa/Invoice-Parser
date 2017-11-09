package com.invoice.creator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.invoice.client.Client;
import com.invoice.client.Delivery;
import com.invoice.client.ReloadCartridge;
import com.invoice.visual.ProgressPage;

public class InvoiceBuilder {
	
	private String extension =".xls";
	private String clientName;
	private int rowCount=0;
	private int cartridgeCount=1;
	private ProgressPage bar;
	private StringBuilder sb;
	private int currentClientIndex;
	
	public InvoiceBuilder(ProgressPage bar) {
		this.bar=bar;
	}
	
	public void create(ArrayList<Client> clients,String path) {
		bar.initExtBar();
		for (Client x : clients) {
			currentClientIndex++;
			bar.updateBar(clients.size(), currentClientIndex, "Build Invoice: "+x.getClientName(),true);
			clientName=x.getClientName();
			if(clientName==null)continue;
			Workbook wb = new HSSFWorkbook();
			Sheet st = wb.createSheet("Invoice");
			for (Delivery del : x.getDeliveries()) {
				Row row = st.createRow(rowCount);
				st.autoSizeColumn(1);
				sb = new StringBuilder();
				sb.append(del.getDate()+" ");
				sb.append(del.getGoods());
				
				Cell cell = row.createCell(0);
				cell.setCellValue(sb.toString());
				cell = row.createCell(1);
				cell.setCellValue(del.getCount());
				cell = row.createCell(2);
				cell.setCellValue(del.getCostUAH());
				cell = row.createCell(3);
				cell.setCellValue(del.getCount()*del.getCostUAH());
				
				rowCount++;
				
				bar.updateExtendetBar(x.getDeliveries().size(), rowCount, "Deliveries");						
			}
			for (ReloadCartridge cartridge : x.getCartridges()) {
				Row row = st.createRow(rowCount);
				st.autoSizeColumn(1);
				sb = new StringBuilder();
				sb.append(cartridge.getDate()+" ");
				sb.append(cartridge.getPrinter()+" ");
				sb.append(cartridge.getWorkDescription());
				
				Cell cell = row.createCell(0);
				cell.setCellValue(sb.toString());
				cell = row.createCell(1);
				cell.setCellValue(cartridge.getCount());
				cell = row.createCell(2);
				cell.setCellValue(cartridge.getCostUAH());
				cell = row.createCell(3);
				cell.setCellValue(cartridge.getCount()*cartridge.getCostUAH());
				
				rowCount++;
				bar.updateExtendetBar(x.getCartridges().size(), cartridgeCount++, "Cartridges");				
			}
			try {
				wb.write(new FileOutputStream(path+clientName+extension));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					wb.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			rowCount=0;
			cartridgeCount=1;
		}
	}

}
