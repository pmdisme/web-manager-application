package cs211.project.controllers;

import cs211.project.models.*;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.TeamList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

public class EventTeamManagementContrller {
    @FXML private GridPane teamListGridPane;
    @FXML private ScrollPane teamListScrollPane;
    @FXML private Label eventNameLabel;

    private String eventUUID;
    private Datasource<TeamList> datasourceTeam;
    private LocalDate currentDate;
    private TeamList teamList;
    private String[] componentData;
    private String currentUsername;
    private CreateTeamController createTeamController;
    private Datasource<EventList> datasourceEvent;
    private EventList eventList;
    private Event event;

    @FXML
    public void initialize(){
        datasourceTeam = new TeamListFileDatasource("data", "team_list.csv");
        teamList = datasourceTeam.readData();
        datasourceEvent = new EventListFileDatasource("data", "eventlist.csv");
        eventList = datasourceEvent.readData();
        componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        currentUsername = componentData[1];
        event = eventList.findEventByUUID(eventUUID);
        eventNameLabel.setText(event.getName());
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        currentDate = LocalDate.now(thaiTimeZone);
        showTeamList();
    }

    @FXML
    public void handleCreateTeamButton(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cs211/project/views/create-team.fxml"));
        try {
            Parent root = loader.load();
            Dialog<Boolean> createTeamDialog = new Dialog<>();
            createTeamDialog.setTitle("Create Team");

            createTeamDialog.getDialogPane().setContent(root);

            createTeamController = loader.getController();
            createTeamController.setEventUUID(eventUUID);
            createTeamController.setCurrentUsername(currentUsername);
            ButtonType createTeamButtonType = new ButtonType("Create Team", ButtonBar.ButtonData.OK_DONE);
            createTeamDialog.getDialogPane().getButtonTypes().addAll(createTeamButtonType);

            createTeamDialog.setResultConverter(dialogButton -> {
                if (dialogButton == createTeamButtonType) {
                    return true;
                }
                return false;
            });

            Optional<Boolean> result = createTeamDialog.showAndWait();

            if (result.isPresent() && result.get()) {
                createTeamController.createTeam();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showTeamList(){
        int row = 0;
        int column = 0;
        for (Team team : teamList.getTeams()) {
            if (!team.getEventUUID().equals(eventUUID)) {
                continue;
            }
            if (team.getClosingJoinDate().isEqual(currentDate)) {
                if (team.getEndTime().isBefore(LocalTime.now(ZoneId.of("Asia/Bangkok")))) {
                    continue;
                }
            }
            else if (team.getClosingJoinDate().isBefore(currentDate)) {
                continue;
            }
            if(column == 2) {
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
            anchorPane.setOnMouseClicked(event1 -> {
                try {
                    FXRouterPane.goTo("team-management", new String[] {team.getEventUUID(), team.getTeamName(), currentUsername});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            teamListGridPane.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }

    }


}
