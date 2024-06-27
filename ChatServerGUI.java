import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerGUI {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;

    public ChatServerGUI() {
        frame = new JFrame("Chat Server");
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
            broadcastMessage("Server: " + message, null);
            textArea.append("Server: " + message + "\n");
            textField.setText("");
        }
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            textArea.append("Server started...\n");
            while (true) {
                Socket socket = serverSocket.accept();
                textArea.append("New client connected: " + socket.getInetAddress().getHostAddress() + "\n");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != excludeClient) {
                clientHandler.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }

    public static void main(String[] args) {
        ChatServerGUI server = new ChatServerGUI();
        server.startServer();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    textArea.append("Received: " + message + "\n");
                    broadcastMessage(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
