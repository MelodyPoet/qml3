using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

public class AverageMove : MonoBehaviour,ICommand
{
  
     public Vector3 dir=Vector3.left;
	private float startTime;
	// Use this for initialization
	void Start ()
	{
	  

	}
	
 

    public void excute()
    {
 

		transform.position += dir*Time.deltaTime;
    }
}
