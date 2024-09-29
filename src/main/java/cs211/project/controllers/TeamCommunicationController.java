package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.*;
import cs211.project.services.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeamCommunicationController {
    @FXML private Label activityNameLabel;
    @FXML private Label teamNameLabel;
    @FXML private TableView activityTableView;
    @FXML private TextField sendMessageTextField;
    @FXML private Button manageTeamButton;
    @FXML private VBox chatBoxVBox;
    @FXML private ScrollPane chatBoxScrollPane;
    @FXML private Button sendMessageButton;
    @FXML private TableColumn activityNameColumn;
    @FXML private TableColumn activityDescriptionColumn;
    @FXML private TableColumn activityStatusColumn;
    @FXML private Text activityDescriptionText;

    private TeamChatList teamChatList;
    private Team team;
    private Datasource<TeamList> teamListDatasource;
    private TeamList teamList;
    private Datasource<TeamChatList> teamChatListDatasource;
    private Datasource<ActivityList> activityListDatasource;
    private ActivityList activityList;
    private String beforeSend = "";
    private String lastTimeUpdate = "";
    private String [] componentData;
    private String eventUUID;
    private String teamName;
    private String currentUsername;
    private Datasource<UserList> userListDatasource;
    private UserList userList;
    private Datasource<EventList> eventListDatasource;
    private EventList eventList;
    private Activity selectedActivity;

    @FXML
    public void initialize(){
        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();
        userListDatasource = new UserListFileDataSource("data", "userData.csv");
        userList = userListDatasource.readData();
        teamListDatasource = new TeamListFileDatasource("data", "team_list.csv");
        teamList = teamListDatasource.readData();
        teamChatListDatasource = new TeamChatListFileDatasource("data", "team_chat_list.csv");
        teamChatList = teamChatListDatasource.readData();
        activityListDatasource = new TeamActivityListFileDatasource("data", "team_activity_list.csv");
        activityList = activityListDatasource.readData();
        componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        teamName = componentData[1];
        currentUsername = componentData[2];
        team = teamList.findEventByEventUUIDAndTeamName(eventUUID, teamName);
        manageTeamButton.setVisible(false);
        if (team.getHeadOfTeamUsername().equals(currentUsername) || team.getTeamOwnerUsername().equals(currentUsername)){
            manageTeamButton.setVisible(true);
        }
        teamNameLabel.setText(team.getTeamName());
        activityNameLabel.setText("");
        activityDescriptionText.setText("");
        sendMessageButton.setDisable(true);
        sendMessageTextField.setDisable(true);
        showActivity(activityList);
        showChat();

        activityTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Activity>() {
            @Override
            public void changed(ObservableValue observable, Activity oldValue, Activity newValue) {
                selectedActivity = newValue;
                sendMessageTextField.clear();
                showChat();
                if (newValue != null) {
                    sendMessageButton.setDisable(false);
                    sendMessageTextField.setDisable(false);
                    if (newValue.getActivityStatus().equals("Ended")){
                        sendMessageButton.setDisable(true);
                        sendMessageTextField.setDisable(true);
                    }
                    activityNameLabel.setText(newValue.getActivityName());
                    activityDescriptionText.setText(newValue.getActivityInformation());
                }
                else{
                    sendMessageButton.setDisable(true);
                    sendMessageTextField.setDisable(true);
                    activityNameLabel.setText("");
                    activityDescriptionText.setText("");
                }
            }
        });
    }

    @FXML
    public void handleSendMessageButton() {
        String text = sendMessageTextField.getText().trim();
        sendMessageTextField.clear();
        if (!text.isEmpty()){
            LocalDateTime nowDateTime = LocalDateTime.now();
            teamChatList.addNewChat(team.getEventUUID(), team.getTeamName(), currentUsername, text, nowDateTime, selectedActivity.getActivityUUID());
            teamChatListDatasource.writeData(teamChatList);
            update(currentUsername, text, nowDateTime);
        }
        Platform.runLater(() -> {
            chatBoxScrollPane.applyCss();
            chatBoxScrollPane.layout();
            chatBoxScrollPane.setVvalue(1.0);
        });
    }

    @FXML
    public void handleManageTeamButton(){
        try {
            FXRouterPane.goTo("team-management", new String[] {team.getEventUUID(), team.getTeamName(), currentUsername});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void showChat(){
        chatBoxVBox.getChildren().clear();
        beforeSend = "";
        lastTimeUpdate = "";
        if (selectedActivity == null){
            return;
        }
        for (TeamChat teamChat : teamChatList.getTeamChats()) {
            if (!teamChat.getEventUUID().equals(team.getEventUUID()) || !teamChat.getTeamName().equals(team.getTeamName()) || !teamChat.getActivityUUID().equals(selectedActivity.getActivityUUID())){
                continue;
            }
            update(teamChat.getUsername(), teamChat.getMessage(), teamChat.getTime());
        }
        Platform.runLater(() -> {
            chatBoxScrollPane.applyCss();
            chatBoxScrollPane.layout();
            chatBoxScrollPane.setVvalue(1.0);
        });
    }
    public void update(String username, String message, LocalDateTime time){
        User user = userList.findUserByUsername(username);
        if (user == null){
            return;
        }
        chatBoxVBox.setSpacing(-5);
        Text text = new Text(message);
        text.getStyleClass().add("message");
        TextFlow flow = new TextFlow();
        Circle img = new Circle(32,32,16);

        VBox messageVBox = new VBox(-5);
        messageVBox.setAlignment(Pos.TOP_LEFT);
        Text nameText = new Text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedTime =  time.format(formatter);
        if (!currentUsername.equals(username)){
            String nameTextString;
            if (team.getTeamOwnerUsername().equals(username)){
                nameTextString = user.getName() + " (Owner)" + " " + formattedTime + "\n";
            }
            else if (team.getHeadOfTeamUsername().equals(username)){
                nameTextString = user.getName() + " (Leader)" + " " + formattedTime + "\n";
            }
            else{
                nameTextString = user.getName() + " " + formattedTime + "\n";
            }
            nameText = new Text(nameTextString);
            nameText.getStyleClass().add("usernameText");
            messageVBox.getChildren().add(nameText);
            String path;
            if (user.getProfilePictureName().equals("default.png")){
                path = getClass().getResource("/cs211/project/images/default.png").toExternalForm();
            }
            else{
                path = new File("data/profile_picture/" + user.getProfilePictureName()).toURI().toString();
            }
            img.setFill(new ImagePattern(new Image(path, 32, 32, false, false)));

        }

        flow.getChildren().add(text);
        flow.setMaxWidth(280);
        messageVBox.getChildren().add(flow);

        HBox hBoxMessage = new HBox(10);

        Pane pane = new Pane();
        if(!currentUsername.equals(username)){
            if (beforeSend.equals(username) && lastTimeUpdate.equals(formattedTime)){
                messageVBox.getChildren().remove(0);
                flow.getStyleClass().add("textFlowFlipped");
                hBoxMessage.setAlignment(Pos.TOP_LEFT);
                pane.setMinWidth(42);
                hBoxMessage.getChildren().add(pane);
                hBoxMessage.getChildren().add(messageVBox);
            }
            else{
                Pane pane2 = new Pane();
                pane2.setMinWidth(10);
                pane.setMaxWidth(nameText.getLayoutBounds().getWidth());
                beforeSend = username;
                messageVBox.getChildren().remove(1);
                HBox tempHBox = new HBox();
                tempHBox.getChildren().add(pane2);
                tempHBox.getChildren().add(flow);
                tempHBox.getChildren().add(pane);
                messageVBox.getChildren().add(tempHBox);
                flow.getStyleClass().add("textFlowFlipped");
                hBoxMessage.setAlignment(Pos.TOP_LEFT);
                hBoxMessage.getChildren().add(img);
                hBoxMessage.getChildren().add(messageVBox);
            }
            lastTimeUpdate = formattedTime;

        }else{
            beforeSend = "";
            lastTimeUpdate = "";
            flow.getStyleClass().add("textFlow");
            hBoxMessage.setAlignment(Pos.TOP_RIGHT);
            HBox timeHBox = new HBox();
            timeHBox.setAlignment(Pos.BOTTOM_RIGHT);
            Text timeText = new Text(formattedTime.split(" ")[1] + " ");
            timeText.getStyleClass().add("timeText");
            timeHBox.getChildren().add(timeText);
            hBoxMessage.getChildren().add(timeHBox);
            hBoxMessage.getChildren().add(flow);
            hBoxMessage.getChildren().add(pane);
        }

        hBoxMessage.getStyleClass().add("hBoxMessage");

        chatBoxVBox.getChildren().addAll(hBoxMessage);
        Platform.runLater(() -> {
            chatBoxScrollPane.applyCss();
            chatBoxScrollPane.layout();
            chatBoxScrollPane.setVvalue(1.0);
        });
    }




    public void showActivity(ActivityList activityList){
        activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("activityName"));

        activityDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("activityInformation"));

        activityStatusColumn.setCellValueFactory(new PropertyValueFactory<>("activityStatus"));

        activityTableView.getColumns().clear();
        activityTableView.getColumns().add(activityStatusColumn);
        activityTableView.getColumns().add(activityNameColumn);
        activityTableView.getColumns().add(activityDescriptionColumn);
        activityTableView.getItems().clear();



        for (Activity activity : activityList.getActivities()) {
            if (activity.getTeamOfActivityName().equals(team.getTeamName()) && activity.getEventOfActivityUUID().equals(team.getEventUUID())) {
                activityTableView.getItems().add(activity);
            }
        }
    }

}
