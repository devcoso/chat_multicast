package chat.client;

import chat.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MulticastSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.DatagramPacket;

public class RecieverThread extends Thread {
    private MulticastSocket socket;
    private Chat v;
    private String name;
    private int contador = 0;

    public RecieverThread(MulticastSocket socket, Chat v, String name) {
        this.socket = socket;
        this.v = v;
        this.name = name;
        this.contador = 0;  
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
                        v.addPrivateMessage(privateMessagePattern.group(3), privateMessagePattern.group(1), privateMessagePattern.group(2));
                    } else if(messagePattern.matches()) {
                        v.addMessage(messagePattern.group(2), messagePattern.group(1));
                    } 
                } else if (received.startsWith("<USERS>")) {
                    String users = received.substring(7);
                    v.updateUsers(users);
                } else if(received.startsWith("<FILE>")) {
                    String file = received.substring(6);
                    Matcher filePattern = Pattern.compile("<([a-zA-Z0-9\\-]+)><(.*)><[0-9]+><([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]*)>")
                    .matcher(file);
                    if(filePattern.matches()) {
                        v.addFile(filePattern.group(2), filePattern.group(3), filePattern.group(1));    
                    }
                } else if (received.startsWith("<SEGMENT>")) {
                    String segment = received.substring(9);
                    //System.out.println(segment);
                    System.out.println("Segmento recibido " + contador);
                    contador++;
                    Matcher segmentPattern = Pattern.compile("(?s)^<([a-zA-Z0-9\\-]+)><([0-9]+)><([0-9]+)>(.+)")
                    .matcher(segment);
                    if(segmentPattern.matches()) {
                         // Extraer los grupos
                        String segmentId = segmentPattern.group(1);   // UUID del segmento
                        String segmentNumber = segmentPattern.group(2); // Número del segmento
                        String totalSegments = segmentPattern.group(3); // Total de segmentos
                        String segmentContent = segmentPattern.group(4); // Contenido del segmento

                        // Crear la carpeta ./tmp si no existeFile f = new File("");
                        File f = new File("");
                        String ruta = f.getAbsolutePath();
                        File tmpDir = new File( ruta + "\\.tmp\\" + name + "\\");
                        tmpDir.mkdirs(); // Crear la carpeta

                        // Generar el archivo en la carpeta tmp
                        File file = new File(tmpDir, segmentId + "_segment_" + segmentNumber + "_of_" + totalSegments + ".txt");
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                            // Escribir el contenido del segmento en el archivo
                            writer.write(segmentContent);  // Escribir el contenido extraído
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("No se pudo parsear el segmento");
                    }
                }   

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}