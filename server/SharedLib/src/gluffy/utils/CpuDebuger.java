package gluffy.utils;

public class CpuDebuger {
	private static long[] tim = new long[999];
    private static int[] showCpuTime = new int[999];
    public static void init(int id,int cpuPrintTime) {
        showCpuTime[id]=cpuPrintTime;
        reset(id);
    }
	public static void reset(int id) {
		tim[id] = System.currentTimeMillis();
		// if (LogManager.debugMode)
		// LogManager.printNormal("CpuDebuger:"+msg+";开始计时");
	}

	public static void print(String msg, int id) {
		long dt = System.currentTimeMillis() - tim[id];
		reset(id);
		 LogManager.printCpu(msg, dt,showCpuTime[id]);
       // System.out.println(msg + ",time:" +dt );

	}
}
