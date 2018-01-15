package m1bhtree.base;

import gluffy.utils.JkTools;
import m1bhtree.action.Cooldown;
import m1bhtree.action.GlobalDelay;
import m1bhtree.base.basetype.CompareEnum;
import m1bhtree.base.basetype.DirEnum;
import m1bhtree.base.basetype.RandomFloat;
import m1bhtree.roleai.action.Attack;
import m1bhtree.roleai.action.AvoidMonster;
import m1bhtree.roleai.action.CheckDis;
import m1bhtree.roleai.action.SetRotation;
import navigation.Vector2;
import sceneRole.BaseRole;
import sceneRole.Npc;

public class AiBaseRole extends AiRoot {
    public BaseRole getTarget()
    {
        return  gameRole .attackCtrl.currentAttackTarget;


    }

    public Vector2 targetPos;
    public Vector2 walkCenter;

    public BaseRole gameRole;
    public BaseNode hitdownCall, attackCmpCall;

    public float stiffEndTime = 0;

    public	void init(BaseNode rootNode, BaseRole gameRole)
    {
        rootNode=new SelectorNode();
        rootNode.init(this,0);
        SequenceNode s0= new SequenceNode();

        rootNode.addChild(s0);
        Cooldown cooldown=new Cooldown();
        cooldown.time=new RandomFloat();
        cooldown.time.mix=1;

        s0.addChild(cooldown);

        AvoidMonster avoidMonster=new AvoidMonster();
        avoidMonster.dis=1;

        s0.addChild(avoidMonster);

        SetRotation setRotation1=new SetRotation();
        setRotation1.dir=DirEnum.escape;
        setRotation1.randomRot=new RandomFloat();
        setRotation1.randomRot.mix=60;
        setRotation1.randomRot.max=120;

        s0.addChild(setRotation1);

        GlobalDelay globalDelay=new GlobalDelay();
        globalDelay.rndTime=new RandomFloat();
        globalDelay.rndTime.mix=5;

        s0.addChild(globalDelay);





        SequenceNode s1= new SequenceNode();

        rootNode.addChild(s1);
        CheckDis checkDis=new CheckDis();
        checkDis.dis=60000;
        checkDis.compare= CompareEnum.Bigger;



        s1.addChild(checkDis);
        SetRotation setRotation=new SetRotation();
        setRotation.dir= DirEnum.moveTo;
        setRotation.randomRot=new RandomFloat();
        setRotation.randomRot.mix=0;

        s1.addChild(setRotation);


        SequenceNode s2= new SequenceNode();

        rootNode.addChild(s2);
          cooldown=new Cooldown();
        cooldown.time=new RandomFloat();
        cooldown.time.mix=4;

        s2.addChild(cooldown);

        Attack attack=new Attack();

        s2.addChild(attack);
        globalDelay=new GlobalDelay();
        globalDelay.rndTime=new RandomFloat();
        globalDelay.rndTime.mix=1.5f;
        s2.addChild(globalDelay);
        this.gameRole = gameRole;

       // walkCenter = gameRole.;
        this.rootNode = rootNode;

        waitTime = JkTools.getRandBetweenf(0f,  stepTime);
        this.globalDelay = JkTools.getRandBetweenf(0f, 0.3f);


    }


public     void Update()
    {
        breakOnce = false;
        if (  gameRole==null)
            return;


      //  if (gameRole.roleVo != null && gameRole.roleVo.attributes != null && gameRole.roleVo.attributes[AttributeEnum.FROZEN]+gameRole.roleVo.attributes[AttributeEnum.DIZZY] > 0)
        //    return;
      //  if (Time.time < stiffEndTime)
        //    return;
//        if (Model.sceneVo.allFightLogicPaused)
//        {
//            gameRole.act.playAct(RoleActEnum.STAND);
//            enabled = false;
//            return;
//        }
      //  if(Model.sceneVo.allFightLogicPausedNpc){
        //    if (gameRole.act.state == RoleActEnum.MOVE)
           // {
          //      gameRole.act.playAct(RoleActEnum.STAND);
            //}
        //    return;
     //   }
     //   if (Proxy.sceneProxy.inAllHeroDead())
        //{
       //     gameRole.act.playAct(RoleActEnum.STAND);
         //   enabled = false;
          //  return;
      //  }
        if (stepTime > 0)
        {
            if ((waitTime += 0.2f) < stepTime) {
                return;
            }
            waitTime = 0;
        }

        //Debug.Log("update-----------");
        if (paused)
            return;


        if (System.currentTimeMillis()*0.001< globalDelay) {
            System.out.println("globalDelay"+gameRole.tempID);
            return;
        }
        if (rootNode != null)
        {

            rootNode.execute();
        }

    }




    public void playAnm(int actID)
    {
        if (breakOnce)
            return;
        if (paused)
            return;


    }


}
