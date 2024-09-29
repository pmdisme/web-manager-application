package cs211.project.controllers;

import cs211.project.models.Activity;
import cs211.project.models.collections.ActivityList;
import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.FXRouterPane;
import cs211.project.services.ParticipantActivityListFileDatasource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ParticipantActivityController {
    @FXML
    private Label eventNameLabel;

    @FXML
    private TableView<Activity> activityParticipantTableView;
    @FXML private Label eventDateLabel;
    @FXML private Label eventTimeLabel;
    @FXML private Label eventInfoLabel;
    @FXML private Label placeLabel;

    private Event event;
    private EventList eventList;
    private String eventUUID;
    private String currentUsername;
    private String sourcePage;
    private ParticipantActivityListFileDatasource datasource;
    private EventListFileDatasource eventListFileDatasource;


    @FXML
    public void initialize() {
        String[] componentData = (String[]) FXRouterPane.getData();
        eventUUID = componentData[0];
        currentUsername = componentData[1];
        sourcePage = componentData[2];
        eventListFileDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListFileDatasource.readData();
        datasource = new ParticipantActivityListFileDatasource("data", "participant_activity_list.csv");
        ActivityList activityList = datasource.readData();
        event = eventList.findEventByUUID(eventUUID);
        eventNameLabel.setText(event.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("th", "TH"));
        String strStartDate = event.getStartDate().format(formatter);
        String strEndDate = event.getEndDate().format(formatter);
        eventDateLabel.setText(strStartDate + " - " + strEndDate);
        eventTimeLabel.setText(event.getStartTime() + " - " + event.getEndTime());
        placeLabel.setWrapText(true);
        placeLabel.setText(event.getPlace());
        placeLabel.setPrefWidth(400);
        eventInfoLabel.setWrapText(true);
        eventInfoLabel.setText(event.getInfo());
        eventInfoLabel.setPrefWidth(400);
        showActivities(activityList);
    }


    public void showActivities(ActivityList activityList) {

        TableColumn<Activity, String> activityDateColumn = new TableColumn<>("Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        activityDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActivityDate().format(formatter))
        );
        TableColumn<Activity, LocalTime> activityStartColumn = new TableColumn<>("Start Time");
        activityStartColumn.setCellValueFactory(new PropertyValueFactory<>("activityStartTime"));

        TableColumn<Activity, LocalTime> activityEndColumn = new TableColumn<>("End Time");
        activityEndColumn.setCellValueFactory(new PropertyValueFactory<>("activityEndTime"));

        TableColumn<Activity, String> activityNameColumn = new TableColumn<>("Activity");
        activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("activityName"));

        TableColumn<Activity, String> activityInfoColumn = new TableColumn<>("Details");
        activityInfoColumn.setCellValueFactory(new PropertyValueFactory<>("activityInformation"));

        //Sort Date and Time
        activityList.getActivities().sort((a1, a2) -> {
            int dateComparison = a1.getActivityDate().compareTo(a2.getActivityDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return a1.getActivityStartTime().compareTo(a2.getActivityStartTime());
        });

        ObservableList<Activity> observableList = FXCollections.observableArrayList(activityList.getActivities());
        activityParticipantTableView.setItems(observableList);

        activityParticipantTableView.getColumns().clear();
        activityParticipantTableView.getColumns().add(activityDateColumn);
        activityParticipantTableView.getColumns().add(activityStartColumn);
        activityParticipantTableView.getColumns().add(activityEndColumn);
        activityParticipantTableView.getColumns().add(activityNameColumn);
        activityParticipantTableView.getColumns().add(activityInfoColumn);


        activityParticipantTableView.getItems().clear();

        activityDateColumn.prefWidthProperty().bind(activityParticipantTableView.widthProperty().multiply(0.17));
        activityStartColumn.prefWidthProperty().bind(activityParticipantTableView.widthProperty().multiply(0.1));
        activityEndColumn.prefWidthProperty().bind(activityParticipantTableView.widthProperty().multiply(0.1));
        activityNameColumn.prefWidthProperty().bind(activityParticipantTableView.widthProperty().multiply(0.23));
        activityInfoColumn.prefWidthProperty().bind(activityParticipantTableView.widthProperty().multiply(0.40));
        for (Activity activity : activityList.getActivities()) {
            if (activity.getEventOfActivityUUID().equals(eventUUID)) {
                activityParticipantTableView.getItems().add(activity);
            }
        }
    }

    public void backToEventInformation() {
        try {
            if ("userInformation".equals(sourcePage)) {
                FXRouterPane.goTo("user-information");
            } else if ("eventInformation".equals(sourcePage)) {
                FXRouterPane.goTo("event-information");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}