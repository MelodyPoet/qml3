using System;
using UnityEngine;

#if TEST_CPU
using System.Diagnostics;
#endif
namespace starbucks.utils
{
	public class Fps : MonoBehaviour {
		public float updateInterval = 0.5F;
		private float lastInterval;
		private int frames = 0;
		private string fps;
		private float mem;
#if TEST_CPU
	string info="";
	//PerformanceCounter cpuCounter;
	//PerformanceCounter ramCounter;
	System.Diagnostics.PerformanceCounter cpuCounter;
	System.Diagnostics.PerformanceCounter ramCounter;
	#endif
		void Start() {
 

	 
		 
			lastInterval = Time.realtimeSinceStartup;
			frames = 0;
#if TEST_CPU
		StartCpu ();
		#endif

		}
		void OnGUI() {
 
			GUI.depth = 100;
//GUILayout.Label("fps"+ fps+",memory:"+mem);
			GUI.skin.label.fontSize=32; 
			GUILayout.Label(fps);
#if TEST_CPU
		GUI.Label (new Rect (40, 0, 200, 20), info);
		#endif
		}
		void Update() {
			++frames;
		 
			//	mem = (Profiler.usedHeapSize + Profiler.GetMonoHeapSize ())/1024f/1024f;
			float timeNow = Time.realtimeSinceStartup;
			if (timeNow > lastInterval + updateInterval) {
				fps =String.Format("fps:{0}",(frames/ (timeNow-lastInterval)).ToString("f1"));
				frames = 0;
				lastInterval = timeNow;
			}
		}
 

#if TEST_CPU

void StartCpu() {
	System.Diagnostics.PerformanceCounterCategory.Exists("PerformanceCounter");

	cpuCounter = new PerformanceCounter();

	cpuCounter.CategoryName = "Processor";
	cpuCounter.CounterName = "% Processor Time";
	cpuCounter.InstanceName = "_Total";

	ramCounter = new PerformanceCounter("Memory", "Available MBytes");
	StartCoroutine (loop ());
}

IEnumerator loop(){
	while (true) {
		info = "> cpu: " + getCurrentCpuUsage () + "; >ram: " + getAvailableRAM ();
		yield return new WaitForSeconds (1);
	}

}
 

public string getCurrentCpuUsage(){
		return (int)cpuCounter.NextValue()+"%";
}

public string getAvailableRAM(){
		return (int)ramCounter.NextValue()+"MB";
} 
	#endif
	}
}