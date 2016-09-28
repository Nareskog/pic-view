package se.tmn.utils;
public class Logger {
	private enum ErrorLevel {
		Debug, Info, Warning, Error, Fatal
	}

	static public void d(String message) {
		System.out.println(message);
	}


	static public void i(String message) {
		printLog(ErrorLevel.Info, message, null);
	}

	static public void i(String message, Exception ex) {
		printLog(ErrorLevel.Info, message, ex);
	}

	static public void w(String message) {
		printLog(ErrorLevel.Warning, message, null);
	}

	static public void w(String message, Exception ex) {
		printLog(ErrorLevel.Warning, message, ex);
	}

	static public void e(String message) {
		printLog(ErrorLevel.Error, message, null);
	}

	static public void e(String message, Exception ex) {
		printLog(ErrorLevel.Error, message, ex);
	}
	static public void f(String message) {
		printLog(ErrorLevel.Fatal, message, null);
	}

	static public void f(String message, Exception ex) {
		printLog(ErrorLevel.Fatal, message, ex);
	}

	private static void printLog(ErrorLevel level, String message, Exception ex) {
		if (ex != null) {
			message = message + " - " + ex.getMessage();
		}

		System.out.println(message);
	}
}
