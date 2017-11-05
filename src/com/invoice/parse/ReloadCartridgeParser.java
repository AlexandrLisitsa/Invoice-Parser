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
import com.invoice.ReloadCartridge;
import com.invoice.visual.ProgressPage;

public class ReloadCartridgeParser {
	
	private int invoiceCellCount;
	private int dataCellIndex;
	private int clientCellIndex;
	private int printerCellIndex;
	private int workCellIndex;
	private int countCellIndex;
	private int priceCellIndex;
	private int clientIndex;
	private ProgressPage bar;
	private int barCounter;
	
	public ReloadCartridgeParser(ProgressPage bar) {
		this.bar=bar;
	}

	public void parse(ArrayList<Client> clients, String path) {

		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Выбери файл с заправками");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Файл заправок");
			if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				try {
					is = new FileInputStream(new File(fc.getSelectedFile().getAbsolutePath().replace("\\","\\\\")));
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
			barCounter++;
			Row row = it.next();
			Iterator<Cell> cells = row.iterator();
			while (cells.hasNext()) {
				Cell cell = cells.next();
				try {
					bar.updateBar(sheet.getLastRowNum(),barCounter,"Parsing Cartridges",false);
					createIndexes(cell);
					if (cell.getStringCellValue().equals("да") && cell.getColumnIndex() == invoiceCellCount) {
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).getClientName()
									.equalsIgnoreCase((row.getCell(clientCellIndex).toString()))) {
								clientIndex = i;
								break;
							}
						}
						addNewCartridge(row, clients);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	
	private void addNewCartridge(Row row, ArrayList<Client> clients) {
		clients.get(clientIndex).getCartridges().add(new ReloadCartridge());
		ReloadCartridge tmp = clients.get(clientIndex).getCartridges().get(clients.get(clientIndex).getCartridges().size()-1);
		tmp.setDate(row.getCell(dataCellIndex).toString());
		tmp.setPrinter((row.getCell(printerCellIndex).toString()));
		tmp.setWorkDescription(row.getCell(workCellIndex).toString());
		tmp.setCount((Double.parseDouble(row.getCell(countCellIndex).toString())));
		tmp.setCostUAH((Double.parseDouble(row.getCell(priceCellIndex).toString())));
		tmp.setTotal(tmp.getCount()*tmp.getCostUAH());
	}

	private void createIndexes(Cell cell) {
		if (cell.getStringCellValue().equals("Дата")) {
			dataCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("клиент")) {
			clientCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Принтер")) {
			printerCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Работа")) {
			workCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Кол-во")) {
			countCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("Цена")) {
			priceCellIndex = cell.getColumnIndex();
		}
		if (cell.getStringCellValue().equals("В счет")) {
			invoiceCellCount = cell.getColumnIndex();
		}
	}

}
