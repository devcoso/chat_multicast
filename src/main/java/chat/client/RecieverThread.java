package chat.client;

import chat.Constants;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.DatagramPacket;

public class RecieverThread extends Thread {
    private MulticastSocket socket;
    private Chat v;

    public RecieverThread(MulticastSocket socket, Chat v) {
        this.socket = socket;
        this.v = v;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buf = new byte[Constants.BUF_LEN];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                
                if (received.startsWith("<MESSAGE>")) {
                    String message = received.substring(9);
                    
                    Matcher messagePattern = Pattern.compile("<([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]+)>(.+)")
                    .matcher(message);

                    Matcher privateMessagePattern = Pattern.compile("<([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]+)><([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]+)>(.+)")
                    .matcher(message);

                    if(privateMessagePattern.matches()) {
                        System.out.println("Private message");
                        v.addPrivateMessage(privateMessagePattern.group(3), privateMessagePattern.group(1), privateMessagePattern.group(2));
                    } else if(messagePattern.matches()) {
                        v.addMessage(messagePattern.group(2), messagePattern.group(1));
                    } 
                }

                if (received.startsWith("<USERS>")) {
                    String users = received.substring(7);
                    v.updateUsers(users);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}