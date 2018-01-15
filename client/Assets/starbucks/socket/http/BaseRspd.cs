using starbucks.basic;

namespace starbucks.socket.http
{
    public class BaseRspd
    {

        public int proID;
        protected ByteArray bytes;
        public void setData(ByteArray bytes)
        {
            this.bytes = bytes;
            //Debug.Log("rev:"+proID);
        }

        public virtual void readData()
        {

        }

    }

}
