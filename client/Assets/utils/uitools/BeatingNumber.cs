using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
namespace ugui
{
    public class BeatingNumber : MonoBehaviour
    {

        Text labNum;
        int perNumber;
        int lastNumber;
        float lastBeatingTime;
        int currentNumber = 0;
        int currentTimes = 1;
        public int beatingCount = 40;
        private int _number;
        int currentBeatingCount = 1;
        bool isOverNow;
        void Awake()
        {
            labNum = GetComponent<Text>();

        }


        public void setNumber(int diffVal, int oldNumber = 0)
        {

            _number = Mathf.Abs(diffVal);
            currentBeatingCount = beatingCount;
            perNumber = _number / beatingCount;
            lastNumber = perNumber + _number % beatingCount;
            if (diffVal < 0)
            {
                perNumber = -perNumber;
                lastNumber = -lastNumber;
            }
            if (_number < beatingCount)
            {
                perNumber = lastNumber = diffVal < 0 ? -1 : 1;
                currentBeatingCount = _number;
            }

            lastBeatingTime = Time.time;
            currentTimes = 1;
            currentNumber = oldNumber;
        }

        public int getNumber()
        {
            return _number;
        }
        public bool overNow
        {
            set
            {
                isOverNow = value;
            }
        }

        //	public int number {
        //		get {
        //			return _number;
        //		}
        //		set {
        //			_number = number;
        //			Debug.LogError("number"+_number);
        //			perNumber = _number / beatingCount;
        //			lastNumber = perNumber + _number % beatingCount;
        //			if(_number<beatingCount){
        //				perNumber=lastNumber=1;
        //				beatingCount=_number;
        //			}
        //			lastBeatingTime = Time.time;
        //			currentTimes=1;
        //			currentNumber=0;
        //		}
        //	}
        void Start()
        {
            //setNumber(System.Int32.Parse(labNum.text));

        }

        void Update()
        {
            if (isOverNow)
            {
                isOverNow = false;
                labNum.text = _number + "";
                currentNumber = _number;
                currentTimes = currentBeatingCount + 2;
                return;
            }
            if (Time.time - lastBeatingTime > 0.02f && currentTimes <= currentBeatingCount + 1)
            {
                labNum.text = currentNumber + "";
                if (currentTimes < currentBeatingCount)
                {
                    currentNumber += perNumber;
                }
                else
                {
                    currentNumber += lastNumber;
                }
                currentTimes++;

                lastBeatingTime = Time.time;

            }

        }

        public int getCurrentNumber()
        {
            return currentNumber;
        }
    }
}

