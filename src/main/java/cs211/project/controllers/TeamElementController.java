package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.TeamParticipantList;
import cs211.project.services.Datasource;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.TeamParticipantListFileDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TeamElementController {
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label teamNameLabel;
    @FXML
    private Label maxParticipantsLabel;
    @FXML
    private Label remaindaysLabel;
    @FXML
    private AnchorPane selectTeamAnchorPane;
    @FXML
    private ImageView eventImageView;
    @FXML
    private Label participantsLabel;
    @FXML private Label eventRemaindaysLabel;
    private Datasource<EventList> eventListDatasource;
    private EventList eventList;
    private Event event;
    private Datasource<TeamParticipantList> teamParticipantListDatasource;
    private TeamParticipantList teamParticipantList;

    public void initialize() {
        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();
        teamParticipantListDatasource = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
        teamParticipantList = teamParticipantListDatasource.readData();
    }

    public void setPage(String eventUUID, String teamName, int maxParticipants, LocalDate startJoinDate, LocalTime startJoinTime, LocalDate closingJoinDate, LocalTime endJoinTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartJoinDate = startJoinDate.format(formatter);
        String formattedClosingJoinDate = closingJoinDate.format(formatter);

        event = eventList.findEventByUUID(eventUUID);
        eventNameLabel.setText(event.getName());
        teamNameLabel.setText(teamName);
        maxParticipantsLabel.setText(String.valueOf(maxParticipants));
        remaindaysLabel.setText("Team : " + formattedStartJoinDate + " " + startJoinTime + " - " + formattedClosingJoinDate + " " + endJoinTime);
        eventImageView.setImage(new Image(new File("data" + File.separator + "eventPicture" + File.separator + event.getPicture()).toURI().toString(), 130, 130, false, false));
        participantsLabel.setText(String.valueOf(teamParticipantList.getTeamParticipantCountByEventUUIDAndTeamName(eventUUID, teamName)));
        eventRemaindaysLabel.setText("Event : " + event.getStartDate().format(formatter) + " " + event.getStartTime() + " - " + event.getEndDate().format(formatter) + " " + event.getEndTime());
    }



}
