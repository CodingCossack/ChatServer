package com.codingcossack.chatserver;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int portNumber = 8080;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started and listening on Port " + portNumber);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("An new client is connected: " + clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
