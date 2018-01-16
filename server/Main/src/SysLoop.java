
/**
 * Created by jackie on 16-4-1.
 */
public class SysLoop   extends Thread {


    @Override
    public void run() {
        super.run();

long tim=0;
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
tim++;

            loop1Se();
            if(tim%10==0)loop10Se();
            if(tim%60==0)loop1Min();
        }
    }

    private void loop1Se() {

    }
    private void loop10Se() {

    }
    private void loop1Min() {

    }
}