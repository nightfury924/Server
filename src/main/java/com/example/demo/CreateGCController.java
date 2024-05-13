package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateGCController implements Initializable {
    private Client client;
    private Stage stage;
    private Parent root;
    private Scene scene;

    @FXML
    private ImageView createImage;

    @FXML
    private TextField nameTextField;

    @FXML
    private Label errorMessage;

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void createGC() throws IOException {
        if(!nameTextField.getText().isEmpty()){
            String username = nameTextField.getText();

            client.validateGroupName(username);
            int x=0;
            while(true){
                if(Client.vGroupNameVar == 1){
                    x=1;
                    Client.vGroupNameVar = 0;
                    break;
                }else if(Client.vGroupNameVar == 2){
                    Client.vGroupNameVar = 0;
                    break;
                }
            }
            if(x == 1){
                client.startNewGroupChat(username);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/CreateGCSuccessful.fxml"));
                root = loader.load();
                stage = (Stage)nameTextField.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }else{
                errorMessage.setVisible(true);
            }
        }
    }
}
