// Package declaration to organize classes into namespaces
package com.codingcossack.chatserver;

// Import necessary classes from Java standard library
import java.net.ServerSocket;          // For listening for client connections
import java.io.IOException;             // For handling input/output exceptions
import java.io.InputStream;             // For reading bytes from client socket
import java.io.OutputStream;            // For writing bytes to client socket
import java.net.Socket;                 // For communication between server and client
import java.io.BufferedReader;          // For reading text from a character-input stream
import java.io.InputStreamReader;       // For converting bytes to characters
import java.io.PrintWriter;             // For writing text to a character-output stream

public class Server {

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

                // Obtain the input stream to read data sent by the client
                InputStream inputStream = clientSocket.getInputStream();

                // Obtain the output stream to send data to the client
                OutputStream outputStream = clientSocket.getOutputStream();

                // Wrap the input stream with a BufferedReader to easily read text lines from the client
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Wrap the output stream with a PrintWriter, auto-flushing enabled, to easily write text lines to the client
                PrintWriter writer = new PrintWriter(outputStream, true);

                // Variable to store messages received from the client
                String message;

                // Infinite loop to continually read messages from the client
                while (true) {
                    // Read a line of text from the client
                    message = reader.readLine();  // This could be null if the client has disconnected

                    // If the message is null, the client has likely disconnected, so exit the loop
                    if (message == null) {
                        break;
                    }

                    // Output the received message to the server's console
                    System.out.println("Received from client: " + message);

                    // Send a response back to the client
                    writer.println("Server received: " + message);

                    // If the client sends the text "quit", exit the loop
                    if ("quit".equalsIgnoreCase(message)) {
                        break;
                    }
                }

                // Close the client socket
                clientSocket.close();
                System.out.println("Client disconnected");

            }
        } catch (IOException e) {
            // Output any IO exceptions that occur
            e.printStackTrace();
        }
    }
}