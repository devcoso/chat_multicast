package chat.server;

import chat.Constants;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) {
        try {
            // Create a multicast socket
            MulticastSocket socket = new MulticastSocket(Constants.MCAST_PORT);
            socket.setTimeToLive(Constants.TTL);
            
            // Join the multicast group
            InetAddress group = InetAddress.getByName(Constants.MCAST_GROUP);
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            socket.joinGroup(new InetSocketAddress(group, Constants.MCAST_PORT), networkInterface);

            // Manange the user list
            ArrayList<String> usuarios = new ArrayList<>();

            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[Constants.BUF_LEN], Constants.BUF_LEN);
                    socket.receive(packet); // Wait for a message
                    String received = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                    //System.out.println("Mensaje recibido: " + received);
                    String message;
                    if(received.contains("<JOIN>")) {
                        String[] parts = received.split("<JOIN>");
                        // Remove special characters
                        parts[1] = parts[1].replaceAll("[^a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]", "");
                        // Max 20 characters
                        String usuario = parts[1].substring(0, Math.min(parts[1].length(), 20));
                        if (usuario.length() == 0) {
                            System.out.println("Usuario no puede ser vacío");
                            continue;
                        }
                        if(usuarios.contains(usuario)) {
                            System.out.println("Usuario " + usuario + " ya está en el chat");
                            continue;
                        }
                        usuarios.add(usuario);
                        System.out.println("Usuario " + usuario + " se ha unido al chat");
                        message = "<MESSAGE><SERVIDOR>" + "Usuario " + usuario + " se ha unido al chat";
                        byte[] buf = message.getBytes();
                        DatagramPacket packetMessage = new DatagramPacket(buf, buf.length, group, Constants.MCAST_PORT);
                        socket.send(packetMessage);
                    } else if(received.contains("<LEAVE>")) {
                        String[] parts = received.split("<LEAVE>");
                        // Remove special characters
                        parts[1] = parts[1].replaceAll("[^a-zA-Z0-9]", "");
                        // Max 20 characters
                        String usuario = parts[1].substring(0, Math.min(parts[1].length(), 20));
                        usuarios.remove(usuario);
                        System.out.println("Usuario " + usuario + " ha abandonado el chat");
                        message = "<MESSAGE><SERVIDOR>" + "Usuario " + usuario + " ha abandonado el chat";    
                        byte[] buf = message.getBytes();
                        DatagramPacket packetUsers = new DatagramPacket(buf, buf.length, group, Constants.MCAST_PORT);
                        socket.send(packetUsers);
                    } else {
                        //System.out.println("Mensaje recibido: " + received);
                        continue;
                    }
                    // Send users list
                    String users = "<USERS>";
                    for (String user : usuarios) {
                        users += user + ",";
                    }
                    byte[] buf = users.getBytes();
                    DatagramPacket packetUsers = new DatagramPacket(buf, buf.length, group, Constants.MCAST_PORT);
                    socket.send(packetUsers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
