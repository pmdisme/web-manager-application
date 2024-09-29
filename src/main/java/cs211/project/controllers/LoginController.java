package cs211.project.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;

public class LoginController {

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

    public void initialize() {
        loginButton.setDisable(true);
        usernameTextField.setOnKeyReleased(event -> {
            if (!usernameTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
                loginButton.setDisable(false);
            } else {
                loginButton.setDisable(true);
            }
        });
        passwordTextField.setOnKeyReleased(event -> {
            if (!usernameTextField.getText().isEmpty() && !passwordTextField.getText().isEmpty()) {
                loginButton.setDisable(false);
            } else {
                loginButton.setDisable(true);
            }
        });
    }


    @FXML
    public void login() throws IOException {
        String username = usernameTextField.getText();
        String inputPassword = passwordTextField.getText();
        String destination = "mainPage";

        Datasource<UserList> userListDatasource = new UserListFileDataSource("data", "userData.csv");
        UserList userList = userListDatasource.readData();
        Datasource<UserList> userListUserLogFileDataSource = new UserLogFileDataSource("data", "logInfo.csv");
        UserList userLogList = new UserList();
        if (username.equals("admin211")) {
            destination = "adminPage";
        }
        for(User user : userList.getUsers()) {
            if(user.getUsername().equals(username)) {
                BCrypt.Result result = BCrypt.verifyer().verify(inputPassword.toCharArray(), user.getPassword());
                if(result.verified) {
                    userLogList.addUser(user);
                    userListUserLogFileDataSource.writeData(userLogList);
                    FXRouter.goTo(destination, user);
                }
            }
        }
        errorLabel.setText("Username or password is incorrect!");
        usernameTextField.setText("");
        passwordTextField.setText("");
    }

    @FXML
    private void goToRegister() {
        try {
            FXRouter.goTo("register");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goToCreators() {
        try {
            FXRouter.goTo("creators");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goToInstructions() {
        try {
            FXRouter.goTo("instructions");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}