package cs211.project.controllers;

import cs211.project.models.Team;
import cs211.project.models.TeamParticipant;
import cs211.project.models.User;
import cs211.project.models.collections.TeamList;
import cs211.project.models.collections.TeamParticipantList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class MyTeamListController {

    @FXML
    private ScrollPane teamListScrollPane;
    @FXML
    private GridPane teamListGridPane;
    @FXML
    private ScrollPane yourCreateTeamListScrollPane;
    @FXML
    private GridPane yourCreateTeamListGridPane;
    private User currentUser;
    private Datasource<TeamList> datasourceTeam;
    private Datasource<TeamParticipantList> datasourceParticipant;
    private LocalDate currentDate;
    private TeamList teamList;
    private TeamParticipantList teamParticipantList;

    public void initialize(){
        currentUser = (User) FXRouter.getData();
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        currentDate = LocalDate.now(thaiTimeZone);
        datasourceTeam = new TeamListFileDatasource("data", "team_list.csv");
        teamList = datasourceTeam.readData();
        datasourceParticipant = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
        teamParticipantList = datasourceParticipant.readData();
        showTeamList();
    }

    public void showTeamList(){
        int row = 0;
        int column = 0;
        for (TeamParticipant teamParticipant : teamParticipantList.getTeamParticipants()) {
            Team team = teamList.findEventByEventUUIDAndTeamName(teamParticipant.getEventUUID(), teamParticipant.getTeamName());
            if (!teamParticipant.getUsername().equals(currentUser.getUsername())) {
                continue;
            }
            if (team.getTeamOwnerUsername().equals(currentUser.getUsername())) {
                continue;
            }
            if (team.getStartJoinDate().isEqual(currentDate)) {
                if (team.getStartTime().isAfter(LocalTime.now(ZoneId.of("Asia/Bangkok")))) {
                    continue;
                }
            }
            else if (team.getStartJoinDate().isAfter(currentDate)) {
                continue;
            }
            else if (team.getClosingJoinDate().isEqual(currentDate)) {
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
                    FXRouterPane.goTo("team-communication", new String[] {team.getEventUUID(), team.getTeamName(), currentUser.getUsername()});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            teamListGridPane.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }

        row = 0;
        column = 0;
        for (Team team : teamList.getTeams()) {
            if (!team.getTeamOwnerUsername().equals(currentUser.getUsername())) {
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
                    FXRouterPane.goTo("team-communication", new String[] {team.getEventUUID(), team.getTeamName(), currentUser.getUsername()});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            yourCreateTeamListGridPane.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }

    }

}
