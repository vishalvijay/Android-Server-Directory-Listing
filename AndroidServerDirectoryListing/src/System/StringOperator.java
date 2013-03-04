package System;

import java.util.Locale;

public class StringOperator {
	private static String toNameFormate(String name, int lower) {
		if ("".equals(name)) {
			return name;
		}
		if (lower == 1) {
			name = name.toLowerCase(Locale.ENGLISH);
		}
		name = name.replaceFirst(Character.toString(name.charAt(0)),
				Character.toString(name.toUpperCase(Locale.ENGLISH).charAt(0)));
		return name;
	}

	public static String toFullNameFormate(String name) {
		name = name.toLowerCase(Locale.ENGLISH);
		String tempText[] = name.split("\\ ");
		name = "";
		for (int i = 0; i < tempText.length; i++) {
			name += toNameFormate(tempText[i], 1) + " ";
		}
		String tempText2[] = name.split("\\.");
		name = "";
		for (int i = 0; i < tempText2.length; i++) {
			name += toNameFormate(tempText2[i], 0) + ".";
		}
		name = name.substring(0, name.length() - 2);
		return name;
	}

	public static boolean isValidName(String name) {
		if (name.matches("[a-zA-Z][ .a-zA-Z]*"))
			return true;
		else
			return false;

	}

	public static boolean isPhoneNumber(String phoneNumber) {
		if (phoneNumber.matches("[+]?[0-9]+(-[0-9][0-9]*)*[0-9]*"))
			return true;
		else
			return false;

	}
}
