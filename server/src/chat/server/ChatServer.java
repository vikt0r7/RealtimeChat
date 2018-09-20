package chat.server;

import network.TCPConnection;
import network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
            new ChatServer();
    }

    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try {
                  new TCPConnection(this, serverSocket.accept());
                } catch (IOException e){
                    System.out.println("TCPConnection exeption: " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection toConnection) {
        connections.add(toConnection);
        sendToAllConnection("Client connected: " + toConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection toConnection, String value) {
        sendToAllConnection(value);
    }

    @Override
    public synchronized void onDissconect(TCPConnection toConnection) {
        connections.remove(toConnection);
        sendToAllConnection("Client disconnected: " + toConnection);

    }

    @Override
    public synchronized void onExeption(TCPConnection toConnection, Exception e) {
        System.out.println("TCPConnection exeption: " + e);
    }

    private void sendToAllConnection(String value){
        System.out.println(value);
        final int count = connections.size();
        for (int i = 0; i < count; i++) connections.get(i).sendString(value);

    }
}
