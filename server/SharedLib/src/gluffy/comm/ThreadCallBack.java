package gluffy.comm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public   class ThreadCallBack   implements Runnable {
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
