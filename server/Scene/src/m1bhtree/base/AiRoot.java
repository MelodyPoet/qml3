package m1bhtree.base;

public class AiRoot {
    public BaseNode rootNode;
    public int aiState;
    public int[] otherStates = new int[3];
    public float stepTime = 0.1f;
    public float waitTime;
    public double globalDelay;
    public boolean paused;
    public boolean breakOnce;
    public boolean printNodeName = false;
    public boolean runself = false;



    protected void execute() {
        if (rootNode != null) {
            breakOnce = false;
            rootNode.execute();

        }
    }

    public void stop() {
        rootNode = null;
    }

    public void pause(float time) {
        paused = true;
        //    CancelInvoke();
//        if (time > 0)
//            Invoke("play", time);
    }

    public void play() {
        paused = false;
    }


//    IEnumerator self_loop ()
//    {
//        while (true) {
//            if (stepTime > 0)
//                yield return new WaitForSeconds (stepTime);
//			else
//            yield return 0;
//
//            execute ();
//        }
//    }
}
