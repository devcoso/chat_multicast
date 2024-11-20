package chat.client;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import chat.Constants;

public class GetNameIntarfaz extends JFrame {

    Boolean validated = false;

    public GetNameIntarfaz(Sender sender, MulticastSocket socket) {
        getContentPane().setLayout(null);

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
            // Logic for no accept repeated names
            validated = true;
            //Close window
            this.dispose();
        });

        this.setSize(500, 150);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void join() {
        // Wait for validation
        while (!validated) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
