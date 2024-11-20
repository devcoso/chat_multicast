package chat.client;

import chat.Constants;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interfaz extends JFrame {

    JTextField jt1 = new JTextField();
    JLabel jl1 = new JLabel();

     public Interfaz(Sender sender) {
        getContentPane().setLayout(null);


        // Last message box
        jl1.setBounds(10, 60, 500, 40);
        jl1.setFont(Constants.getFont(5));
        getContentPane().add(jl1);

        // Message sender box
        jt1.setBounds(10, 10, 500, 40);
        jt1.setFont(Constants.getFont(12));
        getContentPane().add(jt1);    
        
        // Botton to send messsage
        JButton button = new JButton("Enviar mensaje");
        button.setBounds(10, 100, 100, 10);
        getContentPane().add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sender.send(jt1.getText());
                jt1.setText("");
            }
        });

        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
     }

    public void setMessage(String message) {
        jl1.setText(message);
    }

}
