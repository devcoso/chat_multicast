package chat.client;

import chat.Constants;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import javax.swing.JFileChooser;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class Sender {
    private MulticastSocket socket;
    private String name = "";

    public Sender(MulticastSocket socket) {
        this.socket = socket;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void send(String message) {
        try {
            byte[] buf = message.getBytes();
            socket.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(Constants.MCAST_GROUP), Constants.MCAST_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        send("<MESSAGE><" + name + ">" + message);
    }

    public void sendFile() {
        // Crear un JFileChooser
        JFileChooser fileChooser = new JFileChooser();
        // Configurar el cuadro de diálogo (opcional)
        fileChooser.setDialogTitle("Selecciona el archivo a enviar");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Solo archivos
        // Mostrar el cuadro de diálogo y capturar la respuesta del usuario
        int respuesta = fileChooser.showOpenDialog(null);
        // Verificar si el usuario seleccionó un archivo
               if (respuesta == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            String randomName = UUID.randomUUID().toString();
            long segmentSize = Constants.BUF_LEN / 2;
            
            try{
                byte[] fileBytes = Files.readAllBytes(archivoSeleccionado.toPath());  // Leer el archivo como bytes
                String encodedFile = Base64.getEncoder().encodeToString(fileBytes);

                long size = encodedFile.length();
                int segments = (int) Math.ceil((double) size / segmentSize);

                for (int i = 0; i < segments; i++) {
                    long start = i * segmentSize;
                    long end = Math.min(size, start + segmentSize);
                    String segment = "<SEGMENT><" + randomName + "><" + (i + 1) + "><" + segments + ">"
                            + encodedFile.substring((int) start, (int) end);
                    send(segment);
                }

                send("<FILE><" 
                    + randomName + "><" 
                    + archivoSeleccionado.getName() + "><" 
                    + segments + "><" 
                    + name + ">");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
