package chat.client;

import chat.Constants;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class SenderThread extends Thread {
    private MulticastSocket socket;
    private Interfaz v;

    public SenderThread(MulticastSocket socket, Interfaz v) {
        this.socket = socket;
        this.v = v;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            while (true) {
                String mensaje= br.readLine();
                byte[] buf = v.getMessage().getBytes();
                System.out.println("Mensaje enviado: " + mensaje);
                socket.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(Constants.MCAST_GROUP), Constants.MCAST_PORT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
