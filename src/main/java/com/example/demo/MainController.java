package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
// import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
// import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Circle userCircle;
    @FXML
    private Button directChatBtn;
    @FXML
    private Button groupChatBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private TextField searchTextField;
    @FXML
    private VBox chatVBox;
    @FXML
    private HBox topHBox;
    @FXML
    private HBox messageHBox;
    private TextField messageTextField;

    @FXML
    private VBox messageVBox;
    @FXML
    private ScrollPane messageSP;

    public Client client2;

    public void setClient (Client client){
        client2 = client;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            InputStream stream = getClass().getResourceAsStream("user.png");
            if (stream != null) {
                Image userImage = new Image(stream);
                userCircle.setFill(new ImagePattern(userImage));
            } else {
                System.err.println("Failed to load image: user4.png");
            }
        } catch (Exception e) {
            System.out.println("errrrrrrrrrrrror");
        }

        messageVBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                messageSP.setVvalue((Double) newValue);
            }
        });

//        showDirectChats();
    }

    public void showDirectChats() {
        groupChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        directChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 3px; -fx-border-color: #ffffff;");
        chatVBox.getChildren().clear();

        //get direct chat usernames and store in array
        Account acc = client2.userAccount;
        ArrayList<String> usernames = new ArrayList<>();
        for(DirectChat dc : acc.direct_chats){
            if(!dc.participants[0].equals(acc.getUsername())){
                usernames.add(dc.participants[0]);
            }
            else{
                usernames.add(dc.participants[1]);
            }
        }
        createDirectChats(usernames);
    }

    public void showGroupChats(){
        directChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        groupChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 3px; -fx-border-color: #ffffff;");

        chatVBox.getChildren().clear();

        //get group chat names
        ArrayList<String> groupChatNames = new ArrayList<>();

        Account acc = client2.userAccount;
        for(GroupChat gc : acc.group_chats){
            groupChatNames.add(gc.groupName);
        }

        createGroupChats(groupChatNames);
    }

    public void createDirectChats(ArrayList<String> usernames){
        for (String username : usernames) {
            Circle userCircle = new Circle(35.0);
            userCircle.setFill(Color.web("#70bef5"));
            userCircle.setStroke(Color.WHITE);
            userCircle.setStrokeWidth(2.0);

            try {
                InputStream stream = getClass().getResourceAsStream("user5.png");
                if (stream != null) {
                    Image userImage = new Image(stream);
                    userCircle.setFill(new ImagePattern(userImage));
                } else {
                    System.err.println("Failed to load image: user5.png");
                }
            } catch (Exception e) {
                System.out.println("error");
            }

            Label usernameLabel = new Label(username);
            usernameLabel.setStyle("-fx-font-size: 17.0;");

            HBox userHBox = new HBox();
            userHBox.setAlignment(Pos.CENTER_LEFT);
            userHBox.setPrefHeight(80);
            userHBox.setPrefWidth(310);
            userHBox.setSpacing(15.0);
            userHBox.setStyle("-fx-border-color: #f8f8f8; -fx-border-width: 2px 0px 2px 0px;");

            userHBox.getChildren().addAll(userCircle, usernameLabel);

            userHBox.setOnMouseClicked(event -> {
                String name = null;
                for (Node node : userHBox.getChildren()) {
                    if (node instanceof Label label) {
                        name = label.getText();
                        break;
                    }
                }
                filltopHBox(name);
                fillMessageHBox();
                messageVBox.getChildren().clear();

                //get array of messages that have been sent by usernames[i] to display
                //also get array of messages that have been sent to usernames[i]
                DirectChat directChat = null;
                for(DirectChat dc : client2.userAccount.direct_chats){
                    if(dc.participants[0].equals(name) || dc.participants[1].equals(name)){
                        for(Message msg : dc.messages){
                            if(msg.sender.equals(client2.userAccount.getUsername())){
                                addMessageByMe(msg.text, client2.userAccount.getUsername());
                            }
                            else{
                                addMessageByServer(msg.text, msg.sender);
                            }
                        }
                        break;
                    }
                }
//                String[] messagesSent = {"Hi", "How are you?", "I am also", "a", "b", "aodjqiojwd iojadiojad jawajdajwidjai ojdioaw jdoiajdio aj asd asd asd ad ad ad a dad ad ad asd ad a as ad ad asd ad ad asd asd ad a a dasd as das dad ad a dad a as as das as as a a as ad as as a a asf sf sfg dfg dfg sf sfsas asd as sdgs fewsfewsfsf sf afewsf eaf as fas", "a", "e", "z", "x", "c"};
//                String[] messagesReceived = {"Hello", "I am doing fine how about you?", "That's good", "a", "b", "c", "d", "a", "z", "x", "c"};
//
//                for(int i=0 ; i<messagesSent.length ; i++){
//                    addMessageByMe(messagesSent[i]);
//                    addMessageByServer(messagesReceived[i]);
//                }
            });

            userHBox.setOnMouseEntered(event -> {
                userHBox.setCursor(Cursor.HAND);
            });

            userHBox.setOnMouseExited(event -> {
                userHBox.setCursor(Cursor.DEFAULT);
            });

            chatVBox.getChildren().add(userHBox);
        }
    }

    public void createGroupChats(ArrayList<String> groupNames){
        for (String username : groupNames) {
            Circle userCircle = new Circle(35.0);
            userCircle.setFill(Color.web("#70bef5"));
            userCircle.setStroke(Color.WHITE);
            userCircle.setStrokeWidth(2.0);

            try {
                InputStream stream = getClass().getResourceAsStream("user5.png");
                if (stream != null) {
                    Image userImage = new Image(stream);
                    userCircle.setFill(new ImagePattern(userImage));
                } else {
                    System.err.println("Failed to load image: user5.png");
                }
            } catch (Exception e) {
                System.out.println("error");
            }

            Label usernameLabel = new Label(username);
            usernameLabel.setStyle("-fx-font-size: 17.0;");

            HBox userHBox = new HBox();
            userHBox.setAlignment(Pos.CENTER_LEFT);
            userHBox.setPrefHeight(80);
            userHBox.setPrefWidth(310);
            userHBox.setSpacing(15.0);
            userHBox.setStyle("-fx-border-color: #f8f8f8; -fx-border-width: 2px 0px 2px 0px;");

            userHBox.getChildren().addAll(userCircle, usernameLabel);

            userHBox.setOnMouseClicked(event -> {
                String name = null;
                for (Node node : userHBox.getChildren()) {
                    if (node instanceof Label label) {
                        name = label.getText();
                        break;
                    }
                }
                filltopHBox(name);
                fillMessageHBox();
                messageVBox.getChildren().clear();

                //get array of messages that have been sent by usernames[i] to display
                //also get array of messages that have been sent to usernames[i]
                GroupChat groupChat = null;
                for(GroupChat gc : client2.userAccount.group_chats){
                    if(gc.groupName.equals(name)){
                        for(Message msg : gc.messages){
                            if(msg.sender.equals(client2.userAccount.getUsername())){
                                addMessageByMe(msg.text, client2.userAccount.getUsername());
                            }
                            else{
                                addMessageByServer(msg.text, msg.sender);
                            }
                        }
                        break;
                    }
                }
//                String[] messagesSent = {"Hi", "How are you?", "I am also", "a", "b", "aodjqiojwd iojadiojad jawajdajwidjai ojdioaw jdoiajdio aj asd asd asd ad ad ad a dad ad ad asd ad a as ad ad asd ad ad asd asd ad a a dasd as das dad ad a dad a as as das as as a a as ad as as a a asf sf sfg dfg dfg sf sfsas asd as sdgs fewsfewsfsf sf afewsf eaf as fas", "a", "e", "z", "x", "c"};
//                String[] messagesReceived = {"Hello", "I am doing fine how about you?", "That's good", "a", "b", "c", "d", "a", "z", "x", "c"};
//
//                for(int i=0 ; i<messagesSent.length ; i++){
//                    addMessageByMe(messagesSent[i]);
//                    addMessageByServer(messagesReceived[i]);
//                }
            });

            userHBox.setOnMouseEntered(event -> {
                userHBox.setCursor(Cursor.HAND);
            });

            userHBox.setOnMouseExited(event -> {
                userHBox.setCursor(Cursor.DEFAULT);
            });

            chatVBox.getChildren().add(userHBox);
        }
    }

    public void filltopHBox(String username){
        topHBox.getChildren().clear();

        Circle userCircle = new Circle(38.0);
        userCircle.setFill(Color.web("#70bef5"));
        userCircle.setStroke(Color.WHITE);
        userCircle.setStrokeWidth(2.0);

        try {
            InputStream stream = getClass().getResourceAsStream("user5.png");
            if (stream != null) {
                Image userImage = new Image(stream);
                userCircle.setFill(new ImagePattern(userImage));
            } else {
                System.err.println("Failed to load image: user5.png");
            }
        } catch (Exception e) {
            System.out.println("error");
        }

        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 20.0;");

        topHBox.getChildren().addAll(userCircle, usernameLabel);
    }

    public void fillMessageHBox(){
        messageHBox.getChildren().clear();

        messageTextField = new TextField();
        messageTextField.setPromptText("Message");
        messageTextField.setPrefHeight(48);
        messageTextField.setPrefWidth(612);
        messageTextField.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 50px; -fx-padding: 0 0 0 30;");
        messageTextField.setFont(new Font(17));

        ImageView sendIcon = new ImageView(new Image(getClass().getResourceAsStream("send.png")));
        sendIcon.setFitHeight(42);
        sendIcon.setFitWidth(38);

        sendIcon.setOnMouseClicked(event -> {
            sendMessage();
        });

        sendIcon.setOnMouseEntered(event -> {
            sendIcon.setCursor(Cursor.HAND);
        });

        sendIcon.setOnMouseExited(event -> {
            sendIcon.setCursor(Cursor.DEFAULT);
        });

        messageHBox.getChildren().addAll(messageTextField, sendIcon);

        StackPane.setMargin(messageHBox, new Insets(632, 0, 0, 494));
    }

    public void sendMessage(){
        if(!messageTextField.getText().isEmpty()){
            String message = messageTextField.getText();

            Label labelToFind = null;
            for (javafx.scene.Node node : topHBox.getChildren()) {
                if (node instanceof Label) {
                    labelToFind = (Label) node;
                    break; // Stop searching once you find the label
                }
            }

            try{
                client2.sendDirectMessage(message, labelToFind.getText());
                addMessageByMe(message, client2.userAccount.getUsername());
            }catch(Exception e){
                e.printStackTrace();
            }

            messageTextField.clear();
        }
    }

