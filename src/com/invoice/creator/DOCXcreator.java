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

import com.invoice.client.Addition;
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
		createTableTitle(null, "ИТОГО", null, null, null);
		table.getRow(0).getCell(4).getParagraphs().get(0).getRuns().get(0).setBold(true);
		table.getRow(0).getCell(4).getParagraphs().get(0).getRuns().get(0).setText(getFormatedDecimal(createTotal()));
		formatTable(table);
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
			createTableTitle(null, "Скидка на услуги(без серверов): " + client.getDiscount() + " %", null, null, null);
			table.getRow(0).getCell(2).setText("Итого");
			table.getRow(0).getCell(4).setText(getFormatedDecimal(client.getTotalServersCost()
					+ (client.getTotalServiceCost() - (client.getTotalServiceCost() * (client.getDiscount() / 100)))));
			formatTable(table);
		}
	}

	private void createTableServices() {
		if (client.getServices().size() > 0) {
			createTableTitle("№", "Услуги:", "Кол-во", "Цена,грн", "Стоимость,грн");
			formatTable(table);

			int row = 0;

			// создание таблицы сервисов
			table = doc.createTable(client.getServices().size() + client.getServers().size(), 5);
			formatTable(table);
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

		createTableTitle("№", "Дополнительные услуги:", "Кол-во", "Цена,грн", "Стоимость,грн");
		formatTable(table);

		// создание таблицы допов/поставок/заправок
		table = doc.createTable(
				client.getDeliveries().size() + client.getCartridges().size() + client.getAdditions().size(), 5);
		formatTable(table);
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
		run.setText(client.getLowerActTitle() + Months.getMonth(calendar.getTime().getMonth() - 1, "rus") + " "
				+ calendar.get(Calendar.YEAR) + " г.");
		// акт является счетом
		paragraph = doc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontSize(9);
		run.setText("Счет является актом выполненных работ.Стороны претензий не имеют");
	}

	private String incrimentMonth() {
		if (calendar.get(Calendar.MONTH) + 1 > 12) {
			return "1";
		} else {
			return String.valueOf(calendar.get(Calendar.MONTH) + 1);
		}
	}

	private void createActRequisite() {
		paragraph = doc.createParagraph();
		run = paragraph.createRun();
		String imagePath = "res/Оптидея.png";

		try {
			is = new FileInputStream(imagePath);
			try {
				run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(460), Units.toEMU(93));
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

	private void formatTable(XWPFTable tmpTable) {
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
					parList.get(0).setAlignment(ParagraphAlignment.CENTER);
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

	private void createTableTitle(String number, String description, String count, String price, String totalCost) {
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
		formatTable(table);
	}
}
