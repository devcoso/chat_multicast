package chat.client;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import chat.Constants;

public class GetNameInterface extends JFrame {

    Boolean validated = false;
    String name;

    public GetNameInterface(Sender sender, MulticastSocket socket) {
        getContentPane().setLayout(null);

        setTitle("Chat de " + Constants.MCAST_GROUP);

        JLabel j1 = new JLabel();
        j1.setText("Introduce tu nombre de usuario");
        j1.setBounds(25, 10, 300, 20);
        j1.setFont(Constants.getFont());
        getContentPane().add(j1);

        JTextField jt1 = new JTextField();
        jt1.setFont(Constants.getFont());
        jt1.setBounds(25, 30, 400, 30);
        getContentPane().add(jt1);
        getContentPane().add(jt1);

        JButton button = new JButton("Únete al chat");
        button.setFont(Constants.getFont());
        button.setBounds(150, 65, 200, 25);
        getContentPane().add(button);

        button.addActionListener(e -> {
            sender.send("<JOIN>" + jt1.getText());
            // Logic for no accept repeated names or invalid names
            name = jt1.getText().replaceAll("[^a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]", "");
            validated = true;
            //Close window
            this.dispose();
        });

        this.setSize(500, 150);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public String join() {
        // Wait for validation
        while (!validated) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return name;
    }
}
