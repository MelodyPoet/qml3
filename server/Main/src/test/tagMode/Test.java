package test.tagMode;


import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {


    public static void main(String[] args) {
        EventTimer eventTimer = new EventTimer();
        EventTimerTag tagStart = new EventTimerTag(EventTimerType.REDNESS_FIGHT_START);
        EventTimerTag tagEnd = new EventTimerTag(EventTimerType.REDNESS_FIGHT_END);
        eventTimer.addTimeEvent(tagStart, 1);
        eventTimer.addTimeEvent(tagEnd, 2);
        for (int i = 0; i < 40; i++) {
            // for test check
            try {
                Thread.currentThread().sleep(100);
                if (tagStart.called == false) {
                    System.out.println("not start");
                } else if (tagEnd.called == false) {
                    System.out.println("can enter");
                } else {
                    System.out.println("over");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}

class EventTimerTag  implements Runnable{
  public   EventTimerType tagType;
     public      ScheduledExecutorService service;
    public  int callTime;
    public  boolean called=false;
    EventTimerTag ( EventTimerType tagType){
        this.tagType=tagType;
    }
    @Override
    public void run() {
        called=true;
        System.out.println(tagType);
        service.shutdown();
    }
}
enum     EventTimerType{
    REDNESS_FIGHT_START,
    REDNESS_FIGHT_END
}
  class EventTimer {
      HashMap<EventTimerType,EventTimerTag> allScheduled=new HashMap<>();

      public  void  addTimeEvent(EventTimerTag tag,int seconds){
          allScheduled.put(tag.tagType,tag);
          tag.service = Executors
                .newSingleThreadScheduledExecutor();
        tag.service.schedule(tag, seconds, TimeUnit.SECONDS);
    }

  }

