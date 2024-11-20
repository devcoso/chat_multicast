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

            // Join the multicast group
            InetAddress group = InetAddress.getByName(Constants.MCAST_GROUP);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            socket.joinGroup(new InetSocketAddress(group, Constants.MCAST_PORT), networkInterface);

            // Sender
            Sender sender = new Sender(socket);

            // Join the chat
            GetNameInterface getNameIntarfaz = new GetNameInterface(sender, socket);
            // Wait for validation
            String name = getNameIntarfaz.join();
            sender.setName(name);

            Chat v = new Chat(sender);

            // Create receiver threads
            RecieverThread receiver = new RecieverThread(socket, v);
            receiver.start();
            receiver.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

}
