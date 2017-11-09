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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.invoice.client.Client;

public class AdditionParse {
	
	private int clientCellIndex=0;
	private int accruedCellIndex=1;
	private int paydCellIndex=2;
		
	public void parse(ArrayList<Client> clients, String path) {

		FileInputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Выбери файл с допами");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Файл допов");
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
			for(int i=0;i<clients.size();i++) {
				if(row.getCell(clientCellIndex).toString().equalsIgnoreCase(clients.get(i).getClientName())) {
					clients.get(i).setAccrued(Double.parseDouble((row.getCell(accruedCellIndex).toString())));
					clients.get(i).setPayd(Double.parseDouble((row.getCell(paydCellIndex).toString())));
				}
			}
		}
	}
}
