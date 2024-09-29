package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.User;
import cs211.project.services.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;

public class MyEventsController {
    @FXML private TableView<Event> myEventsTableView;
    private EventList eventList;
    private Datasource<EventList> datasource;
    private User currentUser;
    private JoinEventFileDataSource joinEventDataSource;


    @FXML
    public void initialize() {
        currentUser = (User) FXRouter.getData();
        datasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = datasource.readData();
        showTable(eventList);
        joinEventDataSource = new JoinEventFileDataSource("data", "joinEventData.csv");


        myEventsTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue observable, Event oldValue, Event newValue) {
                if (newValue != null) {
                    try {
                        FXRouterPane.goTo("event-management", new String[] {newValue.getEventUUID(), currentUser.getUsername()});
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void showTable(EventList eventList) {
        TableColumn<Event, String> eventNameColumn = new TableColumn<>("Name");
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> eventCategoryColumn = new TableColumn<>("Category");
        eventCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Event, String> eventStartDateColumn = new TableColumn<>("Start Date");
        eventStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Event, String> eventEndDateColumn = new TableColumn<>("End Date");
        eventEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        TableColumn<Event, String> eventStartTimeColumn = new TableColumn<>("Start Time");
        eventStartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<Event, String> eventEndTimeColumn = new TableColumn<>("End Time");
        eventEndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        TableColumn<Event, String> participantsColumn = new TableColumn<>("Max Participants");
        participantsColumn.setCellValueFactory(cellData -> {
            int maxParticipants = cellData.getValue().getMaxParticipants();
            if (maxParticipants == -1) {
                return new SimpleStringProperty("No Maximum Participants");
            } else {
                return new SimpleStringProperty(String.valueOf(maxParticipants));
            }
        });

        TableColumn<Event, String> numberOfParticipantsColumn = new TableColumn<>("Participants");
        numberOfParticipantsColumn.setCellValueFactory(cellData -> {
            int currentParticipants = joinEventDataSource.countParticipantsForEvent(cellData.getValue().getEventUUID());
            return new SimpleStringProperty(String.valueOf(currentParticipants));
        });



        myEventsTableView.getColumns().clear();
        myEventsTableView.getColumns().add(eventNameColumn);
        myEventsTableView.getColumns().add(eventCategoryColumn);
        myEventsTableView.getColumns().add(eventStartDateColumn);
        myEventsTableView.getColumns().add(eventEndDateColumn);
        myEventsTableView.getColumns().add(eventStartTimeColumn);
        myEventsTableView.getColumns().add(eventEndTimeColumn);
        myEventsTableView.getColumns().add(participantsColumn);
        myEventsTableView.getColumns().add(numberOfParticipantsColumn);

        eventNameColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.25));
        eventCategoryColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.15));
        eventStartDateColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.09));
        eventEndDateColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.09));
        eventStartTimeColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.09));
        eventEndTimeColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.09));
        participantsColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.15));
        numberOfParticipantsColumn.prefWidthProperty().bind(myEventsTableView.widthProperty().multiply(0.09));

        myEventsTableView.getItems().clear();

        for (Event event: eventList.getEvents()) {
            if (event.getOwnerUsername().equals(currentUser.getUsername()))
                myEventsTableView.getItems().add(event);
        }

    }
}
