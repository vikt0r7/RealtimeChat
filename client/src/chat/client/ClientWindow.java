package chat.client;

import network.TCPConnection;
import network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    /* CONSTANT FOR CONNECTION TO SERVER (CHANGE IT) */
    private static final String IP_ADRR = "192.168.0.0";
    private static final int PORT = 8189;

    /*BASE CONSTANT*/
    private static final int HEIGHT = 400;
    private static final int WIDTH = 600;

    /* RUN CLIENT SIDE */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("User");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;


    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);

        fieldInput.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);


        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADRR, PORT);
        } catch (IOException e) {
            printMsg("Connection exeption: " + e);

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg  = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + " : " + msg);
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection toConnection) {
        printMsg("Connection ready!");

    }

    @Override
    public synchronized void onReceiveString(TCPConnection toConnection, String value) {
        printMsg(value);
    }

    @Override
    public synchronized void onDissconect(TCPConnection toConnection) {
        printMsg("Connection close.");

    }

    @Override
    public synchronized void onExeption(TCPConnection toConnection, Exception e) {
        printMsg("Connection exeption: " + e);
    }

    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() { //
            @Override
            public void run() {
                log.append(msg + "\r\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
