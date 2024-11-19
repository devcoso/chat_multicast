package chat.client;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Font;

public class Interfaz extends JFrame {

    JTextField jt1 = new JTextField();
    JLabel jl1 = new JLabel();

     public Interfaz() {
        getContentPane().setLayout(null);

        Font fuente = new Font("Segoe UI Emoji", Font.PLAIN, 30); 

        // Last message box
        jl1.setBounds(10, 60, 500, 40);
        jl1.setFont(fuente);
        getContentPane().add(jl1);

        // Message sender box
        jt1.setBounds(10, 10, 500, 40);
        jt1.setFont(fuente);
        getContentPane().add(jt1);    
        
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
     }

    public String getMessage() { 
        return jt1.getText();
    }

    public void setMessage(String message) {
        jl1.setText(message);
    }

}
