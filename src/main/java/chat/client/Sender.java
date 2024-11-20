package chat.client;

import chat.Constants;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Sender {
    private MulticastSocket socket;

    public Sender(MulticastSocket socket) {
        this.socket = socket;
    }

    public void send(String message) {
        try {
            byte[] buf = message.getBytes();
            socket.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(Constants.MCAST_GROUP), Constants.MCAST_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
