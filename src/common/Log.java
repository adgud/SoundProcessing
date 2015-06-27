package common;

public class Log {
	public static void i(String msg) {
		System.out.println(msg);
	}
	public static void i(double msg) {
		System.out.println(msg);
	}
	public static void e(String msg) {
		System.err.println(msg);
	}
	public static void e(double msg) {
		System.err.println(msg);
	}
}
