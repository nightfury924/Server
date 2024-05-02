package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;
import java.util.*;

public class ClientApplication extends Application {

    static Scanner sc = new Scanner(System.in);
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 27508;

    @Override
    public void start(Stage primaryStage) {
        try {
            Socket socKet=new Socket(Client.SERVER_IP, Client.SERVER_PORT);;
            Client client = new Client(socKet);
         //   client.startListening();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Start.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();
            if(loginController == null){
                System.out.println("123");
            }
            System.out.println("abc");
            loginController.setSocket(socKet);
            loginController.setClient(client);

            if(loginController.socKet == null){
                System.out.println("socket is null");
            }
            else{
                System.out.println("socket is not null");
            }

            if(loginController.client == null){
                System.out.printf("client is null");
            }
            else{
                System.out.println("client is not null");
            }


            //Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo/Start.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

//    public static void main(String[] args) {
//        launch(args);
//    }
}
