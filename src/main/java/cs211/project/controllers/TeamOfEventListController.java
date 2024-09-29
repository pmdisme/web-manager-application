package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.TeamList;
import cs211.project.models.collections.TeamParticipantList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

public class TeamOfEventListController {

    @FXML
    private ScrollPane teamScrollPane;
    @FXML
    private GridPane teamGridPane;
    @FXML
    private Label eventNameLabel;

    private Datasource<TeamList> teamListDatasource;
    private Datasource<TeamParticipantList> teamParticipantListDatasource;
    private TeamList teamList;
    private TeamParticipantList teamParticipantList;
    private String eventUUID;
    private String [] componentData;
    private String currentUsername;
    private LocalDate currentDate;
    private Event event;
    private Datasource<EventList> eventListDatasource;
    private EventList eventList;

    @FXML
    public void initialize(){
        teamListDatasource = new TeamListFileDatasource("data", "team_list.csv");
        teamList = teamListDatasource.readData();
        teamParticipantListDatasource = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
        teamParticipantList = teamParticipantListDatasource.readData();
        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();
        componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        currentUsername = componentData[1];
        event = eventList.findEventByUUID(eventUUID);
        eventNameLabel.setText(event.getName());
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        currentDate = LocalDate.now(thaiTimeZone);
        showTeam();
    }


    public void showTeam(){
        int row = 0;
        int column = 0;
        for (Team team : teamList.getTeams()) {
            if (!team.getEventUUID().equals(eventUUID)) {
                continue;
            }
            if (team.getStartJoinDate().isEqual(currentDate)) {
                if (team.getStartTime().isAfter(java.time.LocalTime.now(ZoneId.of("Asia/Bangkok")))) {
                    continue;
                }
            }
            else if (team.getStartJoinDate().isAfter(currentDate)) {
                continue;
            }
            else if (team.getClosingJoinDate().isEqual(currentDate)) {
                if (team.getEndTime().isBefore(java.time.LocalTime.now(ZoneId.of("Asia/Bangkok")))) {
                    continue;
                }
            }
            else if (team.getClosingJoinDate().isBefore(currentDate)) {
                continue;
            }
            if (column == 2) {
                row++;
                column = 0;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/cs211/project/views/team-element.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TeamElementController team_ = fxmlLoader.getController();
            team_.setPage(team.getEventUUID(), team.getTeamName(), team.getMaxParticipants(), team.getStartJoinDate(), team.getStartTime(), team.getClosingJoinDate(), team.getEndTime());
            if (teamParticipantList.getTeamParticipantCountByEventUUIDAndTeamName(team.getEventUUID(), team.getTeamName()) >= team.getMaxParticipants()) {
                anchorPane.getStyleClass().remove("element");
                anchorPane.getStyleClass().add("team-full");
            }
            else{
                anchorPane.setOnMouseClicked(event1 -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Do you want to join this team?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if(result.get() == ButtonType.OK) {
                        if (currentUsername.equals(team.getTeamOwnerUsername())){
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setHeaderText("You are the owner of this team.");
                            alert1.showAndWait();
                            return;
                        }
                        Datasource<TeamParticipantList> datasourceParticipant = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
                        TeamParticipantList teamParticipantList = datasourceParticipant.readData();
                        if (!teamParticipantList.addNewTeamParticipant(currentUsername, team.getEventUUID(), team.getTeamName())){
                            Alert alert1 = new Alert(Alert.AlertType.ERROR);
                            alert1.setHeaderText("You have already joined this team.");
                            alert1.showAndWait();
                            return;
                        }
                        datasourceParticipant.writeData(teamParticipantList);
                        try {
                            FXRouterPane.goTo("team-communication", new String[] {team.getEventUUID(), team.getTeamName(), currentUsername});
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            teamGridPane.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }

    }
}
