using System;
using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.utils
{
	[RequireComponent(typeof(Image))]
	public class MovieClip : MonoBehaviour
	{
		[System.Serializable]
		public class  MovieClipVo
		{
			public string lab = "main";
			public int startFrame = 0;
			public int frameLen = 1;
			public bool loop = true;

			public string endToGoLab;
        
			public Component updateCallItem;
			public Component enterCallItem;
			public Component exitCallItem;
			public Action enterAction;
			public Action exitAction;

		}
		public int fps = 30;
		public bool spriteAssetMode = false;
		public MovieClipAsset spritesAsset;
		public Sprite [] sprites;
		public MovieClipVo[] clips;
		public string autoPlay;
		private Dictionary<string , MovieClipVo> clipsMap;
		public MovieClip[] playBindingChildren;
		private MovieClipVo currentClip;
		private bool isPlaying=false;
		private float clipStartTime;
		private Vector3 actInitPos;
		private Image spriteRenderer;
		// Use this for initialization
		void Awake ()
		{
			spriteRenderer = GetComponent<Image>();
			 
			
			clipsMap =new Dictionary<string, MovieClipVo>();
			if (clips.Length == 0)
			{
				autoPlay = "main";
				clipsMap["main"] = new MovieClipVo() {startFrame = 0, frameLen =spriteAssetMode? spritesAsset.sprites.Length: sprites.Length};
			}
			foreach (var item in clips)
			{
				clipsMap[item.lab] = item;
			}
			

		}
		void Start(){
			if(spriteAssetMode)
			transform.localPosition = spritesAsset.offsetPos;
			if (string.IsNullOrEmpty (autoPlay) == false) {
				Play (autoPlay);
			}
		}
		public void Play(string lab)
		{
			isPlaying = true;
			clipStartTime = Time.timeSinceLevelLoad;
			if (clipsMap == null) {
				autoPlay = lab;
				return;
			}
			bool sameClip = false;
			if (currentClip != null)
			{
				if (currentClip.exitAction != null)
					currentClip.exitAction();
				if(currentClip.exitCallItem!=null)
					(currentClip.exitCallItem as ICommand).excute();

				if (currentClip.lab == lab)
				{
					sameClip = true;
				}
			}
			if (sameClip == false)
			{
				actInitPos = transform.position;
			}
			foreach (var child in playBindingChildren) {
				if(child!=null)
					child.Play (lab);
			}
			//Debug.LogError (lab);
			currentClip = clipsMap[lab];
			if (currentClip.enterAction != null)
				currentClip.enterAction();
			if (currentClip.enterCallItem != null)
				(currentClip.enterCallItem as ICommand).excute();

		}

		public void  Play(string lab,float delay){
			StopAllCoroutines ();
			StartCoroutine (PlayDelay(lab,delay));

		}
		private IEnumerator PlayDelay(string lab,float delay){
			yield return new WaitForSeconds(delay);
			Play(lab);

		}


		public void Stop()
		{
			isPlaying = false;
		}
//	void LateUpdate(){
//
//		if (currentClip.updateCallItem != null)
//		{		float dtime  = Time.timeSinceLevelLoad- clipStartTime;
//			(currentClip.updateCallItem as ICommand).execute(dtime, (float)currentClip.frameLen/fps, actInitPos);
//
//		}
//	}
		// Update is called once per frame
		void Update ()
		{
 
			if (isPlaying == false) return;
			if(currentClip==null)return;
			float dtime  = Time.timeSinceLevelLoad- clipStartTime;

			int index =(int)(dtime * fps);
			if (currentClip.frameLen <= index)
			{
				if (currentClip.loop)
				{
					index %= currentClip.frameLen;
				}
				else
				{
					if (string.IsNullOrEmpty(currentClip.endToGoLab))
					{
						Stop();

					}
					else
					{
						Play(currentClip.endToGoLab);
					}
					return;
				}
			}

			Sprite nextSprite = spriteAssetMode? spritesAsset.sprites[currentClip.startFrame + index]:sprites[currentClip.startFrame + index];
		//	Debug.Log(nextSprite+"---"+ index);
			if (nextSprite == null)
			{
				spriteRenderer.sprite = null;
			}
			else
			{
				if (spriteRenderer.sprite == null || spriteRenderer.sprite.texture != nextSprite.texture ||
				    spriteRenderer.sprite.rect.position != nextSprite.rect.position)
				{
					spriteRenderer.sprite = nextSprite;
				}
			}
			if (currentClip.updateCallItem != null)
			{
				CurveMove cm = currentClip.updateCallItem as CurveMove;
				cm.dt = dtime;
				cm.len = (float) currentClip.frameLen / fps;
				cm.initPos = actInitPos;
				cm.excute();
          

			}




		}
	}
}
