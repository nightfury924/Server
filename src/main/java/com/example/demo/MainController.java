package com.example.demo;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.Parent;
import javafx.event.ActionEvent;


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
    private HBox iconHBox;
    @FXML
    private HBox messageHBox;
    private TextField messageTextField;

    @FXML
    private HBox searchHBox;

    private HBox targetHBox;

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
        createDMSearchBox();

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
                fillDirectMessageHBox();
                messageVBox.getChildren().clear();

                //get array of messages that have been sent by usernames[i] to display
                //also get array of messages that have been sent to usernames[i]
                DirectChat directChat = null;
                for(DirectChat dc : client2.userAccount.direct_chats){
                    directChat = dc;
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
                ArrayList<Message> oldMessages = new ArrayList<>();
                oldMessages.addAll(directChat.messages);
                ArrayList<Message> newMessages = directChat.messages;

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run(){
                        Platform.runLater(() -> {
                            if (checkLists(oldMessages, newMessages)){
                                MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null);
                                Event.fireEvent(userHBox, mouseEvent);

                                oldMessages.clear();
                                oldMessages.addAll(newMessages);
                            }
                        });
                    }
                }, 0, 1000);
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
        createGCSearchBox();

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
                fillGroupMessageHBox();
                messageVBox.getChildren().clear();

                //get array of messages that have been sent by usernames[i] to display
                //also get array of messages that have been sent to usernames[i]
                GroupChat groupChat = null;
                for(GroupChat gc : client2.userAccount.group_chats){
                    if(gc.groupName.equals(name)){
                        groupChat = gc;
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

//                Message[] oldMessages = new Message[groupChat.messages.size()];
//                for(int i=0 ; i<groupChat.messages.size() ; i++){
//                    oldMessages[i] = groupChat.messages.get(i);
//                }
                ArrayList<Message> oldMessages = new ArrayList<>(groupChat.messages);
                ArrayList<Message> newMessages = groupChat.messages;

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run(){
                        Platform.runLater(() -> {
                            if (checkLists(oldMessages, newMessages)){
                                MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null);
                                Event.fireEvent(userHBox, mouseEvent);

                                oldMessages.clear();
                                oldMessages.addAll(newMessages);
                            }
                        });
                    }
                }, 0, 1000);
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

    public boolean checkLists(ArrayList<Message> old, ArrayList<Message> newlist){
        if(old.size() != newlist.size()){
            return true;
        }
        else if(checkEachMsg(old, newlist)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkEachMsg(ArrayList<Message> old, ArrayList<Message> newlist){
        for(int i=0 ; i<old.size() ; i++){
            Message oldMsg = old.get(i);
            Message newMsg = newlist.get(i);

            // Check if any aspect of the messages is different
            if (!oldMsg.equals(newMsg)) {
                return true;
            }
        }

        return false;
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

    public void fillDirectMessageHBox(){
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
            sendDirectMessage();
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

    public void fillGroupMessageHBox(){
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
            sendGroupMessage();
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

    public void fillMessageHBoxWithEdit(){
        messageHBox.getChildren().clear();

        messageTextField = new TextField();
        messageTextField.setPromptText("Type edited message ... ");
        messageTextField.setPrefHeight(48);
        messageTextField.setPrefWidth(612);
        messageTextField.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 50px; -fx-padding: 0 0 0 30;");
        messageTextField.setFont(new Font(17));

        ImageView sendIcon = new ImageView(new Image(getClass().getResourceAsStream("send.png")));
        sendIcon.setFitHeight(42);
        sendIcon.setFitWidth(38);

        sendIcon.setOnMouseClicked(event -> {
            String newMsg = messageTextField.getText();
            messageTextField.clear();
            int i=0;
            int f=0;
            for(Node x : messageVBox.getChildren()) {
                if(x instanceof VBox){
                    VBox vbox = (VBox) x;
                    System.out.println("1");

                    for(Node y : vbox.getChildren()) {
                        if(y instanceof HBox){
                            HBox hbox = (HBox) y;
                            System.out.println("2");

                            if (hbox.equals(targetHBox)) {
                                Label labelToFind = null;
                                for (javafx.scene.Node node : topHBox.getChildren()) {
                                    if (node instanceof Label) {
                                        labelToFind = (Label) node;
                                        System.out.println("a");
                                        break;
                                    }
                                    System.out.println("b");
                                }
                                System.out.println("c");

                                try{
                                    client2.editMessage(labelToFind.getText(), i, newMsg);
                                } catch(Exception e){
                                    System.out.println("abc");
                                    e.printStackTrace();
                                }
                                f=1;
                                break;
                            }
                            else{
                                i++;
                            }
                        }
                    }

                    if(f == 1){
                        break;
                    }
                }
                System.out.println("e");
            }
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

    public void sendDirectMessage(){
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
//                addMessageByMe(message, client2.userAccount.getUsername());
            }catch(Exception e){
                System.out.println("def");
                e.printStackTrace();
            }

            messageTextField.clear();
        }
    }

    public void sendGroupMessage(){
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
                client2.sendGroupMessage(message, labelToFind.getText());
//                addMessageByMe(message, client2.userAccount.getUsername());
            }catch(Exception e){
                System.out.println("ghi");
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

        icon.setOnMouseClicked(event -> {
            targetHBox = hbox;
            addEditAndDelete();
        });

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
        name.setAlignment(Pos.CENTER_LEFT);
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

        icon.setOnMouseClicked(event ->{
            targetHBox = hbox;
            addEditAndDelete();
        });

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

    public void addEditAndDelete(){
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("delete.png")));
        deleteIcon.setFitHeight(24);
        deleteIcon.setFitWidth(24);

        ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("edit.png")));
        editIcon.setFitHeight(24);
        editIcon.setFitWidth(24);

        editIcon.setOnMouseClicked(event ->{
            editMessage();
            iconHBox.getChildren().clear();
        });

        editIcon.setOnMouseEntered(event -> {
            editIcon.setCursor(Cursor.HAND);
        });

        editIcon.setOnMouseExited(event -> {
            editIcon.setCursor(Cursor.DEFAULT);
        });

        deleteIcon.setOnMouseClicked(event ->{
            deleteMessage();
            iconHBox.getChildren().clear();
        });

        deleteIcon.setOnMouseEntered(event -> {
            deleteIcon.setCursor(Cursor.HAND);
        });

        deleteIcon.setOnMouseExited(event -> {
            deleteIcon.setCursor(Cursor.DEFAULT);
        });



        iconHBox.getChildren().clear();

        iconHBox.getChildren().addAll(editIcon, deleteIcon);
    }

    public void showProfile(){
//        directChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
//        groupChatBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 0px; -fx-border-color: #70bef5;");
//        profileBtn.setStyle("-fx-background-color: transparent; -fx-cursor: HAND; -fx-alignment: baseline-left; -fx-border-width: 0px 0px 0px 3px; -fx-border-color: #ffffff;");

//        chatVBox.getChildren().clear();
//        topHBox.getChildren().clear();
//        messageHBox.getChildren().clear();
//        messageVBox.getChildren().clear();
    }

    public void searchDM(){
        if(!searchTextField.getText().isEmpty()){
            String user = searchTextField.getText();

            ArrayList<String> pingpong = new ArrayList<>();
            pingpong.add(user);
            createDirectChats(pingpong);
        }
    }

    public void searchGC(){
        String user = searchTextField.getText();

        ArrayList<String> pingpong = new ArrayList<>();
        pingpong.add(user);
        createGroupChats(pingpong);
    }

    public void createDMSearchBox(){
        if(!searchHBox.getChildren().isEmpty()){
            searchHBox.getChildren().clear();
        }

        ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("search.png")));
        searchIcon.setFitWidth(32);
        searchIcon.setFitHeight(32);

        HBox.setMargin(searchIcon, new Insets(27, 0, 0, 5));

        TextField searchTextField = new TextField();
        searchTextField.setPrefHeight(36);
        searchTextField.setPrefWidth(202);
        searchTextField.setPromptText("Search for chats ...");
        searchTextField.setStyle("-fx-background-color: transparent; -fx-border-width: 0px 0px 2px 0px; -fx-border-color: #70bef5;");
        searchTextField.setFont(Font.font("Ebrima", 13));

        HBox.setMargin(searchTextField, new Insets(20, 0, 0, 5));

        ImageView searchArrowIcon = new ImageView(new Image(getClass().getResourceAsStream("searchArrow.png")));
        searchArrowIcon.setFitHeight(32);
        searchArrowIcon.setFitWidth(32);

        searchArrowIcon.setOnMouseClicked(event -> {
            searchDM();
        });

        searchArrowIcon.setOnMouseEntered(event -> {
            searchArrowIcon.setCursor(Cursor.HAND);
        });

        searchArrowIcon.setOnMouseExited(event -> {
            searchArrowIcon.setCursor(Cursor.DEFAULT);
        });

        ImageView createChatIcon = new ImageView(new Image(getClass().getResourceAsStream("createChat.png")));
        createChatIcon.setFitHeight(32);
        createChatIcon.setFitWidth(32);

        createChatIcon.setOnMouseClicked(event -> {
            createDM();
        });

        createChatIcon.setOnMouseEntered(event -> {
            createChatIcon.setCursor(Cursor.HAND);
        });

        createChatIcon.setOnMouseExited(event -> {
            createChatIcon.setCursor(Cursor.DEFAULT);
        });

        VBox searchVBox = new VBox(3);

        HBox.setMargin(searchVBox, new Insets(27, 0, 0, 5));

        searchVBox.getChildren().addAll(searchArrowIcon, createChatIcon);
        searchHBox.getChildren().addAll(searchIcon, searchTextField, searchVBox);
    }

    public void createGCSearchBox(){
        if(!searchHBox.getChildren().isEmpty()){
            searchHBox.getChildren().clear();
        }

        ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("search.png")));
        searchIcon.setFitWidth(32);
        searchIcon.setFitHeight(32);

        HBox.setMargin(searchIcon, new Insets(27, 0, 0, 5));

        TextField searchTextField = new TextField();
        searchTextField.setPrefHeight(36);
        searchTextField.setPrefWidth(202);
        searchTextField.setPromptText("Search for chats ...");
        searchTextField.setStyle("-fx-background-color: transparent; -fx-border-width: 0px 0px 2px 0px; -fx-border-color: #70bef5;");
        searchTextField.setFont(Font.font("Ebrima", 13));

        HBox.setMargin(searchTextField, new Insets(20, 0, 0, 5));

        ImageView searchArrowIcon = new ImageView(new Image(getClass().getResourceAsStream("searchArrow.png")));
        searchArrowIcon.setFitHeight(32);
        searchArrowIcon.setFitWidth(32);

        searchArrowIcon.setOnMouseClicked(event -> {
            searchGC();
        });

        searchArrowIcon.setOnMouseEntered(event -> {
            searchArrowIcon.setCursor(Cursor.HAND);
        });

        searchArrowIcon.setOnMouseExited(event -> {
            searchArrowIcon.setCursor(Cursor.DEFAULT);
        });

        ImageView createChatIcon = new ImageView(new Image(getClass().getResourceAsStream("createChat.png")));
        createChatIcon.setFitHeight(32);
        createChatIcon.setFitWidth(32);

        createChatIcon.setOnMouseClicked(event -> {
            createGC();
        });

        createChatIcon.setOnMouseEntered(event -> {
            createChatIcon.setCursor(Cursor.HAND);
        });

        createChatIcon.setOnMouseExited(event -> {
            createChatIcon.setCursor(Cursor.DEFAULT);
        });

        VBox searchVBox = new VBox(3);

        HBox.setMargin(searchVBox, new Insets(27, 0, 0, 5));

        searchVBox.getChildren().addAll(searchArrowIcon, createChatIcon);
        searchHBox.getChildren().addAll(searchIcon, searchTextField, searchVBox);
    }

    public void createDM(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/CreateDM.fxml"));

            Parent root = loader.load();

            CreateDCController controller = loader.getController();
            controller.setClient(client2);

            Stage secondaryStage = new Stage();
            Scene scene = new Scene(root);
            secondaryStage.setScene(scene);

            Stage primaryStage = (Stage) directChatBtn.getScene().getWindow();

            secondaryStage.initModality(Modality.APPLICATION_MODAL);

            secondaryStage.initOwner(primaryStage);

            secondaryStage.show();
        } catch (Exception e){
            System.out.println("123");
            e.printStackTrace();
        }
    }

    public void createGC(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/CreateGC.fxml"));

            Parent root = loader.load();

            CreateGCController controller = loader.getController();
            controller.setClient(client2);

            Stage secondaryStage = new Stage();
            Scene scene = new Scene(root);
            secondaryStage.setScene(scene);

            Stage primaryStage = (Stage) directChatBtn.getScene().getWindow();

            secondaryStage.initModality(Modality.APPLICATION_MODAL);

            secondaryStage.initOwner(primaryStage);

            secondaryStage.show();
        } catch (Exception e){
            System.out.println("456");
            e.printStackTrace();
        }
    }

    public void deleteMessage(){

    }

    public void editMessage(){
        fillMessageHBoxWithEdit();
    }

}