//    public void addMessage(String message, boolean sentByMe){
//        HBox hbox = new HBox();
//        hbox.setPadding(new Insets(5, 5, 5, 10));
//        hbox.setStyle("-fx-background-color: transparent;");
//        hbox.setAlignment(Pos.CENTER_LEFT);
//
//        Label text = new Label(message);
//        text.setFont(new Font(15));
//
//
//
//        if (sentByMe) {
//            text.setBackground(new Background(new BackgroundFill(Color.rgb(112, 190, 245), new CornerRadii(10), Insets.EMPTY)));
//            text.setTextFill(Color.BLACK);
//        } else {
//            text.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
//            text.setTextFill(Color.BLACK);
//        }
//
//        text.setPadding(new Insets(8, 12, 8, 12));
//
//        HBox.setHgrow(text, javafx.scene.layout.Priority.ALWAYS);
//
//        hbox.getChildren().add(text);
//
//        BorderPane borderPane = new BorderPane();
//
//        if(sentByMe){
//            borderPane.setRight(hbox);
//        }
//        else{
//            borderPane.setLeft(hbox);
//        }
//
//        messageVBox.getChildren().add(borderPane);
//    }

    public void addMessageByMe(String message, String sender){
        HBox hbox = new HBox(2);
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setAlignment(Pos.CENTER_RIGHT);

        Text text = new Text(message);
        text.setFont(Font.font(15.0));
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(112, 190, 245);" + " -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        text.setFill(Color.color(1.0, 1.0, 1.0));

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("gear.png")));
        icon.setFitHeight(21);
        icon.setFitWidth(19);
        icon.setVisible(false);

        icon.setOnMouseEntered(event -> {
            icon.setCursor(Cursor.HAND);
        });

        icon.setOnMouseExited(event -> {
            icon.setCursor(Cursor.DEFAULT);
        });

        hbox.getChildren().addAll(icon, textFlow);

        hbox.setOnMouseEntered(event -> {
            icon.setVisible(true);
        });

        hbox.setOnMouseExited(event -> {
            icon.setVisible(false);
        });

        VBox vbox = new VBox(1);

        Label name = new Label();
        name.setText(sender);
        name.setAlignment(Pos.CENTER_RIGHT);
        name.setVisible(false);
        name.setPadding(new Insets(0, 0, 0, 10));
        name.setStyle("-fx-font-size: 1; -fx-text-fill: gray;");

        vbox.getChildren().addAll(name, hbox);

        messageVBox.getChildren().add(vbox);
    }

    public void addMessageByServer(String message, String sender){
        HBox hbox = new HBox(2);
        hbox.setPadding(new Insets(5, 10, 5, 10));
        hbox.setAlignment(Pos.CENTER_LEFT);

        Text text = new Text(message);
        text.setFont(Font.font(15.0));
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(255,255,255);" + " -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        text.setFill(Color.color(0.0, 0.0, 0.0));

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("gear.png")));
        icon.setFitHeight(21);
        icon.setFitWidth(19);
        icon.setVisible(false);

        icon.setOnMouseEntered(event -> {
            icon.setCursor(Cursor.HAND);
        });

        icon.setOnMouseExited(event -> {
            icon.setCursor(Cursor.DEFAULT);
        });

        hbox.getChildren().addAll(textFlow, icon);

        hbox.setOnMouseEntered(event -> {
            icon.setVisible(true);
        });

        hbox.setOnMouseExited(event -> {
            icon.setVisible(false);
        });

        VBox vbox = new VBox(1);

        Label name = new Label();
        name.setAlignment(Pos.CENTER_LEFT);
        name.setText(sender);
        name.setPadding(new Insets(0, 0, 0, 10));
        name.setStyle("-fx-font-size: 13; -fx-text-fill: gray;");

        vbox.getChildren().addAll(name, hbox);

        messageVBox.getChildren().add(vbox);
    }

    public void showProfile(){
        directChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        groupChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
        profileBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 3px; -fx-border-color: #ffffff;");

        chatVBox.getChildren().clear();
        topHBox.getChildren().clear();
        messageHBox.getChildren().clear();
        messageVBox.getChildren().clear();
    }

    public void searchDirectChat(){
        String user = searchTextField.getText();

        ArrayList<String> pingpong = new ArrayList<>();
        pingpong.add(user);
        createDirectChats(pingpong);



        //logic to get user from that name
    }

    public void searchGroupChat(){
        String user = searchTextField.getText();

        ArrayList<String> pingpong = new ArrayList<>();
        pingpong.add(user);
        createGroupChats(pingpong);
    }

    public void search(){

    }

}
