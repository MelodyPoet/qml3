import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Test {
    public static class ThreadCallBack   implements Runnable {

        // 标志位
        private Consumer<Void> action;
            ExecutorService exec ;
        public ThreadCallBack(Consumer<Void> action) {
            this.action = action;
            exec= Executors.newFixedThreadPool(1);
        }

        public void run() {
            action.accept(null);
        }
       public  void submit(){
           exec.submit(this);
       }
        public  void shutdown(){
            exec.shutdown();
        }
        public  void submitAndShutdown(){
            submit();
            shutdown();
        }

    }

    // 可以容纳10个线程的执行器.
    final static ExecutorService exec = Executors.newFixedThreadPool(50);
    static Set<Integer> rstAdd=null,rstRemove=null,rstSame=null;
    public static void main(String[] args)
    {
        int [] a=new int[]{6,5,3,1,7,9,4, 106, 105, 103, 101, 107, 109, 104, 206, 205,203, 201,207, 209, 204, };
        int[] b = new int[] { 16, 15, 3, 21, 7, 39, 64 ,116, 115, 103, 121, 107, 139, 164 ,216, 215, 203, 221, 207, 239, 264 };
        HashSet<Integer> aSet=new HashSet<Integer>();
        HashSet<Integer> bSet = new HashSet<Integer>();


        for (int i : a)
        {
            aSet.add(i);
        }
        for (int i : b)
        {
            bSet.add(i);
        }
        Consumer<Integer> action= voida->{

                HashMap<Integer,Byte> dic=new HashMap<>();

            for (int i : a)
                {
                    dic.put(i,(byte)1);
                }
            for (int i : b) {
                if (dic.containsKey(i)) {
                    dic.put(i, (byte) 3);
                } else {
                    dic.put(i, (byte) 2);
                }
            }

        };
        Consumer<Integer> action2= voida->{
           rstRemove= new HashSet<Integer>();

            rstRemove.addAll(aSet);
            rstRemove.removeAll(bSet);

            rstAdd= new HashSet<Integer>();

            rstAdd.addAll(bSet);
            rstAdd.removeAll(aSet);


            rstSame= new HashSet<Integer>();

            rstSame.addAll(aSet);
            rstSame.retainAll(bSet);

        };
     long tim=   System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            action2.accept(0);
        }


        System.out.println(System.currentTimeMillis()-tim);
    }

    private static void callfun(int i) {

        ThreadCallBack runner = new ThreadCallBack(aVoid -> {
            try {
                System.out.println(i);
                Thread.sleep(1000+i*500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i);});
        runner.submitAndShutdown();
    }
}
