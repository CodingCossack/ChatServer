// Package declaration to organize classes into namespaces
package com.codingcossack.chatserver;

// Import necessary classes from Java standard library
import java.net.Socket;               // For communication with the server
import java.io.IOException;           // For handling input/output exceptions
import java.io.InputStream;           // For reading bytes from server socket
import java.io.OutputStream;          // For writing bytes to server socket
import java.io.BufferedReader;        // For reading text from a character-input stream
import java.io.InputStreamReader;   // For converting bytes to characters
import java.io.PrintWriter;           // For writing text to a character-output stream
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) {
        int portNumber = 8080;

        try (Socket socket = new Socket("localhost", portNumber)) {
            // Get the input stream associated with the socket, to receive data from the server.
            InputStream inputStream = socket.getInputStream();
            // Get the output stream associated with the socket, to send data to the server.
            OutputStream outputStream = socket.getOutputStream();
            // Create a BufferedReader to read text from the input stream.
            // InputStreamReader converts bytes from input stream to characters.
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // Create a PrintWriter to write text to the output stream.
            // 'true' indicates it should auto flush after each write operation.
            PrintWriter writer = new PrintWriter(outputStream, true);

            // Create a BufferedReader to read input from the console (keyboard)
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String serverPrompt = reader.readLine();
            System.out.println(serverPrompt); // Should print "Enter username:"
            String username = consoleReader.readLine();
            writer.println(username);

            serverPrompt = reader.readLine();
            System.out.println(serverPrompt); // Should print "Enter password:"
            String password = consoleReader.readLine();
            writer.println(password);

            String loginResult = reader.readLine();
            System.out.println(loginResult);

            if ("Login successful".equals(loginResult)) {
                while (true) {
                    // Read a message from the user's input (console).
                    String message = consoleReader.readLine();

                    LOGGER.info("Enter a message to send to the server: {}", message);
                    // Break the loop if the user wants to quit. For example, if the message is "quit".
                    if ("quit".equalsIgnoreCase(message)) {
                        break;
                    }
                    // Send the message to the server.
                    writer.println(message);
                    // 3. Read the server's response and print it.
                    String serverResponse = reader.readLine();
                    System.out.println("Server responded: " + serverResponse);
                }
            }
        } catch(IOException e){
            LOGGER.error("An error occurred while trying to communicate with the server {}", e);
        }
    }
}