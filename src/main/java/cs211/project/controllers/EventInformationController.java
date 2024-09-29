package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EventInformationController {
    @FXML private Label eventNameLabel;
    @FXML private ImageView eventImageView;
    @FXML private Label eventInfoLabel;
    @FXML private Label placeLabel;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label maxParticipantsLabel;
    @FXML private Label startJoinDateLabel;
    @FXML private Label closingJoinDateLabel;
    @FXML private Label categoryLabel;
    @FXML private Label errorLabel;

    private Datasource<EventList> datasource;
    private EventList eventList;
    private Event event;
    private String eventUUID;
    private String [] componentData;
    private String currentUsername;

    @FXML
    public void initialize() {
        datasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = datasource.readData();
        componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        currentUsername = componentData[1];
        event = eventList.findEventByUUID(eventUUID);
        eventNameLabel.setText(event.getName());

        eventInfoLabel.setWrapText(true);
        eventInfoLabel.setText(event.getInfo());
        eventInfoLabel.setPrefWidth(940);

        placeLabel.setWrapText(true);
        placeLabel.setText(event.getPlace());
        placeLabel.setPrefWidth(440);

        String pattern = "dd MMMM yyyy";
        startDateLabel.setText(event.getStartDate().format(DateTimeFormatter.ofPattern(pattern)) + " @ " + event.getStartTime() + " to");
        endDateLabel.setText(event.getEndDate().format(DateTimeFormatter.ofPattern(pattern)) + " @ " + event.getEndTime());

        if (event.getMaxParticipants() == -1) {
            maxParticipantsLabel.setText("No Maximum Participants");
        } else {
            maxParticipantsLabel.setText("" + event.getMaxParticipants());
        }

        if (event.getStartJoinDate() == null) {
            startJoinDateLabel.setText("N/A");
        } else if (event.getStartJoinTime() == null) {
            startJoinDateLabel.setText(event.getStartJoinDate().format(DateTimeFormatter.ofPattern(pattern)) + " to");
        } else {
            startJoinDateLabel.setText(event.getStartJoinDate().format(DateTimeFormatter.ofPattern(pattern)) + " @ " + event.getStartJoinTime() + " to");
        }

        if (event.getCloseJoinDate() == null) {
            closingJoinDateLabel.setText("N/A");
        } else if (event.getCloseJoinTime() == null) {
            closingJoinDateLabel.setText(event.getCloseJoinDate().format(DateTimeFormatter.ofPattern(pattern)));
        } else {
            closingJoinDateLabel.setText(event.getCloseJoinDate().format(DateTimeFormatter.ofPattern(pattern)) + " @ " + event.getCloseJoinTime());
        }

        categoryLabel.setText(event.getCategory());

        String filePath = "data/eventPicture/" + event.getPicture();
        File file = new File(filePath);
        eventImageView.setImage(new Image(file.toURI().toString()));
        eventImageView.setFitWidth(398);
        eventImageView.setFitHeight(230);

        errorLabel.setText("");
    }

    @FXML
    public void handleBackToEventPage() {
        try {
            FXRouterPane.goTo("event-list");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void joinEventButton() {
        if (currentUsername.equals(event.getOwnerUsername())){
            errorLabel.setText("You can not join your own event.");
            return;
        }
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        LocalDate currentDate = LocalDate.now(thaiTimeZone);
         if (event.getEndDate().isBefore(currentDate)) {
            errorLabel.setText("The event has ended.\nYou can not join.");
            return;
        }

        LocalDateTime currentDateTime = LocalDateTime.now(thaiTimeZone);
        LocalDateTime startJoinDateTime = (event.getStartJoinDate() != null && event.getStartJoinTime() != null)
                ? LocalDateTime.of(event.getStartJoinDate(), event.getStartJoinTime())
                : LocalDateTime.MIN;
        LocalDateTime closeJoinDateTime = (event.getCloseJoinDate() != null && event.getCloseJoinTime() != null)
                ? LocalDateTime.of(event.getCloseJoinDate(), event.getCloseJoinTime())
                : LocalDateTime.MAX;

        if (currentDateTime.isBefore(startJoinDateTime) || currentDateTime.isAfter(closeJoinDateTime)) {
            errorLabel.setText("You can not join this event\noutside the specified joining period.");
            return;
        }

        JoinEventFileDataSource joinDataSource = new JoinEventFileDataSource("data", "joinEventData.csv");
        int currentParticipants = joinDataSource.countParticipantsForEvent(event.getEventUUID());
        if (event.getMaxParticipants() != -1 && currentParticipants >= event.getMaxParticipants()) {
            errorLabel.setText("The event has reached its maximum\nnumber of participants. You can not join.");
            return;
        }

        String filePath = "data/joinEventData.csv";
        File file = new File(filePath);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );
        BufferedReader buffer = new BufferedReader(inputStreamReader);
        ArrayList<String> allJoinEventData = new ArrayList<String>();
        String line = "";

        try {
            while ((line = buffer.readLine()) != null) {
                if (line.equals(""))
                    continue;

                String[] data = line.split(",");
                String userName = data[0].trim();
                String eventUUID = data[1].trim();

                if (event.getEventUUID().equals(eventUUID)) {
                    if (currentUsername.equals(userName)) {
                        goToParticipantActivity();
                        return;
                    }
                }
                allJoinEventData.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter bufferWrite = new BufferedWriter(outputStreamWriter);

        String newJoinEvent = currentUsername + "," + event.getEventUUID();
        try {
            for(String joinEventData : allJoinEventData) {
                bufferWrite.append(joinEventData);
                bufferWrite.append('\n');
            }
            bufferWrite.append(newJoinEvent);
            bufferWrite.append('\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bufferWrite.flush();
                bufferWrite.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        goToParticipantActivity();
    }

    @FXML
    private void goToParticipantActivity() {
        try {
            FXRouterPane.goTo("participant-activity", new String[] { event.getEventUUID(), currentUsername, "eventInformation"});
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleVisitTeamButton(){
        try {
            FXRouterPane.goTo("teamofevent-list", new String[] { event.getEventUUID(), currentUsername });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

