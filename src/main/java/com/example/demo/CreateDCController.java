package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateDCController {
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

    public void createDC(ActionEvent event) throws IOException {
        if(!nameTextField.getText().isEmpty()){
            String username = nameTextField.getText();

            if(client.isValid(username)){
                client.startNewDirectChat(username);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/CreateDCSuccessful.fxml"));
                root = loader.load();
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }
            else{
                errorMessage.setVisible(true);
            }
        }
    }
}
