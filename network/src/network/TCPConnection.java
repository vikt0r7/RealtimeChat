package network;

import jdk.jfr.internal.BufferWriter;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;

    public TCPConnection(TCPConnectionListener eventListener, String ipAdress, int port) throws IOException{
        this(eventListener, new Socket( ipAdress, port)); //Викликаю другий коструктор

    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException
    {
        this.eventListener = eventListener;
        this.socket = socket;
        in =  new BufferedReader(new InputStreamReader (socket.getInputStream(),  Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()){
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);
                    }
                } catch (IOException e){
                    eventListener.onExeption(TCPConnection.this, e);

                } finally {
                    eventListener.onDissconect(TCPConnection.this);
                }

            }
        });
        rxThread.start();

    }

    public synchronized void sendString(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e){
            eventListener.onExeption(TCPConnection.this, e);
            dissconect();
        }
    }

    public synchronized void dissconect(){
        rxThread.isInterrupted();
        try {
            socket.close();
        } catch (IOException e){
            eventListener.onExeption(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection:" + socket.getInetAddress() + ": " + socket.getPort();
    }
}
