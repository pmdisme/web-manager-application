package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.TeamList;
import cs211.project.models.collections.TeamParticipantList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class CreateTeamController {
    @FXML private DatePicker startDateDatePicker;
    @FXML  private DatePicker endDateDatePicker;
    @FXML private TextField teamNameTextField;
    @FXML private TextField numPeopleTextField;
    @FXML private MenuButton startHourMenuButton;
    @FXML private MenuButton startMinuteMenuButton;
    @FXML private MenuButton endHourMenuButton;
    @FXML private MenuButton endMinuteMenuButton;
    private TeamList teamList;
    private Datasource<TeamList> datasource;
    private String eventUUID;
    private String currentUsername;
    private Datasource<EventList> eventListDatasource;
    private EventList eventList;
    private Event event;

    @FXML
    public void initialize() {
        datasource = new TeamListFileDatasource("data", "team_list.csv");
        teamList = datasource.readData();
        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();
        for (int i = 0; i < 24; i++) {
            MenuItem startHourMenuItem = new MenuItem(String.valueOf(i).length() == 2 ? String.valueOf(i) : "0" + String.valueOf(i));
            startHourMenuItem.setOnAction(event -> {
                startHourMenuButton.setText(startHourMenuItem.getText().length() == 2 ? startHourMenuItem.getText() : "0" + startHourMenuItem.getText());
            });
            startHourMenuButton.getItems().add(startHourMenuItem);
            MenuItem endHourMenuItem = new MenuItem(String.valueOf(i).length() == 2 ? String.valueOf(i) : "0" + String.valueOf(i));
            endHourMenuItem.setOnAction(event -> {
                endHourMenuButton.setText(endHourMenuItem.getText().length() == 2 ? endHourMenuItem.getText() : "0" + endHourMenuItem.getText());
            });
            endHourMenuButton.getItems().add(endHourMenuItem);
        }
        for (int i = 0; i < 60; i++) {
            MenuItem startMinuteMenuItem = new MenuItem(String.valueOf(i).length() == 2 ? String.valueOf(i) : "0" + String.valueOf(i));
            startMinuteMenuItem.setOnAction(event -> {
                startMinuteMenuButton.setText(startMinuteMenuItem.getText().length() == 2 ? startMinuteMenuItem.getText() : "0" + startMinuteMenuItem.getText());
            });
            startMinuteMenuButton.getItems().add(startMinuteMenuItem);
            MenuItem endMinuteMenuItem = new MenuItem(String.valueOf(i).length() == 2 ? String.valueOf(i) : "0" + String.valueOf(i));
            endMinuteMenuItem.setOnAction(event -> {
                endMinuteMenuButton.setText(endMinuteMenuItem.getText().length() == 2 ? endMinuteMenuItem.getText() : "0" + endMinuteMenuItem.getText());
            });
            endMinuteMenuButton.getItems().add(endMinuteMenuItem);
        }
        startHourMenuButton.setText(LocalTime.now().getHour() < 10 ? "0" + LocalTime.now().getHour() : String.valueOf(LocalTime.now().getHour()));
        startMinuteMenuButton.setText(LocalTime.now().getMinute() < 10 ? "0" + LocalTime.now().getMinute() : String.valueOf(LocalTime.now().getMinute()));
    }

    public void createTeam() {
        if (teamNameTextField.getText().isEmpty() || numPeopleTextField.getText().isEmpty() || startDateDatePicker.getValue() == null || endDateDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please fill all the information");
            alert.showAndWait();
            return;
        }
        try {
            int startHour = Integer.parseInt(startHourMenuButton.getText());
            int startMinute = Integer.parseInt(startMinuteMenuButton.getText());
            int endHour = Integer.parseInt(endHourMenuButton.getText());
            int endMinute = Integer.parseInt(endMinuteMenuButton.getText());

            LocalTime startJoinTime = LocalTime.of(startHour, startMinute);
            LocalTime endJoinTime = LocalTime.of(endHour, endMinute);

            LocalDateTime startJoinDateTime = LocalDateTime.of(startDateDatePicker.getValue(), startJoinTime);
            LocalDateTime endJoinDateTime = LocalDateTime.of(endDateDatePicker.getValue(), endJoinTime);

            if (startJoinDateTime.isAfter(endJoinDateTime)) {
                showAlert("Error", "Start Join Date Time must be before End Join Date Time");
                return;
            }

            LocalDateTime eventStartDateTime = LocalDateTime.of(event.getStartDate(), event.getStartTime());
            LocalDateTime eventEndDateTime = LocalDateTime.of(event.getEndDate(), event.getEndTime());

            if (endJoinDateTime.isAfter(eventEndDateTime)) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmation");
                confirmationAlert.setHeaderText("The end join date time is after the event end date time.");
                confirmationAlert.setContentText("Do you want to set the end join date time to the event end date time?");
                Optional<ButtonType> result = confirmationAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    endJoinDateTime = eventEndDateTime;
                } else {
                    return;
                }
            }
            boolean isExist = teamList.addNewTeam(eventUUID, teamNameTextField.getText(),
                    Integer.parseInt(numPeopleTextField.getText()), startJoinDateTime.toLocalDate(),
                    startJoinDateTime.toLocalTime(), endJoinDateTime.toLocalDate(), endJoinDateTime.toLocalTime(), currentUsername, currentUsername);

            if (!isExist) {
                showAlert("Error", "This Team is already exist");
                return;
            }

            datasource.writeData(teamList);
            Datasource<TeamParticipantList> datasourceParticipant = new TeamParticipantListFileDataSource("data", "team_participant_list.csv");
            TeamParticipantList teamParticipantList = datasourceParticipant.readData();
            teamParticipantList.addNewTeamParticipant(currentUsername, eventUUID, teamNameTextField.getText());
            datasourceParticipant.writeData(teamParticipantList);
            FXRouterPane.goTo("event-team-management");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a number for number of people");
            alert.showAndWait();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void setEventUUID(String eventUUID) {
        this.eventUUID = eventUUID;
        event = eventList.findEventByUUID(eventUUID);
    }


    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }
}
