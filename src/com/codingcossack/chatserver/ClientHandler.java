package com.codingcossack.chatserver;

import java.io.*;
import java.net.Socket;
import java.io.InputStream;             // For reading bytes from client socket
import java.io.OutputStream;            // For writing bytes to client socket
import java.io.BufferedReader;          // For reading text from a character-input stream
import java.io.InputStreamReader;       // For converting bytes to characters
import java.io.PrintWriter;             // For writing text to a character-output stream

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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

        } catch (IOException e) {
            // Output any IO exceptions that occur
            e.printStackTrace();
            }
    }
}