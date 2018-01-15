package gluffy.utils;



import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {
	public static boolean debugMode = true;
	public static Logger serverLog;// 记录启动 与关闭维护
	private static Logger crashLog;// 异常
 
	private static Logger payLog;//
	private static Logger buyLog;//
	private static Logger goldLog;//
	// private static Logger expLog;//经验获得
	private static Logger cupLog;// cup消耗过高记录

	public static void init() {

		new File("gameLog").mkdir();

		serverLog = Logger.getLogger("serverLog");
		crashLog = Logger.getLogger("crashLog");
		 
		payLog = Logger.getLogger("payLog");
		buyLog = Logger.getLogger("buyLog");
		goldLog = Logger.getLogger("goldLog");

		cupLog = Logger.getLogger("cupLog");
		addFile(serverLog, false);
		addFile(crashLog, true);
	 
		addFile(payLog, true);
		addFile(buyLog, true);
		addFile(goldLog, true);

		addFile(cupLog, true);

	}

	// public static void trace(Object msg) {
	// System.out.println(msg);
	// }
	public static void printCpu(String msg, long tim,int showTimeMore) {
		if (tim > showTimeMore) {
			cupLog.info(msg + ",time:" + tim);
		}
	}

	public static void printNormal(String msg) {

			System.out.println(msg);

		//nomalLog.info(msg);

	}

	public static void printPay(String msg) {

		payLog.info(msg);

	}

	public static void printBuy(String msg) {

		buyLog.info(msg);

	}

	public static void printGold(String msg) {
		goldLog.info(msg);
	}

	public static void printError(Exception e) {
		printError(e, 0);
	}

	public static void printError(Exception e, int uid) {
		e.printStackTrace();
		StringWriter trace = new StringWriter();
		e.printStackTrace(new PrintWriter(trace));
		crashLog.severe("guid:" + uid + ":::" + trace.toString());
		

	}

	private static void addFile(Logger log, boolean forDay) {
		FileHandler h = null;
		try {
			h = new FileHandler("gameLog/"
					+ log.getName()
					+ (forDay ? "_" + Calendar.getInstance().get(Calendar.DATE)
							: "") + ".txt", true);
			h.setEncoding("utf-8");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		h.setFormatter(new SimpleFormatter());
		log.addHandler(h);
	}

}
