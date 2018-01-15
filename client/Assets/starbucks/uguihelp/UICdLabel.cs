using System;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.uguihelp
{
    public class UICdLabel : MonoBehaviour
    {
        Text lab;
        public float seconds;

        public Action OnCdLabelStart;
        public Action OnCdLabelStop;
        public bool hideLabWendEnd = true;
        Coroutine startCdCoroutine;
        bool isRun = false;
        int second_2 = 0;

        public int maxCount = 0;
        public int leftCount = 0;

        void Awake()
        {
            lab = GetComponent<Text>();
            if (hideLabWendEnd)
                lab.gameObject.SetActive(false);
        }

        public void init(int max)
        {
            maxCount = max;
            leftCount = max;
        }

        // Update is called once per frame
        public void startCD(float seconds, bool needMinus = true)
        {
            //        if(maxCount > 1 && leftCount != maxCount)
            //            stopCD();
            this.seconds = seconds;

            if (maxCount > 1 && needMinus)
            {
                leftCount--;
                if (leftCount <= 0)
                    leftCount = 0;
            }
            if (hideLabWendEnd)
            {
                lab.gameObject.SetActive(!(bool)(maxCount > 1));
                //            lab.gameObject.SetActive(true);
            }
            if (lab != null)
            {
                lab.text = Mathf.CeilToInt(seconds) + "";
            }

            //        if (startCdCoroutine != null)
            //            StopCoroutine(startCdCoroutine);
            //        startCdCoroutine = StartCoroutine(loop());
            if (OnCdLabelStart != null)
                OnCdLabelStart.Invoke();
            isRun = true;
        }

        public void stopCD()
        {
            //        if (isReady)
            //            return;
            stop();
        }

        void Update()
        {
            if (isRun == false)
                return;
            if (seconds <= 0)
                stop();
            second_2 = Mathf.CeilToInt(seconds);
            seconds -= Time.deltaTime;
            if (second_2 != Mathf.CeilToInt(seconds))
            {
                if (lab != null && Mathf.CeilToInt(seconds) != 0)
                    lab.text = Mathf.CeilToInt(seconds) + "";
            }
        }

        //    IEnumerator loop()
        //    {
        //        lab.text = (int)seconds + "";
        //        if (seconds <= 0)
        //        {
        //            stop();
        //        }
        //        yield return new WaitForSeconds(1);
        //        seconds -= 1;
        //        startCdCoroutine = StartCoroutine(loop());
        //    }

        public bool isReady
        {
            get
            {
                if (maxCount > 1)
                    return leftCount > 0;
                else
                    return seconds <= 0;
            }
        }

        void stop()
        {
            //        if (startCdCoroutine != null)
            //            StopCoroutine(startCdCoroutine);
            seconds = 0;
            if (lab == null)
                return;
            leftCount++;
            if (leftCount >= maxCount)
                leftCount = maxCount;

            if (lab != null)
                lab.text = "";
            if (hideLabWendEnd)
            {
                lab.gameObject.SetActive(false);
            }

            isRun = false;
            if (OnCdLabelStop != null)
            {
                OnCdLabelStop.Invoke();
            }
        }
    }
}
