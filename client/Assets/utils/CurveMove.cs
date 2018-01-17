using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

public class CurveMove : MonoBehaviour,ICommand
{
    public string tag;
    public AnimationCurve move;
    public Vector3 dir=Vector3.left;

	
	[HideInInspector]
	public float dt;
	[HideInInspector]
	public float len;
	[HideInInspector]
	public Vector3 initPos;
	// Use this for initialization
	void Start ()
	{
		initPos = transform.position;

	}
	
 

    public void excute()
    {
 
        transform.position = initPos + move.Evaluate(dt/len) * dir;
    }
}
