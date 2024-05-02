package com.example.demo;

import java.io.*;
import java.net.*;

public class Server {
    private static final int port = 27508;
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println(" Client Connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(port);
        Server  server = new Server(serverSocket);
        server.startServer();
    }
}

