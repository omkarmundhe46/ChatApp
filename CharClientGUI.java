import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClientGUI {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;

    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;

    public ChatClientGUI() {
        frame = new JFrame("Chat Client");
        textArea = new JTextArea(20, 50);
        textField = new JTextField(40);
        sendButton = new JButton("Send");

        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel();
        panel.add(textField);
        panel.add(sendButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = textField.getText();
        if (message != null && !message.trim().isEmpty()) {
            out.println(message);
            textField.setText("");
        }
    }

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new MessageReader(in)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClientGUI client = new ChatClientGUI();
        client.start();
    }

    private class MessageReader implements Runnable {
        private BufferedReader in;

        public MessageReader(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    textArea.append("Server: " + message + "\n");
                }
            } catch (IOException e) {
                if (e.getMessage().equals("Stream closed")) {
                    textArea.append("Connection to server lost.\n");
                } else {
                    e.printStackTrace();
                }
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
