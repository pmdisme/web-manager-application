package cs211.project.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cs211.project.models.LogUser;
import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;

public class AdminPageController {
    @FXML
    private TableView usersLogTableView;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label nameLabel;
    private Datasource<UserList> datasource;
    private UserList usersLogList;
    private boolean changePasswordOn;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label errorLabel;
    @FXML
    private Label usernameTagLabel;
    @FXML
    private Label nameTagLabel;
    @FXML
    private Pane changePasswordPane;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private CheckBox cautionChoiceBox;
    @FXML
    private Button changePasswordButton;

    @FXML
    public void initialize() {
        datasource = new UserLogFileDataSource("data", "logInfo.csv");
        usersLogList = datasource.readData();
        showTable(usersLogList);

        usersLogTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LogUser>() {
            @Override
            public void changed(ObservableValue observable, LogUser oldValue, LogUser newValue) {
                if (newValue != null) {
                    profileImageView.setImage(newValue.getProfilePicture());
                    usernameLabel.setText(newValue.getUsername());
                    nameLabel.setText(newValue.getName());
                } else {
                    profileImageView.setImage(null);
                    usernameLabel.setText("");
                    nameLabel.setText("");
                }
            }
        });
    }

    private void showTable(UserList usersLogList) {
        TableColumn<LogUser, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<LogUser, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<LogUser, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("logDate"));

        TableColumn<LogUser, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("logTime"));

        usersLogTableView.getColumns().clear();
        usersLogTableView.getColumns().add(usernameColumn);
        usersLogTableView.getColumns().add(nameColumn);
        usersLogTableView.getColumns().add(dateColumn);
        usersLogTableView.getColumns().add(timeColumn);

        usersLogTableView.getItems().clear();
        for (User user : usersLogList.getUsers()) {
            usersLogTableView.getItems().add(user);
        }

    }

    @FXML
    public void changePasswordPaneToggle() {
        Timeline timeline = new Timeline();
        if (!changePasswordOn) {
            double targetHeight = 165;

            KeyValue keyValue = new KeyValue(changePasswordPane.prefHeightProperty(), targetHeight);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.15), keyValue);

            timeline.getKeyFrames().add(keyFrame);
            changePasswordPane.setDisable(false);
            changePasswordPane.setVisible(true);
            timeline.play();
            changePasswordOn = true;
        } else {
            double targetHeight = 0;

            KeyValue keyValue = new KeyValue(changePasswordPane.prefHeightProperty(), targetHeight);
            KeyValue keyValue2 = new KeyValue(changePasswordPane.visibleProperty(), false);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.15), keyValue);
            KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.15), keyValue2);

            timeline.getKeyFrames().add(keyFrame);
            timeline.getKeyFrames().add(keyFrame2);
            timeline.play();

            changePasswordPane.setVisible(false);
            changePasswordPane.setDisable(true);
            changePasswordOn = false;
        }
    }

    @FXML
    public void changePassword() {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        if(oldPass.isEmpty() || newPass.isEmpty()) {
            errorLabel.setText("Please fill in the required information.");
            return;
        }
        boolean correctPass = false;
        Datasource<UserList> userListDatasource = new UserListFileDataSource("data", "userData.csv");
        UserList userList = userListDatasource.readData();
        UserList updatePass = new UserList();
        for (User user : userList.getUsers()) {
            if (user.getUsername().equals("admin211")) {
                BCrypt.Result result = BCrypt.verifyer().verify(oldPass.toCharArray(), user.getPassword());
                if(result.verified) {
                    if(newPass.equals(oldPass)) {
                        errorLabel.setText("Your new password can not be the same as your current password.");
                        return;
                    }
                    if(newPass.contains(",")) {
                        errorLabel.setText("Comma \",\" is not allowed in password.");
                        return;
                    }
                    user.setPassword(BCrypt.withDefaults().hashToString(12, newPass.toCharArray()));
                    correctPass = true;
                }
                else {
                    break;
                }
            }
            updatePass.addUser(user);
        }
        if(correctPass) {
            userListDatasource.writeData(updatePass);
            try {
                FXRouter.goTo("login");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            errorLabel.setText("Wrong Password!!");
        }
    }

    @FXML
    public void agreeToCaution() {
        changePasswordButton.setDisable(!cautionChoiceBox.isSelected());
    }

    @FXML
    public void logout() {
        try {
            FXRouter.goTo("login");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
