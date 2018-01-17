using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using starbucks.basic;
using starbucks.utils;
using UnityEngine.Playables;

public class Test : MonoBehaviour {
	public PlayableDirector changeWave;
	public Action onDie;
	public Action onUpdateHp;
 	public MovieClip2 hero;
	 
 	 
	public MovieClip2 currentMonster;
  	bool running=true;
	public static Test instance;
	List<ICommand> cmdList=new List<ICommand>();
 
	void Awake(){
		instance = this;
	}
	// Use this for initialization
	void Start () {

		cmdList.Add (new AttackCmd (){ isHero = true });
		cmdList.Add (new HurtCmd(){isHero=false,point=62});

		cmdList.Add (new AttackCmd (){ isHero = false } );
		cmdList.Add (new HurtCmd(){isHero=true,point=45} );

		cmdList.Add (new AttackCmd (){ isHero = true });
		cmdList.Add (new DieCmd(){isHero=false,point=76});

 




 
		//Application.targetFrameRate = 30;
		//hero.Play("run");
		 
	 
		//onUpdateHp ();
		running = false;
		//changeWave.time = changeWave.duration / 2;
		//changeWave.Play ();
		StartCoroutine (fighting());
	}
//	void Update () {
//		//Application.targetFrameRate = 30;
//		if (running) {
//			if (Vector3.Distance (hero.transform.position, currentMonster.transform.position) < 3) {
//				hero.Play ("wait");
//				onUpdateHp ();
//				running = false;
//				StartCoroutine (fighting());
//				//new AttackCmd (){ isHero = true }.execute ();
//			}
//		}
//	}
	IEnumerator fighting(){
		yield return new WaitForSeconds (1f);
	 
	 
		yield return new WaitForSeconds (0.7f);
		while(cmdList.Count>0){
			ICommand cmd=	cmdList [0];
			cmdList.RemoveAt (0);
			Debug.LogError (cmd);
			cmd.excute ();
			if (cmd is AttackCmd) {


			
			}
			if (cmd is HurtCmd) {
 
				yield return new WaitForSeconds (1.3f);


			}
			if (cmd is DieCmd) {

				yield return new WaitForSeconds (1.5f);
				if(onDie!=null)
				onDie ();
				
		 
				break;

			}

		}
	}
	// Update is called once per frame
	public	void playAll (string lab) {
		if (lab == "attack") {
			//wa.Play ("hurt");
			//Camera.main.GetComponent<FollowHeroCamera> ().hero = hero.transform;
		}
		if (lab == "hurt") {
			hero.Play ("hurt");
			//Camera.main.GetComponent<FollowHeroCamera> ().hero = wa.transform;
		}
  
	}
 
}
