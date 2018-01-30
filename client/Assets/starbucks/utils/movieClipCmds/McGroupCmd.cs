using System.Collections;
using System.Collections.Generic;
using starbucks.basic;
using UnityEngine;

public class McGroupCmd : MonoBehaviour,ICommand
{
    public Component[] cmds;
     
    public void excute()
    {
        foreach (ICommand cmd in cmds)
        {
            cmd.excute();
        }
    }
}
