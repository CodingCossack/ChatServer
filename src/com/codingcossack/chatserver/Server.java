// Package declaration to organize classes into namespaces
package com.codingcossack.chatserver;

// Import necessary classes from Java standard library
import java.net.ServerSocket;  // For listening for client connections
import java.io.IOException;     // For handling input/output exceptions
import java.io.InputStream;     // For reading bytes from client socket
import java.io.OutputStream;    // For writing bytes to client socket
import java.net.Socket;         // For communication between server and client
import java.io.BufferedReader;  // For reading text from a character-input stream
import java.io.InputStreamReader; // For converting bytes to characters
import java.io.PrintWriter;     // For writing text to a character-output stream

// Define the Server class
public class Server {

    // Define the main method as the entry point of the application
    public static void main(String[] args) {

        // Define the port number on which the server will listen for client connections
        int portNumber = 8080;

        // Try to create a server socket and listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            // Display that the server has started and is listening on the specified port
            System.out.println("Server started and listening on Port " + portNumber);

            // Infinite loop to keep the server running and accepting client connections
            while (true) {

                // Accept a new client connection and obtain the socket for communication
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client is connected: " + clientSocket);

                // Obtain the raw input stream from the client socket
                InputStream inputStream = clientSocket.getInputStream();

                // Obtain the raw output stream to the client socket
                OutputStream outputStream = clientSocket.getOutputStream();

                // Wrap the raw input stream with BufferedReader for efficient reading of text data
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                // Wrap the raw output stream with PrintWriter for convenient writing of text data.
                // 'true' argument indicates that PrintWriter should auto-flush the stream.
                PrintWriter writer = new PrintWriter(outputStream, true);

            }

        } catch (IOException e) {
            // Handle any IOException that might occur during network communication
            e.printStackTrace();
        }
    }
}