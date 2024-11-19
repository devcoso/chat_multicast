package chat.client;

import chat.Constants;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;


public class Client {

    public static void main(String[] args) {
        try {
            // Create a multicast socket
            MulticastSocket socket = new MulticastSocket(Constants.MCAST_PORT);
            socket.setTimeToLive(Constants.TTL);
            
            Interfaz v = new Interfaz();

            // Join the multicast group
            InetAddress group = InetAddress.getByName(Constants.MCAST_GROUP);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            socket.joinGroup(new InetSocketAddress(group, Constants.MCAST_PORT), networkInterface);
        
            // Create sender and receiver threads
            SenderThread sender = new SenderThread(socket, v);
            RecieverThread receiver = new RecieverThread(socket, v);
            sender.start();
            receiver.start();
            sender.join();
            receiver.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

}
