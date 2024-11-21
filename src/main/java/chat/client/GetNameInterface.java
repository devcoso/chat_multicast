package chat.client;

import java.net.MulticastSocket;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

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
        button.setEnabled(false);
        button.setFont(Constants.getFont());
        button.setBounds(150, 65, 200, 25);
        getContentPane().add(button);

        button.addActionListener(e -> {
            name = jt1.getText();
            sender.send("<JOIN>" + name);
            validated = true;
            //Close window
            this.dispose();
        });

        
        //Enable false when is nothing
        jt1.addKeyListener( new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                toggleButton();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                toggleButton();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                toggleButton();
            }

            private void toggleButton() {
                //Validar texto
                button.setEnabled(
                    !Pattern.compile("[^a-zA-Z0-9áéíóúñÁÉÍÓÚÑ]").matcher(jt1.getText()).find() 
                    &&
                    !jt1.getText().trim().isEmpty()
                );
            }
        }
    );

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
