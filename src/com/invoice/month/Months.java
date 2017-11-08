package com.invoice.month;

public class Months {

	public static String getMonth(int month, String language) {

		String monthsRUS[] = { "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь",
				"Октябрь", "Ноябрь", "Декабрь" };

		String monthsUKR[] = { "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень", "Липень", "Серпень",
				"Вересень", "Жовтень", "Листопад", "Грудень" };

		if (language.equals("rus")) {
			return monthsRUS[month];
		} else if (language.equals("ukr")) {
			return monthsUKR[month];
		} else {
			return "не верно выбран язык";
		}
	}

}
