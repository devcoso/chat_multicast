package chat.client;

import chat.Constants;
import java.io.IOException;
import java.net.MulticastSocket;
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
                v.setMessage(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}