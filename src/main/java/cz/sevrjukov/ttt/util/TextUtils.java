package cz.sevrjukov.ttt.util;

public class TextUtils {


	public static String numToHumanStr(long num) {
		if (num <= 1000) {
			return Long.toString(num);
		}

		if (num < 1_000_000_000L) {
			return num / 1000 + "K";
		}
		return num / 1_000_000 + "M";
	}

}
