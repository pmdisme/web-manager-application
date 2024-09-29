package cs211.project.controllers;

import cs211.project.models.Activity;
import cs211.project.models.User;
import cs211.project.models.collections.ActivityList;
import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EventParticipantManagementController {

    @FXML private TableView eventParticipantTableView;
    @FXML private TableView activityParticipantTableView;
    @FXML private Button removeActivityButton;
    @FXML private Button startTimePicker;
    @FXML private Button endTimePicker;
    @FXML private Label eventNameLabel;
    @FXML private Label eventName2Label;
    @FXML private TextField activityNameTextField;
    @FXML private TextArea activityInfoTextArea;
    @FXML private DatePicker activityDatePicker;
    private LocalTime selectedStartTime;
    private LocalTime selectedEndTime;
    private ActivityList activityList;
    private Datasource<ActivityList> activityListDatasource;
    private Activity selectedActivity;
    private EventList eventList;
    private Event event;
    private Datasource<EventList> eventListDatasource;
    private String[] componentData;
    private String currentUsername;
    private String eventOfParticipantUUID;
    private Datasource<List<String[]>> participantDataSource;
    private List<String[]> participantList;
    private Datasource<UserList> userListDatasource;
    private UserList userList;



    public void initialize(){
        removeActivityButton.setDisable(true);
        componentData = (String[]) FXRouterPane.getData();
        eventOfParticipantUUID = componentData[0];
        currentUsername = componentData[1];
        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();
        event = eventList.findEventByUUID(eventOfParticipantUUID);
        activityListDatasource = new ParticipantActivityListFileDatasource("data", "participant_activity_list.csv");
        activityList = activityListDatasource.readData();
        participantDataSource = new JoinEventFileDataSource("data", "joinEventData.csv");
        participantList = participantDataSource.readData();
        userListDatasource = new UserListFileDataSource("data", "userData.csv");
        userList = userListDatasource.readData();
        eventNameLabel.setText(event.getName());
        eventName2Label.setText(event.getName());
        showParticipants(participantList);
        showActivity(activityList);
        activityParticipantTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Activity>() {
            @Override
            public void changed(ObservableValue observable, Activity oldValue, Activity newValue) {
                if (newValue != null) {
                    removeActivityButton.setDisable(false);
                    selectedActivity = newValue;
                }
                else {
                    removeActivityButton.setDisable(true);
                }
            }
        });
}


    @FXML
    public void handleBackToEventManagementButton() {
        try {
            FXRouterPane.goTo("event-management");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleStartTimePickerButton() {
        selectedStartTime = showCustomTimePickerDialog();
        if (selectedStartTime != null) {
           startTimePicker.setText(selectedStartTime.toString());
        }
    }

    @FXML
    public void handleEndTimePickerButton() {
        selectedEndTime = showCustomTimePickerDialog();
        if (selectedEndTime != null) {
            endTimePicker.setText(selectedEndTime.toString());
        }
    }

    private LocalTime showCustomTimePickerDialog() {
        Dialog<LocalTime> dialog = new Dialog<>();
        dialog.setTitle("Select Time");

        ComboBox<Integer> hoursBox = new ComboBox<>();
        ComboBox<Integer> minutesBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hoursBox.getItems().add(i);
        }
        for (int i = 0; i < 60; i+=5) {
            minutesBox.getItems().add(i);
        }

        hoursBox.getSelectionModel().select(LocalTime.now().getHour());
        minutesBox.getSelectionModel().select(LocalTime.now().getMinute());

        HBox timePickerLayout = new HBox(5);
        timePickerLayout.getChildren().addAll(hoursBox, new Label(":"), minutesBox);
        dialog.getDialogPane().setContent(timePickerLayout);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return LocalTime.of(hoursBox.getValue(), minutesBox.getValue());
            }
            return null;
        });

        Optional<LocalTime> result = dialog.showAndWait();
        return result.orElse(null);
    }


    public void handleAddActivityPartiButton() {
        String activityName = activityNameTextField.getText();
        String activityInfo = activityInfoTextArea.getText();
        LocalTime startTime = selectedStartTime;
        LocalTime endTime = selectedEndTime;
        LocalDate activityDate = activityDatePicker.getValue();

        if (!activityName.isEmpty() || !activityInfo.isEmpty() || !(startTime == null) || !(endTime == null) || !(activityDate == null)) {
            activityList.addNewActivityParticipant(eventOfParticipantUUID, activityName, activityInfo, startTime, endTime, activityDate);
            activityListDatasource.writeData(activityList);
            activityNameTextField.clear();
            activityInfoTextArea.clear();
            selectedStartTime = null;
            selectedEndTime = null;
            activityDatePicker.setValue(null);
            startTimePicker.setText("Start Time");
            endTimePicker.setText("End Time");
            showActivity(activityList);
        }
    }


    public void handleRemoveActivityPartiButton(){
        if (selectedActivity == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Activity");
        alert.setHeaderText("Are you sure you want to remove this activity?");
        alert.setContentText("You won't be able to go back and change it.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            activityList.removeActivity(selectedActivity);
            activityListDatasource.writeData(activityList);
            showActivity(activityList);
            selectedActivity = null;
            removeActivityButton.setDisable(true);
        }
    }

    private void removeParticipant(String[] participantData) {
        eventParticipantTableView.getItems().remove(participantData);
        participantList.remove(participantData);
        participantDataSource.writeData(participantList);
    }

    public void showParticipants(List<String[]> participantList){
        TableColumn<String[], String> partiNameColumn = new TableColumn<>("Participant Name");
        partiNameColumn.setCellValueFactory(cellData -> {
            String username = cellData.getValue()[0];
            User user = userList.findUserByUsername(username);
            return new SimpleStringProperty(user != null ? user.getName() : "Unknown User");
        });
        ObservableList<String[]> filteredParticipants = FXCollections.observableArrayList();
        for (String[] participantData : participantList) {
            if (participantData[1].equals(eventOfParticipantUUID)) {
                filteredParticipants.add(participantData);
            }
        }

        TableColumn<String[], String> removeButtonColumn = new TableColumn<>("Remove");
        removeButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    removeButton.setOnAction(event -> {
                        String[] participantData = getTableView().getItems().get(getIndex());

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Remove Participant");
                        alert.setHeaderText("Are you sure you want to remove this participant?");
                        alert.setContentText("You won't be able to go back and change it.");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            removeParticipant(participantData);
                        }
                    });
                    setGraphic(removeButton);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<String[], ImageView> profilePictureColumn = new TableColumn<>("");
        profilePictureColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        profilePictureColumn.setCellValueFactory(cellData -> {
            String username = cellData.getValue()[0];
            User user = userList.findUserByUsername(username);
            if (user != null && user.getProfilePictureName() != null) {
                String profilePicturePath;
                if (user.getProfilePictureName().equals("default.png")) {
                    profilePicturePath = getClass().getResource("/cs211/project/images/default.png").toExternalForm();
                } else {
                    profilePicturePath = new File("data" + File.separator + "profile_picture" + File.separator + user.getProfilePictureName()).toURI().toString();
                }
                ImageView imageView = new ImageView(new Image(profilePicturePath));
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                return new SimpleObjectProperty<>(imageView);
            }
            return new SimpleObjectProperty<>(null);
        });


        eventParticipantTableView.setItems(filteredParticipants);
        eventParticipantTableView.getColumns().clear();
        eventParticipantTableView.getColumns().add(profilePictureColumn);
        eventParticipantTableView.getColumns().add(partiNameColumn);
        eventParticipantTableView.getColumns().add(removeButtonColumn);

        profilePictureColumn.prefWidthProperty().bind(eventParticipantTableView.widthProperty().multiply(0.2));
        partiNameColumn.prefWidthProperty().bind(eventParticipantTableView.widthProperty().multiply(0.5));
        removeButtonColumn.prefWidthProperty().bind(eventParticipantTableView.widthProperty().multiply(0.3));
    }
    public void showActivity(ActivityList activityList){

        TableColumn<Activity, String> activityNameColumn = new TableColumn<>("Activity");
        activityNameColumn.setCellValueFactory(new PropertyValueFactory<>("activityName"));

        TableColumn<Activity, String> activityInfoColumn = new TableColumn<>("Details");
        activityInfoColumn.setCellValueFactory(new PropertyValueFactory<>("activityInformation"));

        TableColumn<Activity, LocalTime> activityStartColumn = new TableColumn<>("Start Time");
        activityStartColumn.setCellValueFactory(new PropertyValueFactory<>("activityStartTime"));

        TableColumn<Activity, LocalTime> activityEndColumn = new TableColumn<>("End Time");
        activityEndColumn.setCellValueFactory(new PropertyValueFactory<>("activityEndTime"));

        TableColumn<Activity, String> activityDateColumn =  new TableColumn<>("Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM  yyyy");
        activityDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getActivityDate().format(formatter))
        );
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
            if (activity.getEventOfActivityUUID().equals(event.getEventUUID())) {
                activityParticipantTableView.getItems().add(activity);
            }
        }
    }
}
