package chat.client;

import chat.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.DatagramPacket;

public class RecieverThread extends Thread {
    private MulticastSocket socket;
    private Chat v;
    private String name;

    public RecieverThread(MulticastSocket socket, Chat v, String name) {
        this.socket = socket;
        this.v = v;
        this.name = name;
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
                    Matcher filePattern = Pattern.compile("<([a-zA-Z0-9\\-]+)><(.*)><([0-9]+)><([a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]*)>")
                    .matcher(file);
                    if(filePattern.matches()) {
                        // Extraer los grupos
                        String fileId = filePattern.group(1);   // UUID del archivo
                        String fileName = filePattern.group(2); // Nombre del archivo
                        String segments = filePattern.group(3); // Total de segmentos
                        String owner = filePattern.group(4); // Dueño del archivo
                        if(owner.equals(name)) {
                            v.addFile(fileName, owner);
                            continue;
                        }
                        // Juntar los archivos
                        File f = new File("");
                        String ruta = f.getAbsolutePath();
                        File tmpDir = new File( ruta + "\\.tmp\\" + name + "\\" + fileId + "\\");
                        File[] files = tmpDir.listFiles();
                        String content = "";
                        int i = 0;
                        for (File fileSegment : files) {
                            i++;
                            try {
                                content += Files.readString(fileSegment.toPath());
                            } catch (IOException e) {
                               v.erroFile(fileName, owner);
                            }
                        }

                        if(i != Integer.parseInt(segments)) {
                            v.erroFile(fileName, owner);
                            continue;
                        }

                        try {
                            // Decodificar el archivo
                            byte[] decodedFile = Base64.getDecoder().decode(content);
                            // Crear el directorio
                            File dir = new File(ruta + "\\files\\" + name + "\\");
                            dir.mkdirs();

                            // Crear el archivo
                            File f3 = new File(dir, fileName);
                            // Escribir el archivo
                            Files.write(f3.toPath(), decodedFile);    
                        } catch (Exception e) {
                            v.erroFile(fileName, content);
                        }

                        v.addFile(fileName, owner);    
                    }
                } else if (received.startsWith("<SEGMENT>")) {
                    String segment = received.substring(9);
                    //System.out.println(segment);
                    Matcher segmentPattern = Pattern.compile("(?s)^<([a-zA-Z0-9\\-]+)><([0-9]+)><([0-9]+)>(.+)")
                    .matcher(segment);
                    if(segmentPattern.matches()) {
                         // Extraer los grupos
                        String fileID = segmentPattern.group(1);   // UUID del segmento
                        String segmentNumber = segmentPattern.group(2); // Número del segmento
                        String totalSegments = segmentPattern.group(3); // Total de segmentos
                        String segmentContent = segmentPattern.group(4); // Contenido del segmento

                        // Crear la carpeta ./tmp si no existeFile f = new File("");
                        File f = new File("");
                        String ruta = f.getAbsolutePath();
                        File tmpDir = new File( ruta + "\\.tmp\\" + name + "\\" + fileID + "\\"); 
                        tmpDir.mkdirs(); // Crear la carpeta

                        // Generar el archivo en la carpeta tmp
                        File file = new File(tmpDir, "_segment_" + segmentNumber + "_of_" + totalSegments + ".txt");
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