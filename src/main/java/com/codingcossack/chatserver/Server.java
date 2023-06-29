// Package declaration to organize classes into namespaces
package com.codingcossack.chatserver;

// Import necessary classes from Java standard library
import java.net.ServerSocket;          // For listening for client connections
import java.io.IOException;             // For handling input/output exceptions
import java.net.Socket;                 // For communication between server and client
import java.util.*;

public class Server {

    // A map to store username and password pairs.
    static Map<String, String> userCredentials = new HashMap<>();
    // A synchronized list to store connected clients
    static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    static {
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
    }

    public static void main(String[] args) {
        // Define the port number on which the server will listen for connections
        int portNumber = 8080;

        // Try-with-resources block to auto-close resources and handle exceptions
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started and listening on Port " + portNumber);

            // Infinite loop to keep server running and accepting client connections
            while (true) {
                // Wait for a client to connect, and obtain the socket representing the client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client is connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);

                new Thread(clientHandler).start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}