package com.invoice.creator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.invoice.client.Client;
import com.invoice.client.Delivery;
import com.invoice.client.ReloadCartridge;
import com.invoice.month.Months;

public class DOCXcreator {

	private static final int NUMBER_CELL_SUZE = 500;
	private static final int DESCRIPTION_CELL_SIZE = 6000;
	private static final int OTHER_CELL_SIZE = 1000;

	private XWPFDocument doc;
	private XWPFRun run;
	private XWPFParagraph paragraph;
	private FileInputStream is;
	private Calendar calendar = Calendar.getInstance();
	private Client client;
	private XWPFTable table;

	// test

	// test

	public void createInvoice(Client client) {
		this.client = client;
		doc = new XWPFDocument();
		createActHead();
		createActTitle();
		ctrateActTable();
		save();
	}

	private void ctrateActTable() {
		createTableDebt();
		createTableServices();
		createTableAdditions();
	}

	private void createTableServices() {
		createTableTitle("������");
		formatTable();
		//�������� ������� ��������
		table = doc.createTable(client.getServices().size(), 5);
		formatTable();
		for(int row=0;row<client.getServices().size();row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row+1));
			table.getRow(row).getCell(1).setText(client.getServices().get(row).getDescription());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(client.getServices().get(row).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(client.getServices().get(row).getPrice()));
			table.getRow(row).getCell(4).setText(getFormatedDecimal(client.getServices().get(row).getPrice()*client.getServices().get(row).getCount()));
		}
	}

	private void createTableAdditions() {
		// �������� ��������� �������
		ArrayList<ReloadCartridge> cartridge = client.getCartridges();
		ArrayList<Delivery> deliveries = client.getDeliveries();
		int row = 0;
		
		createTableTitle("�������������� ������:");
		formatTable();

		// �������� ������� �����/��������/��������
		table = doc.createTable(client.getDeliveries().size() + client.getCartridges().size(), 5);
		formatTable();
		// ���������� ������� �����/��������/��������

		// ��������
		for (int c = row; c < client.getCartridges().size(); c++, row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
			table.getRow(row).getCell(1).setText(cartridge.get(c).getDate() + " " + cartridge.get(c).getPrinter() + " "
					+ cartridge.get(c).getWorkDescription());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(cartridge.get(c).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(cartridge.get(c).getCostUAH()+25));
			table.getRow(row).getCell(4).setText(getFormatedDecimal(
					cartridge.get(c).getCount() * cartridge.get(c).getCostUAH() + (25 * cartridge.get(c).getCount())));
		}
		// ��������
		for (int d = 0; d < client.getDeliveries().size(); d++, row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
			table.getRow(row).getCell(1).setText(deliveries.get(d).getDate() + " " + deliveries.get(d).getGoods());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(deliveries.get(d).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(deliveries.get(d).getCostUAH() * 1.1));
			table.getRow(row).getCell(4)
					.setText(getFormatedDecimal(deliveries.get(d).getCount() * (deliveries.get(d).getCostUAH() * 1.1)));
		}
	}

	private String getFormatedDecimal(double x) {
		String str;
		str = String.valueOf(x);
		if (str.endsWith(".0")) {
			return str.substring(0, str.indexOf("."));
		} else {
			try {
				return str.substring(0, str.indexOf(".") + 3);
			} catch (Exception e) {
				return str;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void createTableDebt() {
		List<XWPFParagraph> parList;
		table = doc.createTable(3, 5);

		int prevMonth = getPrevMonth() - 1;
		int currMonth = calendar.getTime().getMonth() - 1;
		int nextMonth = getNextMonth() - 1;

		for (int row = 0; row < table.getNumberOfRows(); row++) {
			for (int col = 0; col < 5; col++) {
				if (col == 0) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(NUMBER_CELL_SUZE));
				} else if (col == 1) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(DESCRIPTION_CELL_SIZE));
					if (row == 0) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("��������� ��");
					} else if (row == 1) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("�������� �");
					} else if (row == 2) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("������������� ��");
					}
				} else if (col == 2) {
					if (row == 0) {
						table.getRow(row).getCell(col).setText(Months.getMonth(prevMonth, "rus"));
					}
					if (row == 1) {
						table.getRow(row).getCell(col).setText(Months.getMonth(currMonth, "rus"));
					}
					if (row == 2) {
						table.getRow(row).getCell(col).setText(Months.getMonth(nextMonth, "rus"));
					}
				} else {
					parList = table.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(OTHER_CELL_SIZE));
				}
			}
		}
		table.getRow(0).getCell(4).setText(getFormatedDecimal(client.getAccrued()));
		table.getRow(1).getCell(4).setText(getFormatedDecimal(client.getPayd()));
		table.getRow(2).getCell(4).setText(getFormatedDecimal(client.getAccrued() - client.getPayd()));
		doc.createParagraph();
	}

	@SuppressWarnings("deprecation")
	private int getPrevMonth() {
		if (calendar.getTime().getMonth() == 0) {
			return 11;
		} else {
			return calendar.getTime().getMonth() - 1;
		}
	}

	@SuppressWarnings("deprecation")
	private int getNextMonth() {
		if (calendar.getTime().getMonth() == 11) {
			return 0;
		} else {
			return calendar.getTime().getMonth() + 1;
		}
	}

	@SuppressWarnings("deprecation")
	private void createActTitle() {
		// ������� ������
		paragraph = doc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontSize(16);
		run.setFontFamily("times new roman");
		run.setBold(true);
		run.setText(client.getUpperActTitle() + "/" + String.valueOf(Integer.valueOf(incrimentMonth())) + "-"
				+ String.valueOf(calendar.getTime().getYear()).substring(1, 3));
		// ������ ������
		paragraph = doc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontSize(16);
		run.setFontFamily("times new roman");
		run.setBold(true);
		run.setText(client.getLowerActTitle() + Months.getMonth(calendar.getTime().getMonth() - 1, "rus") + " "
				+ calendar.get(Calendar.YEAR) + " �.");
	}

	private String incrimentMonth() {
		if (calendar.get(Calendar.MONTH) + 1 > 12) {
			return "1";
		} else {
			return String.valueOf(calendar.get(Calendar.MONTH) + 1);
		}
	}

	private void createActHead() {
		paragraph = doc.createParagraph();
		run = paragraph.createRun();
		String imagePath = "res/logo.png";

		try {
			is = new FileInputStream(imagePath);
			try {
				run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(460), Units.toEMU(53));
			} catch (InvalidFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void save() {
		try {
			doc.write(new FileOutputStream(new File(client.getClientName() + ".docx")));
			doc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void formatTable() {
		List<XWPFParagraph> parList;
		for (int row = 0; row < table.getNumberOfRows(); row++) {
			for (int col = 0; col < 5; col++) {
				if (col == 0) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(NUMBER_CELL_SUZE));
				} else if (col == 1) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(DESCRIPTION_CELL_SIZE));
					parList = table.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);

				} else {
					parList = table.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(OTHER_CELL_SIZE));
				}
			}
		}
	}
	
	private void createTableTitle(String description) {
		table = doc.createTable(1, 5);
		for (int i = 0; i < 5; i++) {
			List<XWPFParagraph> tpar = table.getRow(0).getCell(i).getParagraphs();
			tpar.get(0).setVerticalAlignment(TextAlignment.CENTER);
			run = tpar.get(0).createRun();
			run.setBold(true);
			run.setFontFamily("times new roman");
			if (i == 0) {
				run.setText("�");
			} else if (i == 1) {
				run.setText(description);
			} else if (i == 2) {
				run.setText("���-��");
			} else if (i == 3) {
				run.setText("����,���");
			} else if (i == 4) {
				run.setText("���������,���");
			}
		}
		formatTable();
	}
}
