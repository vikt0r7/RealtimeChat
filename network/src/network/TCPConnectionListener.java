package network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection toConnection);
    void onReceiveString(TCPConnection toConnection, String value);
    void onDissconect(TCPConnection toConnection);
    void onExeption(TCPConnection toConnection, Exception e);

}
