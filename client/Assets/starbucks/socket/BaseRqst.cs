namespace starbucks.socket
{
    public class BaseRqst : udp.BaseRqst
    {
        public BaseRqst(int proID, bool isImportant = false) : base(proID, isImportant)
        {
        }
    }
}

