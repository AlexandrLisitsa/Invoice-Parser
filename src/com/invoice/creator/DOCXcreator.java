package com.invoice.creator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;

import com.invoice.calendar.InvoiceCalendar;
import com.invoice.client.Addition;
import com.invoice.client.Client;
import com.invoice.client.Delivery;
import com.invoice.client.ReloadCartridge;

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
	private XWPFTable table, requisiteTable;

	public void createInvoice(Client client) {
		this.client = client;
		doc = new XWPFDocument();
		createActHead();
		createActTitle();
		ctrateActTable();
		createActRequisite();
		save();
	}

	private void ctrateActTable() {
		createTableDebt();
		createTableServices();
		createTableDiscount();
		if (client.getCartridges().size() + client.getDeliveries().size() + client.getAdditions().size() != 0) {
			createTableAdditions();
		}
		createTableTotal();
	}

	private void createTableTotal() {
		createTableTitle(null, "ИТОГО", null, null, null, ParagraphAlignment.CENTER);
		table.getRow(0).getCell(4).getParagraphs().get(0).getRuns().get(0).setBold(true);
		table.getRow(0).getCell(4).getParagraphs().get(0).getRuns().get(0).setText(getFormatedDecimal(createTotal()));
		formatTable(table, ParagraphAlignment.CENTER);
	}

	private double createTotal() {
		double x = 0;
		x += client.getTotalServersCost()
				+ (client.getTotalServiceCost() - (client.getTotalServiceCost() * (client.getDiscount() / 100)));
		x += client.getTotalCartridgeCostWith25();
		x += client.getTotalDeliveriesCostWith10Percent();
		x += client.getDebt();
		x += client.getTotalAdditionsCost();
		return x;
	}

	private void createTableDiscount() {
		if (client.getDiscount() > 0) {
			createTableTitle(null, "Скидка на услуги(без аренды серверов): " + client.getDiscount() + " %", null, null,
					null, ParagraphAlignment.CENTER);
			table.getRow(0).getCell(2).setText("Итого");
			table.getRow(0).getCell(4).setText(getFormatedDecimal(client.getTotalServersCost()
					+ (client.getTotalServiceCost() - (client.getTotalServiceCost() * (client.getDiscount() / 100)))));
			formatTable(table, ParagraphAlignment.CENTER);
		}
	}

	private void createTableServices() {
		if (client.getServices().size() > 0||client.getServers().size()>0) {
			createTableTitle("№", "Услуги:", "Кол-во", "Цена,грн", "Стоимость,грн", ParagraphAlignment.CENTER);
			formatTable(table, ParagraphAlignment.CENTER);

			int row = 0;

			// создание таблицы сервисов
			table = doc.createTable(client.getServices().size() + client.getServers().size(), 5);
			formatTable(table, ParagraphAlignment.LEFT);
			for (int i = row; row < client.getServices().size(); i++, row++) {
				table.getRow(row).getCell(0).setText(String.valueOf(i + 1));
				table.getRow(row).getCell(1).setText(client.getServices().get(i).getDescription());
				table.getRow(row).getCell(2).setText(getFormatedDecimal(client.getServices().get(i).getCount()));
				table.getRow(row).getCell(3).setText(getFormatedDecimal(client.getServices().get(i).getPrice()));
				table.getRow(row).getCell(4).setText(getFormatedDecimal(
						(client.getServices().get(i).getPrice() * client.getServices().get(row).getCount())));
			}
			for (int i = 0; i < client.getServers().size(); i++, row++) {
				table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
				table.getRow(row).getCell(1).setText(client.getServers().get(i).getDescription());
				table.getRow(row).getCell(2).setText(getFormatedDecimal(client.getServers().get(i).getCount()));
				table.getRow(row).getCell(3).setText(getFormatedDecimal(client.getServers().get(i).getPrice()));
				table.getRow(row).getCell(4).setText(getFormatedDecimal(
						(client.getServers().get(i).getPrice() * client.getServers().get(i).getCount())));
			}
		}
	}

	private void createTableAdditions() {
		// создание заголовка таблицы
		ArrayList<ReloadCartridge> cartridge = client.getCartridges();
		ArrayList<Delivery> deliveries = client.getDeliveries();
		ArrayList<Addition> additions = client.getAdditions();
		int row = 0;

		createTableTitle("№", "Дополнительные услуги:", "Кол-во", "Цена,грн", "Стоимость,грн",
				ParagraphAlignment.CENTER);
		formatTable(table, ParagraphAlignment.CENTER);

		// создание таблицы допов/поставок/заправок
		table = doc.createTable(
				client.getDeliveries().size() + client.getCartridges().size() + client.getAdditions().size(), 5);
		formatTable(table, ParagraphAlignment.LEFT);
		// заполнение таблицы допов/поставок/заправок

		// заправки
		for (int c = row; c < client.getCartridges().size(); c++, row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
			table.getRow(row).getCell(1).setText(cartridge.get(c).getDate() + " " + cartridge.get(c).getPrinter() + " "
					+ cartridge.get(c).getWorkDescription());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(cartridge.get(c).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(cartridge.get(c).getCostUAH() + 25));
			table.getRow(row).getCell(4).setText(getFormatedDecimal(
					cartridge.get(c).getCount() * cartridge.get(c).getCostUAH() + (25 * cartridge.get(c).getCount())));
		}
		// поставки
		for (int d = 0; d < client.getDeliveries().size(); d++, row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
			table.getRow(row).getCell(1).setText(deliveries.get(d).getDate() + " " + deliveries.get(d).getGoods());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(deliveries.get(d).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(deliveries.get(d).getCostUAH() * 1.1));
			table.getRow(row).getCell(4)
					.setText(getFormatedDecimal(deliveries.get(d).getCount() * (deliveries.get(d).getCostUAH() * 1.1)));
		}
		// допы
		for (int dop = 0; dop < client.getAdditions().size(); dop++, row++) {
			table.getRow(row).getCell(0).setText(String.valueOf(row + 1));
			table.getRow(row).getCell(1)
					.setText(additions.get(dop).getDate() + " " + additions.get(dop).getDescription());
			table.getRow(row).getCell(2).setText(getFormatedDecimal(additions.get(dop).getCount()));
			table.getRow(row).getCell(3).setText(getFormatedDecimal(additions.get(dop).getCostUAH()));
			table.getRow(row).getCell(4)
					.setText(getFormatedDecimal(additions.get(dop).getCount() * (additions.get(dop).getCostUAH())));
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

	private void createTableDebt() {
		List<XWPFParagraph> parList;
		table = doc.createTable(3, 5);

		int prevMonth = getPrevMonth();
		int currMonth = getCurrentMonth();
		int nextMonth = getNextMonth();

		for (int row = 0; row < table.getNumberOfRows(); row++) {
			for (int col = 0; col < 5; col++) {
				if (col == 0) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(NUMBER_CELL_SUZE));
				} else if (col == 1) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(5455));
					if (row == 0) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("Начислено за");
					} else if (row == 1) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("Оплачено в");
					} else if (row == 2) {
						parList = table.getRow(row).getCell(col).getParagraphs();
						parList.get(0).setAlignment(ParagraphAlignment.CENTER);
						parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
						table.getRow(row).getCell(col).setText("Задолженность на");
					}
				} else if (col == 2) {
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(905));
					if (row == 0) {
						table.getRow(row).getCell(col).setText(InvoiceCalendar.getMonth(prevMonth, "rus"));
					}
					if (row == 1) {
						table.getRow(row).getCell(col).setText(InvoiceCalendar.getMonth(currMonth, "rus"));
					}
					if (row == 2) {
						table.getRow(row).getCell(col).setText(InvoiceCalendar.getMonth(nextMonth, "rus"));
					} else if (col == 4) {

					}
				} else if (col == 4) {
					parList = table.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
					table.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
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

	private int getPrevMonth() {
		if (calendar.get(Calendar.MONTH) == 0) {
			return 10;
		} else if (calendar.get(Calendar.MONTH) == 1) {
			return 11;
		} else {
			return calendar.get(Calendar.MONTH) - 2;
		}
	}

	private int getCurrentMonth() {
		if (calendar.get(Calendar.MONTH) == 0) {
			return 11;
		} else {
			return calendar.get(Calendar.MONTH) - 1;
		}
	}

	private int getNextMonth() {
		return calendar.get(Calendar.MONTH);
	}

	@SuppressWarnings("deprecation")
	private void createActTitle() {
		// верхняя строка
		paragraph = doc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontSize(16);
		run.setFontFamily("times new roman");
		run.setBold(true);
		run.setText(client.getUpperActTitle() + "/" + String.valueOf(Integer.valueOf(incrimentMonth())) + "-"
				+ String.valueOf(calendar.getTime().getYear()).substring(1, 3));
		// нижняя строка
		paragraph = doc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontSize(16);
		run.setFontFamily("times new roman");
		run.setBold(true);
		run.setText(client.getLowerActTitle() + " за " + InvoiceCalendar.getMonth(calendar.getTime().getMonth() - 1, "rus") + " "
				+ Integer.valueOf(calendar.get(Calendar.YEAR)) + " г.");
		// акт является счетом
		if (!client.isAct()) {
			paragraph = doc.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run = paragraph.createRun();
			run.setFontSize(9);
			run.setText("Счет является актом выполненных работ.Стороны претензий не имеют");
		}
	}

	private String incrimentMonth() {
		if (calendar.get(Calendar.MONTH) + 1 > 12) {
			return "1";
		} else {
			return String.valueOf(calendar.get(Calendar.MONTH) + 1);
		}
	}

	/*
	 * private void createActRequisite() { paragraph = doc.createParagraph(); run =
	 * paragraph.createRun(); String imagePath = client.getRequisiteImgPath();
	 * BufferedImage img = null; try { img = ImageIO.read(new
	 * File(client.getRequisiteImgPath())); } catch (IOException e2) { // TODO
	 * Auto-generated catch block // e2.printStackTrace();
	 * JOptionPane.showMessageDialog(null, "Не заполнены реквизиты " +
	 * client.getClientName()); } try { is = new FileInputStream(imagePath); try {
	 * run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, imagePath,
	 * Units.toEMU(img.getWidth() * 0.7), Units.toEMU(img.getHeight() * 0.7)); }
	 * catch (InvalidFormatException | IOException e) { // TODO Auto-generated catch
	 * block // e.printStackTrace(); } } catch (FileNotFoundException e1) { // TODO
	 * Auto-generated catch block e1.printStackTrace(); } finally { try {
	 * is.close(); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } }
	 * 
	 */

	private void createActRequisite() {
		doc.createParagraph();
		// создаем таблицу реквизитов
		requisiteTable = doc.createTable(1, 2);
		requisiteTable.getCTTbl().setTblPr(getExampleCTTblPr());
		// заполняем реквизиты клиенты
		if (client.getRequisites().size() != 0) {
			requisiteTable.getRow(0).getCell(0).removeParagraph(0);
			for (String x : client.getRequisites()) {
				if (x.indexOf(0) != ' ') {
					requisiteTable.getRow(0).getCell(0).addParagraph().createRun().setText(x);
				}
			}
		}
		// наши реквизиты
		if (client.getOur_requisites().size() != 0) {
			requisiteTable.getRow(0).getCell(1).removeParagraph(0);
			for (String x : client.getOur_requisites()) {
				requisiteTable.getRow(0).getCell(1).addParagraph().createRun().setText(x);
			}
		}
		setTableFontSize(requisiteTable, 1, 2, 10);
	}

	private void setTableFontSize(XWPFTable table, int row, int col, int fontSize) {
		for (int rows = 0; rows < row; rows++) {
			for (int cols = 0; cols < col; cols++) {
				List<XWPFParagraph> tPar = table.getRow(rows).getCell(cols).getParagraphs();
				for (XWPFParagraph x : tPar) {
					x.setSpacingAfter(0);
					x.setSpacingAfterLines(0);
					x.setSpacingBefore(0);
					x.setSpacingBeforeLines(0);
					x.setSpacingBetween(0.9);
					List<XWPFRun> tRun = x.getRuns();
					for (XWPFRun runs : tRun) {
						runs.setFontFamily("times new roman");
						runs.setFontSize(fontSize);
					}
				}
			}
		}
	}

	// метод для подгрузки стиля таблицы
	private CTTblPr getExampleCTTblPr() {
		InputStream is = null;
		try {
			is = new FileInputStream(new File("res/requisiteExample.docx"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XWPFDocument tdoc = null;
		try {
			tdoc = new XWPFDocument(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tdoc.getTableArray(0).getCTTbl().getTblPr();
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

			int month = calendar.get(Calendar.MONTH) + 1;
			doc.write(new FileOutputStream(
					//new File("C:\\Users\\wypik\\Desktop\\invoices\\DOCX\\" + month + "_" + client.getClientName() + ".docx"))
					new File(client.getLocation()+month+"_"+ client.getClientName() + ".docx"))
			);
			doc.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void formatTable(XWPFTable tmpTable, ParagraphAlignment alignment) {
		List<XWPFParagraph> parList;
		for (int row = 0; row < tmpTable.getNumberOfRows(); row++) {
			for (int col = 0; col < 5; col++) {
				if (col == 0) {
					tmpTable.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(NUMBER_CELL_SUZE));
				} else if (col == 1) {
					tmpTable.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(DESCRIPTION_CELL_SIZE));
					parList = tmpTable.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(alignment);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);

				} else {
					parList = tmpTable.getRow(row).getCell(col).getParagraphs();
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
					parList.get(0).setVerticalAlignment(TextAlignment.CENTER);
					tmpTable.getRow(row).getCell(col).getCTTc().addNewTcPr().addNewTcW()
							.setW(BigInteger.valueOf(OTHER_CELL_SIZE));
				}
			}
		}
	}

	private void createTableTitle(String number, String description, String count, String price, String totalCost,
			ParagraphAlignment align) {
		table = doc.createTable(1, 5);
		for (int i = 0; i < 5; i++) {
			List<XWPFParagraph> tpar = table.getRow(0).getCell(i).getParagraphs();
			tpar.get(0).setVerticalAlignment(TextAlignment.CENTER);
			run = tpar.get(0).createRun();
			run.setBold(true);
			run.setFontFamily("times new roman");
			if (i == 0) {
				run.setText(number);
			} else if (i == 1) {
				run.setText(description);
			} else if (i == 2) {
				run.setText(count);
			} else if (i == 3) {
				run.setText(price);
			} else if (i == 4) {
				run.setText(totalCost);
			}
		}
		formatTable(table, align);
	}
}
