package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.ActivityList;
import cs211.project.models.collections.TeamChatList;
import cs211.project.models.collections.TeamList;
import cs211.project.models.collections.TeamParticipantList;
import cs211.project.services.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class TeamManagementController {
    @FXML private TextField activityNameTextField;
    @FXML private TextArea activityDescriptionTextArea;
    @FXML private ScrollPane participantListScrollPane;
    @FXML private GridPane participantListGridPane;
    @FXML private TableView activityTableView;
    @FXML private Button addActivityButton;
    @FXML private Button deleteActivityButton;
    @FXML private Button endActivityButton;
    @FXML private TableColumn activityNameColumn;
    @FXML private TableColumn activityDescriptionColumn;
    @FXML private TableColumn activityStatusColumn;

    private ActivityList activityList;
    private Datasource<ActivityList> activityListDatasource;
    private Activity selectedActivity;
    private Datasource<TeamList> teamListDatasource;
    private TeamList teamList;
    private Team team;
    private Datasource<TeamParticipantList> teamParticipantListDatasource;
    private TeamParticipantList teamParticipantList;
    private String[] componentData;
    private String currentUsername;
    private String eventUUID;
    private String teamName;
    @FXML
    public void initialize(){
        addActivityButton.setDisable(true);
        deleteActivityButton.setDisable(true);
        endActivityButton.setDisable(true);
        componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        teamName = componentData[1];
        currentUsername = componentData[2];
        teamListDatasource = new TeamListFileDatasource("data", "team_list.csv");
        teamList = teamListDatasource.readData();
        team = teamList.findTeamByObject(teamList.findEventByEventUUIDAndTeamName(eventUUID, teamName));
        activityListDatasource = new TeamActivityListFileDatasource("data", "team_activity_list.csv");
        activityList = activityListDatasource.readData();
        teamParticipantListDatasource = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
        teamParticipantList = teamParticipantListDatasource.readData();
        showActivity(activityList);
        showParticipant();

        activityNameTextField.setOnKeyReleased(event -> {
            if (activityNameTextField.getText().isEmpty() || activityDescriptionTextArea.getText().isEmpty()){
                addActivityButton.setDisable(true);
            }
            else{
                addActivityButton.setDisable(false);
            }
        });

        activityDescriptionTextArea.setOnKeyReleased(event -> {
            if (activityNameTextField.getText().isEmpty() || activityDescriptionTextArea.getText().isEmpty()){
                addActivityButton.setDisable(true);
            }
            else{
                addActivityButton.setDisable(false);
            }
        });


        activityTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Activity>() {
            @Override
            public void changed(ObservableValue observable, Activity oldValue, Activity newValue) {
                if (newValue != null) {
                    deleteActivityButton.setDisable(false);
                    if (currentUsername.equals(team.getTeamOwnerUsername())){
                        endActivityButton.setDisable(false);
                    }
                    selectedActivity = newValue;
                    activityNameTextField.setText(selectedActivity.getActivityName());
                    activityDescriptionTextArea.setText(selectedActivity.getActivityInformation());
                }
                else{
                    deleteActivityButton.setDisable(true);
                    endActivityButton.setDisable(true);
                    activityNameTextField.setText("");
                    activityDescriptionTextArea.setText("");
                }
            }
        });
    }

    @FXML
    public void handleEndActivityButton(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("End Activity");
        alert.setContentText("Are you sure you want to end this activity?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            if (selectedActivity.getActivityStatus().equals("Ended")){
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Error Dialog");
                alert1.setHeaderText("Activity already ended");
                alert1.setContentText("This activity has already ended.");
                alert1.showAndWait();
            }
            activityList.setActivityStatusByUUID(selectedActivity.getActivityUUID(), "Ended");
            activityListDatasource.writeData(activityList);
            showActivity(activityList);
            selectedActivity = null;
            deleteActivityButton.setDisable(true);
            endActivityButton.setDisable(true);
        }
    }

    @FXML
    public void handleAddActivityButton(){
        String activityName = activityNameTextField.getText();
        String activityDescription = activityDescriptionTextArea.getText();
        UUID uuid = UUID.randomUUID();
        String activityUUID = uuid.toString();
        while (activityList.findActivityByUUID(activityUUID) != null){
            uuid = UUID.randomUUID();
            activityUUID = uuid.toString();
        }
        if (!activityName.isEmpty() && !activityDescription.isEmpty()){
            activityList.addNewActivityTeam(eventUUID, teamName, activityName, activityDescription, "In Progress", activityUUID);
            activityListDatasource.writeData(activityList);
            showActivity(activityList);
            activityNameTextField.clear();
            activityDescriptionTextArea.clear();
        }
        addActivityButton.setDisable(true);
    }

    @FXML
    public void handleRemoveActivityButton(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Remove Activity");
        alert.setContentText("Are you sure you want to remove this activity?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            activityList.removeActivity(selectedActivity);
            activityListDatasource.writeData(activityList);
            showActivity(activityList);
            Datasource<TeamChatList> teamChatListDatasource = new TeamChatListFileDatasource("data", "team_chat_list.csv");
            TeamChatList teamChatList = teamChatListDatasource.readData();
            teamChatList.deleteChatOfActivity(selectedActivity.getActivityUUID());
            teamChatListDatasource.writeData(teamChatList);
            selectedActivity = null;
            deleteActivityButton.setDisable(true);
            endActivityButton.setDisable(true);
        }
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
            if (activity.getTeamOfActivityName().equals(teamName) && activity.getEventOfActivityUUID().equals(eventUUID)) {
                activityTableView.getItems().add(activity);
            }
        }
    }

    public void showParticipant(){
        int row = 0;
        for (TeamParticipant teamParticipant: teamParticipantList.getTeamParticipants()) {
            if (!teamParticipant.getEventUUID().equals(eventUUID) || !teamParticipant.getTeamName().equals(teamName)) {
                continue;
            }
            if (teamParticipant.getUsername().equals(team.getTeamOwnerUsername())){
                continue;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/cs211/project/views/team-participant-element.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TeamParticipantElementController team_ = fxmlLoader.getController();
            team_.setTeamParticipant(teamParticipant.getUsername(), teamParticipant.getEventUUID(), teamParticipant.getTeamName(), currentUsername);
            participantListGridPane.add(anchorPane, 0, row);
            row++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }
    }
}
