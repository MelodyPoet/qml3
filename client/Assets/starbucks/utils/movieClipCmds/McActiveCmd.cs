using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

public class McActiveCmd : MonoBehaviour,ICommand
{
    public GameObject item;
    public bool active;
    public void excute()
    {
        item.SetActive(active);
    }
}
