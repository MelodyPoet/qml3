package test.callMode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {


    public static void main(String[] args) {
        EventTimer eventTimer = new EventTimer();
        EventTimerTag tagStart = new EventTimerTag(EventTimerType.REDNESS_FIGHT_START);
        EventTimerTag tagEnd = new EventTimerTag(EventTimerType.REDNESS_FIGHT_END);
        eventTimer.addTimeEvent(tagStart, 2);
        eventTimer.addTimeEvent(tagEnd, 3);

        for (int i = 0; i < 40; i++) {
            // for test check
            try {
                Thread.currentThread().sleep(100);
                if(eventTimer.waitForDealList.size()==0)continue;
                EventTimerTag callItem=  eventTimer.waitForDealList.get(0);
                eventTimer.waitForDealList.remove(0);
                if (callItem.tagType== EventTimerType.REDNESS_FIGHT_START) {
                    System.out.println("do something on start");
                } else if (callItem.tagType== EventTimerType.REDNESS_FIGHT_END) {
                    System.out.println("do something on end");

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}

class EventTimerTag  implements Runnable{
  public EventTimerType tagType;
     public      ScheduledExecutorService service;
    public  int callTime;
    public  EventTimer eventTimer;
     EventTimerTag ( EventTimerType tagType){
        this.tagType=tagType;
    }
    @Override
    public void run() {
        eventTimer.waitForDealList.add(this);
        //System.out.println(tagType);
        service.shutdown();
    }
}

enum     EventTimerType{
    REDNESS_FIGHT_START,
    REDNESS_FIGHT_END
}

class EventTimer {
    HashMap<EventTimerType, EventTimerTag> allScheduled=new HashMap<>();
public  ArrayList<EventTimerTag> waitForDealList=new ArrayList<EventTimerTag>();

    public  void  addTimeEvent(EventTimerTag tag, int seconds){
        allScheduled.put(tag.tagType,tag);
        tag.eventTimer=this;
        tag.service = Executors
              .newSingleThreadScheduledExecutor();
      tag.service.schedule(tag, seconds, TimeUnit.SECONDS);
  }

}

