using System.Collections;
using System.Collections.Generic;
using UnityEngine;
 
public class FollowHeroCamera : MonoBehaviour {
	public float offsetPos=0;
	public Transform hero;
	public float lerp=0.3f;
	Vector3 initPos;
	// Use this for initialization
	void Start () {
		initPos = transform.position;
	}
	
	// Update is called once per frame
	void Update () {
		Vector3 pos = initPos;
		pos.x += offsetPos+ hero.position.x;

		transform.position = Vector3.Lerp (transform.position, pos, lerp);
	}
}
