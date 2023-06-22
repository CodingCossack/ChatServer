package com.codingcossack.chatserver;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    // Wrap the output stream with a PrintWriter, auto-flushing enabled, to easily write text lines to the client
    private PrintWriter writer;
    private final Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    private String username;
    private String password;
    private String getUsername() {
        return username;
    }
    private void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        try {
            // Obtain the input stream to read data sent by the client
            InputStream inputStream = clientSocket.getInputStream();

            // Obtain the output stream to send data to the client
            OutputStream outputStream = clientSocket.getOutputStream();

            // Wrap the input stream with a BufferedReader to easily read text lines from the client
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Wrap the output stream with a PrintWriter, auto-flushing enabled, to easily write text lines to the client
            writer = new PrintWriter(outputStream, true);

            writer.println("Enter username: ");
            String username = reader.readLine();

            writer.println("Enter password: ");
            String password = reader.readLine();

            if (Server.userCredentials.containsKey(username) && Server.userCredentials.get(username).equals(password)) {
                writer.println("Login successful");
                // After successful authentication
                Server.clients.add(this);

                // Infinite loop to continually read messages from the client
                while (true) {
                    // Read a line of text from the client
                    String message = reader.readLine();  // This could be null if the client has disconnected

                    // If the message is null, the client has likely disconnected, so exit the loop
                    if (message == null) {
                        break;
                    }
                    // Check if the message is a private message
                    if(message.startsWith("/msg")) {
                        // Split the message into parts
                        String[] parts = message.split(" ", 3); // Limiting to 3 parts: "/msg", "[username]", "[message]"
                        String recipientUsername = parts[1];
                        String actualMessage = parts[2];
                        for(ClientHandler client : Server.clients) {
                            if(ClientHandler.getUsername().equals(recipientUsername)) {
                                client.sendMessage("Private message from" + username + " " + actualMessage);
                                break;
                            }
                        }
                    } else {
                        // Output the received message to the server's console
                        System.out.println("Received from client: " + message);
                        // Send a response back to the client
                        broadcastMessage("Message from " + username + ": " + message);
                    }
                    // If the client sends the text "quit", exit the loop
                    if ("quit".equalsIgnoreCase(message)) {
                        break;
                    }
                }
            } else {
                writer.println("Login failed");
            }
        } catch (IOException e) {
                // Output any IO exceptions that occur
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                // This block will run whether an exception is thrown or not
                Server.clients.remove(this);    // Remove client on disconnect
                System.out.println("Client disconnected");
            }


    }
    public void broadcastMessage(String message) {
        // Iterate through the list of connected clients and send the message to each one
        synchronized (Server.clients) {
            for (ClientHandler client : Server.clients) {
                client.writer.println(message);
            }
        }
    }
}