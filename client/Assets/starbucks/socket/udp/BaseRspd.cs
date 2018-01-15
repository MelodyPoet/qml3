using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.udp{

public class BaseRspd
{

	public int proID;

	protected ByteArray bytes;
	public void setData(ByteArray bytes){
		this.bytes=bytes;
		Debug.Log("rev:"+proID);
	}

	public virtual void  readData()
	{

	}

}


}