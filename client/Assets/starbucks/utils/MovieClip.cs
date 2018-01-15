using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace starbucks.utils
{
	public class MovieClip : MonoBehaviour {
		private Dictionary<string ,MovieClipVo> dicMovieClips;
		public	MovieClipVo []movieClips;
		private float currentMcStartTime;
		private MovieClipVo currentMcVo;
		public string autoPlayLab;
		public MovieClip[] playBindingChildren;
		private SpriteRenderer spr;
		private Vector3 initPos;
		[System.Serializable]
		public	class MovieClipVo{
			public string frameLab;
			public Sprite []frames;
			public bool loop=false;
			public string endToFrameLab;
			public AnimationCurve moveX;
			public GameObject activeObj;
		}
		// Use this for initialization
		void Awake () {
			dicMovieClips = new Dictionary<string, MovieClipVo> ();
			foreach (var item in movieClips) {
				dicMovieClips [item.frameLab] = item;
			}
			spr = GetComponent<SpriteRenderer> ();
			initPos = transform.position;
		}
		void Start(){
			if (string.IsNullOrEmpty (autoPlayLab) == false) {
				Play (autoPlayLab);
			}
		}
		public void  Play(string lab){
			currentMcStartTime = Time.timeSinceLevelLoad;
		 
			if (dicMovieClips == null) {
				autoPlayLab = lab;
				return;
			}
			currentMcVo = dicMovieClips [lab];
			foreach (var child in playBindingChildren) {
				if(child!=null)
					child.Play (lab);
			}
			if (currentMcVo.activeObj != null) {
				currentMcVo.activeObj.SetActive (false);
				currentMcVo.activeObj.SetActive (true);
			}
		 
		}
		public void  Play(string lab,float delay){
			StopAllCoroutines ();
			StartCoroutine (PlayDelay(lab,delay));

		}
		private IEnumerator PlayDelay(string lab,float delay){
			yield return new WaitForSeconds(delay);
			Play(lab);

		}
	
		// Update is called once per frame
		void Update () {
			if (currentMcVo == null)
				return;
			float dtime = Time.timeSinceLevelLoad - currentMcStartTime;
			int index = (int)(dtime *30);
			if (currentMcVo.loop) {
				index = index % currentMcVo.frames.Length;
			} else {
				if (index >= currentMcVo.frames.Length) {
					index = currentMcVo.frames.Length - 1;
					if (string.IsNullOrEmpty (currentMcVo.endToFrameLab) == false) {
						Play (currentMcVo.endToFrameLab);
						return;
					}
				}


			}
			if (currentMcVo.moveX.length > 1) {
				Vector3 pos = Vector3.zero;
				pos.x=currentMcVo.moveX.Evaluate (dtime);
				transform.position=initPos+pos;
			}
			Sprite nextSp = currentMcVo.frames [index];
			if (nextSp.rect.position!=spr.sprite.rect.position) {
				spr.sprite =nextSp;
			}
		}
	}
}
