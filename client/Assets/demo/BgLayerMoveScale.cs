using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BgLayerMoveScale : MonoBehaviour {
	public Transform normalLayer;
	public float scale =0.3f;
	private Vector3 pos;
	// Use this for initialization
	void Start () {
		pos = transform.position;
	}
	
	// Update is called once per frame
	void Update () {
		pos.x = normalLayer.position.x * scale;
		this.transform.position = pos;
	}
}
