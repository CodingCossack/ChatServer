package com.codingcossack.chatserver;

import java.io.*;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler implements Runnable {
    // Wrap the output stream with a PrintWriter, auto-flushing enabled, to easily write text lines to the client
    private PrintWriter writer;
    private final Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    private String username;
    private String getUsername() {
        return username;
    }
    private void sendMessage(String message) {
        writer.println(message);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

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

            LOGGER.info("Handling Client {}", clientSocket);

            writer.println("Enter username: ");
            String username = reader.readLine();

            if (username == null || username.isEmpty()) {
                writer.println("Username cannot be empty");
                LOGGER.warn("Empty username provided");
                return; // Exit the method
            }

            if (username.length() > 20) {
                writer.println("Username too long. Enter maximum of 20 characters");
                LOGGER.warn("Username too long: {}", username);
            }

            writer.println("Enter password: ");
            String password = reader.readLine();

            if (password == null || password.isEmpty()) {
                writer.println("Password cannot be empty");
                LOGGER.warn("Empty password provided for user: {}", username);
            }

            if (password.length() > 30) {
                writer.println("Password entered too long. Enter 30 characters maximum");
                LOGGER.warn("Password entered too long: {}", username);
            }

            if (Server.userCredentials.containsKey(username) && Server.userCredentials.get(username).equals(password)) {
                writer.println("Login successful");
                // After successful authentication
                Server.clients.add(this);
                this.username = username; // assign value to username field

                LOGGER.info("User {} logged in successfully", username);

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
                        if(parts.length < 3) {
                            writer.println("Invalid private message format. Please use: /msg [username] [message]");
                            continue;
                        }
                        String recipientUsername = parts[1];
                        String actualMessage = parts[2];
                        for(ClientHandler client : Server.clients) {
                            if(client.getUsername().equals(recipientUsername)) {
                                client.sendMessage("Private message from" + username + " " + actualMessage);
                                break;
                            }
                        }
                    } else {
                        // Output the received message to the server's console
                        LOGGER.info("Received from client: {}", message);
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
                LOGGER.warn("User {} failed to login", username);
            }
        } catch (IOException e) {
                // Output any IO exceptions that occur
                LOGGER.error("An error occurred while handling client {}: {}", clientSocket, e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e){
                    LOGGER.error("An error occurred while closing client socket {}: {}", clientSocket, e);
                }
                // This block will run whether an exception is thrown or not
                Server.clients.remove(this);    // Remove client on disconnect
                LOGGER.info("Client {} disconnected", clientSocket);
            }


    }
    public void broadcastMessage(String message) {
        // Iterate through the list of connected clients and send the message to each one
        synchronized (Server.clients) {
            for (ClientHandler client : Server.clients) {
                client.writer.println(message);
            }
        }
        LOGGER.debug("Broadcast message to all clients {}", message);
    }
}