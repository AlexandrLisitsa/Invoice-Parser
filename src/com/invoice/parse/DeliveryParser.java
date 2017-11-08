package com.invoice.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.invoice.Client;
import com.invoice.Delivery;
import com.invoice.visual.ProgressPage;

public class DeliveryParser {

	private int invoiceCellCount;
	private int dataCellIndex;
	private int clientCellIndex;
	private int goodsCellIndex;
	private int countCellIndex;
	private int usdCellIndex;
	private int courseCellIndex;
	private int clientIndex;
	private int barCounter = 0;
	private ProgressPage bar;

	public DeliveryParser(ProgressPage bar) {
		this.bar = bar;
	}

	public void parse(ArrayList<Client> clients, String path) {

		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Выбери файл с поставками");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Файл поставок");
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
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> it = sheet.iterator();
		while (it.hasNext()) {
			Row row = it.next();
			barCounter++;
			Iterator<Cell> cells = row.iterator();
			while (cells.hasNext()) {
				Cell cell = cells.next();
				try {
					bar.updateBar(sheet.getLastRowNum(), barCounter, "Parsing Deliveries", false);
					createIndexes(cell);
					if (cell.getStringCellValue().equalsIgnoreCase("да") && cell.getColumnIndex() == invoiceCellCount) {
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).getClientName()
									.equalsIgnoreCase((row.getCell(clientCellIndex).toString()))) {
								clientIndex = i;
								addNewDelivery(row, clients);
								break;
							}
						}				
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void addNewDelivery(Row row, ArrayList<Client> clients) {
		clients.get(clientIndex).getDeliveries().add(new Delivery());
		Delivery tmp = clients.get(clientIndex).getDeliveries()
				.get(clients.get(clientIndex).getDeliveries().size() - 1);
		tmp.setDate(row.getCell(dataCellIndex).toString());
		tmp.setGoods(row.getCell(goodsCellIndex).toString());
		tmp.setCount(Double.parseDouble(row.getCell(countCellIndex).toString()));
		tmp.setCostUSD(Double.parseDouble(row.getCell(usdCellIndex).toString()));
		tmp.setCourse(Double.parseDouble(row.getCell(courseCellIndex).toString()));
		tmp.setCostUAH(tmp.getCourse() * tmp.getCostUSD());
	}

	private void createIndexes(Cell cell) {
		if (cell.getStringCellValue().equals("Дата")) {
			dataCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("клиент")) {
			clientCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Товар")) {
			goodsCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Кол-во")) {
			countCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Цена $")) {
			usdCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Курс")) {
			courseCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("В счет")) {
			invoiceCellCount = cell.getColumnIndex();
		}
	}
}
