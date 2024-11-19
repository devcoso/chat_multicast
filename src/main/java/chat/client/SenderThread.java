package chat.client;

import chat.Constants;
import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class SenderThread extends Thread {
    private MulticastSocket socket;

    public SenderThread(MulticastSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buf = new byte[Constants.BUF_LEN];
                System.in.read(buf);
                socket.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(Constants.MCAST_GROUP), Constants.MCAST_PORT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
