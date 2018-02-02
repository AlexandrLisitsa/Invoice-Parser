package com.invoice.calendar;

import java.util.Calendar;

public class InvoiceCalendar {

	public static String getMonth(int month, String language) {

		String monthsRUS[] = { "������", "�������", "����", "������", "���", "����", "����", "������", "��������",
				"�������", "������", "�������" };

		String monthsUKR[] = { "ѳ����", "�����", "��������", "������", "�������", "�������", "������", "�������",
				"��������", "�������", "��������", "�������" };

		if (language.equals("rus")) {
			if (month < 0) {
				return monthsRUS[monthsRUS.length-1];
			} else {
				return monthsRUS[month];
			}
		} else if (language.equals("ukr")) {
			return monthsUKR[month];
		} else {
			return "�� ����� ������ ����";
		}
	}

	public static String getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		return getMonth(c.get(Calendar.MONTH), "rus");
	}

}
