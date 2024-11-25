package chat.client;

import chat.Constants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Chat extends JFrame {

    private JTextArea messageLog;
    private JTextArea userLog;
    private Sender sender;

     public Chat(Sender sender) {
        this.sender = sender;
        setTitle("Chat de " + sender.getName()); 
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10)); // Espaciado horizontal y vertical
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Márgenes
        add(mainPanel);

        messageLog = new JTextArea();
        messageLog.setEditable(false);
        messageLog.setFont(Constants.getFont());      

        userLog = new JTextArea();
        userLog.setEditable(false);
        userLog.setFont(Constants.getFont());

        JSplitPane outputPanel = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(messageLog),
            new JScrollPane(userLog)
        );
        outputPanel.setDividerLocation(600);
            
        mainPanel.add(outputPanel, BorderLayout.CENTER);
        
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Enviar");
        JButton sendFileButton = new JButton("📄"); 
        sendButton.setFont(Constants.getFont());
        sendFileButton.setFont(Constants.getFont());
        sendButton.setEnabled(false);
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(sendFileButton, BorderLayout.WEST);
        buttonPanel.add(sendButton, BorderLayout.CENTER);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        inputField.setFont(Constants.getFont());

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sender.sendMessage(inputField.getText());
                sendButton.setEnabled(false);
                inputField.setText("");
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                sender.sendFile();
            }
        }); 

        //Enable false when is nothing
        inputField.addKeyListener( new KeyListener() {
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
                    // Activar o desactivar el botón dependiendo si el texto está vacío
                    sendButton.setEnabled(!inputField.getText().trim().isEmpty());
                }
            }
        );

        setSize(800, 500);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
        setVisible(true);
        		//Cerrar conenxion cuando se cierre la ventana
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				int resp = JOptionPane.showConfirmDialog(null, "¿Desea salir del chat?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp == JOptionPane.YES_OPTION) {
					sender.send("<LEAVE>" + sender.getName());
					System.exit(0);
				}
			}
		});
    }

    public void addMessage(String message, String from) {
        messageLog.append(from + ": " + message + "\n");
    }

    public void addPrivateMessage(String message, String from, String to) {
        if(to.equals(sender.getName())) {
            messageLog.append(from + " te ha susurrado " + ": " + message + "\n");
        } else if(from.equals(sender.getName())) {
            messageLog.append("Has susurrado a " + to + ": " + message + "\n");
        }
    }

    public void updateUsers(String users) {
        String userFormat = users.replace(",", "\n");
        userLog.setText(userFormat);
    }

    public void addFile(String fileName, String from, String file) {
        if(!from.equals(sender.getName())) {
            messageLog.append(from + " te ha enviado un archivo " + fileName + "\n");
        } else {
            messageLog.append("Has enviado un archivo : " + fileName + "\n");
        }
    }

}
