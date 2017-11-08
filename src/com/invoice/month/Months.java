package com.invoice.month;

public class Months {

	public static String getMonth(int month, String language) {

		String monthsRUS[] = { "������", "�������", "����", "������", "���", "����", "����", "������", "��������",
				"�������", "������", "�������" };

		String monthsUKR[] = { "ѳ����", "�����", "��������", "������", "�������", "�������", "������", "�������",
				"��������", "�������", "��������", "�������" };

		if (language.equals("rus")) {
			return monthsRUS[month];
		} else if (language.equals("ukr")) {
			return monthsUKR[month];
		} else {
			return "�� ����� ������ ����";
		}
	}

}
