package cs211.project.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.Datasource;
import cs211.project.services.FXRouter;
import cs211.project.services.UserListFileDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class ResetPasswordController {
    @FXML private PasswordField oldPasswordTextField;
    @FXML private PasswordField newPasswordTextField;
    @FXML private PasswordField confirmPasswordTextField;
    @FXML private Label errorLabel;
    private User currentUser;
    private Datasource<UserList> userListDataSource;
    private UserList userList;


    public void initialize() {
        currentUser = (User) FXRouter.getData();

        userListDataSource = new UserListFileDataSource("data", "userData.csv");
        userList = userListDataSource.readData();

        errorLabel.setText("");
    }

    @FXML
    public void saveChange() {
        String oldPassword = oldPasswordTextField.getText();
        String newPassword = newPasswordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();
        errorLabel.setText("");

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in the required information.");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmPasswordTextField.setText("");
            return;
        }

        if (oldPassword.contains(",") || newPassword.contains(",")) {
            errorLabel.setText("Comma \",\" is not allowed.");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmPasswordTextField.setText("");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("New password and Confirm password does not match.");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmPasswordTextField.setText("");
            return;
        }

        BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), currentUser.getPassword());
        if (!result.verified) {
            errorLabel.setText("Your current password is not correct.");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmPasswordTextField.setText("");
            return;
        }

        if (result.verified && newPassword.equals(oldPassword)) {
            errorLabel.setText("Your new password can not be the same as your current password.");
            oldPasswordTextField.setText("");
            newPasswordTextField.setText("");
            confirmPasswordTextField.setText("");
            return;
        }

        if (result.verified && newPassword.equals(confirmPassword)) {
            String encryptPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

            for (User user : userList.getUsers()) {
                if (user.getUsername().equals(currentUser.getUsername())) {
                    user.setPassword(encryptPassword);
                    break;
                }
            }
            userListDataSource.writeData(userList);
        }

        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void cancelChange() {
        try {
            FXRouter.goTo("mainPage", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
