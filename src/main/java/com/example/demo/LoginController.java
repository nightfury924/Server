package com.example.demo;

// import javafx.animation.PauseTransition;
// import javafx.fxml.Initializable;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.StackPane;
// import javafx.scene.paint.Color;
// import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXML;
// import javafx.util.Duration;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// import java.net.URL;
// import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private String username;
    private String password;
    private String email;
    private String DOB;

    private String confirmPassword;

    public static Scanner sc = new Scanner(System.in);

    public Client client;
    public Socket socKet;

    @FXML
    private AnchorPane anchor1;

    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Label loginUsernameError;
    @FXML
    private Label loginFailError;

    @FXML
    private TextField signupUsernameField;
    @FXML
    private TextField signupEmailField;
    @FXML
    private PasswordField signupPasswordField;
    @FXML
    private PasswordField signupDOBField;
    @FXML
    private Label signupUsernameError;
    @FXML
    private Label signupEmailError;
    @FXML
    private Label signupPasswordError;
    @FXML
    private Label signupDOBError;
    @FXML
    private Label signupFailError;

    @FXML
    private TextField forgetPasswordEmailField;
    @FXML
    private PasswordField forgetPasswordPasswordField;
    @FXML
    private PasswordField forgetPasswordConfirmPasswordField;
    @FXML
    private Label forgetPasswordEmailError;
    @FXML
    private Label forgetPasswordPasswordError;
    @FXML
    private Label forgetPasswordConfirmPasswordError;
    @FXML
    private Label forgetPasswordFailError;

    public void setSocket(Socket socket){
        socKet = socket;
    }

    public void setClient(Client c){
        client = c;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        try{
//            socKet=new Socket(Client.SERVER_IP, Client.SERVER_PORT);;
//            client = new Client(socKet);
////            int ch = sc.nextInt();
////            sc.nextLine();
////            boolean authenticated;
////            if(ch==1){
////                authenticated = client.login();
////            }
////            else if(ch == 2){
////                authenticated = client.signUp();
////            }
////            else{
////                authenticated = false;
////            }
//
////            if(!authenticated){
////                System.exit(SERVER_PORT);
////            }
//            // client.printMessages();
////            client.startListening();
////            client.interact();
////            sc.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void switchToLogin(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Login.fxml"));
        loader.setController(this);

        root = loader.load();
        //root = FXMLLoader.load(getClass().getResource("/com/example/demo/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSignup(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/Signup.fxml"));
        loader.setController(this);

        root = loader.load();
        //root = FXMLLoader.load(getClass().getResource("/com/example/demo/Signup.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToForgetPassword(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ForgetPassword.fxml"));
        loader.setController(this);

        root = loader.load();
        //root = FXMLLoader.load(getClass().getResource("/com/example/demo/ForgetPassword.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void login(ActionEvent event) throws IOException{
        username = loginUsernameField.getText();
        password = loginPasswordField.getText();

        loginUsernameError.setVisible(username.length() < 8);

        if(!loginUsernameError.isVisible()){
            client.out.write("login\n");
            //client.out.flush();

            client.out.write(username);
            client.out.newLine();
            client.out.flush();
            client.out.write(password);
            client.out.newLine();
            client.out.flush();
            String response = client.in.readLine();
            if(response.equals("true")){
                client.receiveAccount();
                client.startListening();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/MainPage.fxml"));
                root = loader.load();

                MainController mainController = loader.getController();
                mainController.setClient(client);

                //root = FXMLLoader.load(getClass().getResource("/com/example/demo/MainPage.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else{
                loginFailError.setVisible(true);
            }

//            if(username.equals("admin123") && password.equals("admin123")){
//                root = FXMLLoader.load(getClass().getResource("/com/example/demo/MainPage.fxml"));
//                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//                scene = new Scene(root);
//                stage.setScene(scene);
//                stage.show();
//            }
//            else{
//
//            }
        }
        else{
            loginFailError.setVisible(false);
        }
    }

    public void signup(ActionEvent event) throws IOException{
        int x = 0 ;

        username = signupUsernameField.getText();
        email = signupEmailField.getText();
        password = signupPasswordField.getText();
        DOB = signupDOBField.getText();

        signupUsernameError.setVisible(username.length() < 8);
        signupEmailError.setVisible(!email.contains("@") || !email.contains("."));
        signupPasswordError.setVisible(password.length() < 8);

        String regex = "\\d{2}/\\d{2}/\\d{4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(DOB);
        signupDOBError.setVisible(!matcher.matches());

        if(!signupUsernameError.isVisible() && !signupEmailError.isVisible() && !signupPasswordError.isVisible() && !signupDOBError.isVisible()){
            client.out.write("signUp\n");
            client.out.flush();

            client.out.write(email);
            client.out.newLine();
            client.out.flush();
            if(!client.in.readLine().equals("ok")){
                signupFailError.setVisible(true);
                x = 1;
                System.out.println("a");
            }

            client.out.write(username);
            client.out.newLine();
            client.out.flush();
            if(!client.in.readLine().equals("ok")){
                signupFailError.setVisible(true);
                x = 1;
                System.out.printf("b");
            }

            System.out.println("here x is " + x);
            if(x == 0){
                Account  newAccount = new Account(password,username,DOB,email);
                System.out.println("z");
                client.out.write(Client.gson.toJson(newAccount));
                client.out.newLine();
                client.out.flush();
                System.out.println("c");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/SignupSuccessful.fxml"));
                loader.setController(this);

                root = loader.load();
                //root = FXMLLoader.load(getClass().getResource("/com/example/demo/SignupSuccessful.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                System.out.println("d");
            }
        }
        else{
            signupFailError.setVisible(false);
            System.out.println("e");
        }
    }

    public void resetPassword(ActionEvent event) throws IOException{
        email = forgetPasswordEmailField.getText();
        password = forgetPasswordPasswordField.getText();
        confirmPassword = forgetPasswordConfirmPasswordField.getText();

        forgetPasswordEmailError.setVisible(!email.contains("@") || !email.contains("."));
        forgetPasswordPasswordError.setVisible(password.length() < 8);
        forgetPasswordConfirmPasswordError.setVisible(!password.equals(confirmPassword));

        if(!forgetPasswordEmailError.isVisible() && !forgetPasswordPasswordError.isVisible() && !forgetPasswordConfirmPasswordError.isVisible()){
            if(email.equals("admin123@gmail.com")){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/ForgetPasswordSuccessful.fxml"));
                loader.setController(this);

                root = loader.load();
                //root = FXMLLoader.load(getClass().getResource("/com/example/demo/ForgetPasswordSuccessful.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else{
                forgetPasswordFailError.setVisible(true);
            }
        }
        else{
            forgetPasswordFailError.setVisible(false);
        }
    }
}
