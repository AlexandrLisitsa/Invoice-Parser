package com.invoice.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.invoice.calendar.InvoiceCalendar;
import com.invoice.client.Addition;
import com.invoice.client.Client;

public class AdditionParse {

	private int clientCellIndex = 0;
	private int accruedCellIndex = 1;
	private int paydCellIndex = 2;

	public void parseDebt(ArrayList<Client> clients, String path) {

		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "������ ���� � ������");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("���� �����");
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					is = new FileInputStream(new File(fc.getSelectedFile().getAbsolutePath().replace("\\", "\\\\")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Workbook wb = null;
		try {
			wb = new HSSFWorkbook(is);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Sheet sheet = wb.getSheet("�����");
		Iterator<Row> it = sheet.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			try {
				for (int i = 0; i < clients.size(); i++) {

					if (row.getCell(clientCellIndex).toString().equalsIgnoreCase(clients.get(i).getClientName())) {
						clients.get(i).setAccrued(Double.parseDouble((row.getCell(accruedCellIndex).toString())));
						clients.get(i).setPayd(Double.parseDouble((row.getCell(paydCellIndex).toString())));
						break;
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void parseAdditions(ArrayList<Client> clients, String path) {
		int dateI = 0;
		int clientI = 1;
		int descriptionI = 2;
		int countI = 3;
		int costI = 4;

		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "������ ���� � ������");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("���� �����");
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				try {
					is = new FileInputStream(new File(fc.getSelectedFile().getAbsolutePath().replace("\\", "\\\\")));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Workbook wb = null;
		try {
			wb = new HSSFWorkbook(is);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
					}
		Sheet sheet = getMonthSheet(wb);
		Iterator<Row> it=null;
		try{
			it=sheet.iterator();
			it.hasNext();
		}catch (Exception e) {
			Calendar c = Calendar.getInstance();
			JOptionPane.showMessageDialog(null, "�� ������ ���� ����� �� "+InvoiceCalendar.getMonth(c.get(Calendar.MONTH)-1, "rus"));
			System.exit(0);
		}
		
		while (it.hasNext()) {
			Row row = it.next();
			for (int i = 0; i < clients.size(); i++) {
				try {
					if (row.getCell(clientI).toString().equalsIgnoreCase(clients.get(i).getClientName())) {
						Client c = clients.get(i);
						c.getAdditions().add(new Addition());
						Addition ad = c.getAdditions().get(c.getAdditions().size() - 1);
						ad.setDate(row.getCell(dateI).toString());
						ad.setDescription(row.getCell(descriptionI).toString());
						ad.setCount(Double.parseDouble(row.getCell(countI).toString()));
						ad.setCostUAH(Double.parseDouble(row.getCell(costI).toString()));
					}
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	private Sheet getMonthSheet(Workbook book) {
		Calendar calendar =	Calendar.getInstance();
		for(int i=0;i<book.getNumberOfSheets();i++) {
			Sheet sheetIT = book.getSheetAt(i);
			String fMonth = sheetIT.getSheetName().toLowerCase();
			String sMonth = InvoiceCalendar.getMonth(calendar.get(Calendar.MONTH)-1, "rus").toLowerCase();	
			if(fMonth.contains(sMonth)) {
				return sheetIT;				
			}
		}
		return null;
	}
}
