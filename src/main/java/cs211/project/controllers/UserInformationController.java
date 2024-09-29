package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class UserInformationController {
    @FXML private Label usernameLabel;
    @FXML private Label nameLabel;
    @FXML private TableView<Event> activeEventTableView;
    @FXML private TableView<Event> eventHistoryTableView;
    @FXML private ImageView profileImageView;
    @FXML private Button changeProfileButton;
    @FXML private Button cancelButton;
    private User currentUser;
    private Datasource<EventList> eventListDatasource;
    private EventList eventList;
    private Event event;
    private Datasource<UserList> userListDataSource;
    private UserList userList;
    private Datasource<List<String[]>> joinEventDataSource;
    private List<String[]> joinEventData;
    private File selectedImage = null;

    public void initialize() {
        currentUser = (User) FXRouter.getData();

        userListDataSource = new UserListFileDataSource("data", "userData.csv");
        userList = userListDataSource.readData();

        eventListDatasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = eventListDatasource.readData();

        joinEventDataSource = new JoinEventFileDataSource("data", "joinEventData.csv");
        joinEventData = joinEventDataSource.readData();

        checkFileIsExisted("userData.csv");
        showUser();
        showActiveTable(eventList);
        showHistoryTable(eventList);
        activeEventTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue observable, Event oldValue, Event newValue) {
                if (newValue != null) {
                    try {
                        FXRouterPane.goTo("participant-activity", new String[] {newValue.getEventUUID(), currentUser.getUsername(), "userInformation"});
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        eventHistoryTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue observable, Event oldValue, Event newValue) {
                if (newValue != null) {
                    try {
                        FXRouterPane.goTo("participant-activity", new String[] {newValue.getEventUUID(), currentUser.getUsername(), "userInformation"});
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void checkFileIsExisted(String fileName) {
        File file = new File("data");
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = "data" + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showUser() {
        nameLabel.setText(currentUser.getName());
        usernameLabel.setText("@" + currentUser.getUsername());
        Circle clip = new Circle(70, 70, 70);
        profileImageView.setClip(clip);
        profileImageView.setImage(currentUser.getProfilePicture(140, 140, false, false));
        profileImageView.setFitHeight(140);
        profileImageView.setFitWidth(140);
    }

    private void showActiveTable(EventList eventList) {
        TableColumn<Event, String> eventNameColumn = new TableColumn<>("Name");
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> eventCategoryColumn = new TableColumn<>("Category");
        eventCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Event, String> eventStartDateColumn = new TableColumn<>("Start Date");
        eventStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Event, String> eventEndDateColumn = new TableColumn<>("End Date");
        eventEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        eventNameColumn.prefWidthProperty().bind(activeEventTableView.widthProperty().multiply(0.36));
        eventCategoryColumn.prefWidthProperty().bind(activeEventTableView.widthProperty().multiply(0.30));
        eventStartDateColumn.prefWidthProperty().bind(activeEventTableView.widthProperty().multiply(0.17));
        eventEndDateColumn.prefWidthProperty().bind(activeEventTableView.widthProperty().multiply(0.17));

        activeEventTableView.getColumns().clear();
        activeEventTableView.getColumns().addAll(eventNameColumn, eventCategoryColumn, eventStartDateColumn, eventEndDateColumn);
        activeEventTableView.getItems().clear();

        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        LocalDate currentDate = LocalDate.now(thaiTimeZone);

        for (String[] data : joinEventData) {
            String username = data[0];
            String eventUUID = data[1];
            if (username.equals(currentUser.getUsername())) {
                event = eventList.findEventByUUID(eventUUID);
                if (event.getEndDate().isAfter(currentDate)) {
                    activeEventTableView.getItems().add(event);
                } else {
                    activeEventTableView.getItems();
                }
            }
        }
    }

    private void showHistoryTable(EventList eventList) {
        TableColumn<Event, String> eventNameColumn = new TableColumn<>("Name");
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Event, String> eventCategoryColumn = new TableColumn<>("Category");
        eventCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Event, String> eventStartDateColumn = new TableColumn<>("Start Date");
        eventStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<Event, String> eventEndDateColumn = new TableColumn<>("End Date");
        eventEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        eventNameColumn.prefWidthProperty().bind(eventHistoryTableView.widthProperty().multiply(0.36));
        eventCategoryColumn.prefWidthProperty().bind(eventHistoryTableView.widthProperty().multiply(0.30));
        eventStartDateColumn.prefWidthProperty().bind(eventHistoryTableView.widthProperty().multiply(0.17));
        eventEndDateColumn.prefWidthProperty().bind(eventHistoryTableView.widthProperty().multiply(0.17));

        eventHistoryTableView.getColumns().clear();
        eventHistoryTableView.getColumns().addAll(eventNameColumn, eventCategoryColumn, eventStartDateColumn, eventEndDateColumn);
        eventHistoryTableView.getItems().clear();

        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        LocalDate currentDate = LocalDate.now(thaiTimeZone);

        for (String[] data : joinEventData) {
            String username = data[0];
            String eventUUID = data[1];
            if (username.equals(currentUser.getUsername())) {
                event = eventList.findEventByUUID(eventUUID);
                if (event.getEndDate().isBefore(currentDate)) {
                    eventHistoryTableView.getItems().add(event);
                } else {
                    eventHistoryTableView.getItems();
                }
            }
        }
    }

    @FXML
    public void changeProfile(){
        chooseFile();

        if(selectedImage != null) {
            Image profileImage = new Image(selectedImage.toURI().toString());
            profileImageView.setImage(profileImage);

            String selectedImagePath = selectedImage.getAbsolutePath();
            String targetDirectoryPath = "data/profile_picture";
            Path targetDirectory = Path.of(targetDirectoryPath);

            String fileType = null;
            try {
                fileType = Files.probeContentType(Paths.get(selectedImage.getAbsolutePath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String currentProfilePicName = currentUser.getProfilePictureName();
            Path existingProfilePicPath = targetDirectory.resolve(currentProfilePicName);

            if (Files.exists(existingProfilePicPath)) {
                try {
                    Files.delete(existingProfilePicPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            String newProfilePicFileName = currentUser.getUsername() + "." + (fileType.substring(6));
            currentUser.setProfilePic(newProfilePicFileName);

            Path targetFilePath = targetDirectory.resolve(newProfilePicFileName);
            try {
                Files.copy(Path.of(selectedImagePath), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            userList.setProfileImageByUsername(currentUser.getUsername(), newProfilePicFileName);
            userListDataSource.writeData(userList);

            try {
                FXRouter.goTo("mainPage", currentUser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All image files","*.jpg","*.png", "*.jpeg", "*.webp",  "*.jfif" , "*.pjpeg" , "*.pjp"));

        Stage stage = (Stage) changeProfileButton.getScene().getWindow();
        selectedImage = fileChooser.showOpenDialog(stage);
    }

    @FXML
    private void cancelChangeProfilePic(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Profile Picture");
        alert.setHeaderText("Are you sure to delete your profile picture?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }
        selectedImage = null;
        profileImageView.setImage(new Image(getClass().getResource("/cs211/project/images/default.png").toExternalForm()));

        String targetDirectoryPath = "data/profile_picture";
        Path targetDirectory = Path.of(targetDirectoryPath);
        String currentProfilePicName = currentUser.getProfilePictureName();
        Path existingProfilePicPath = targetDirectory.resolve(currentProfilePicName);

        if (Files.exists(existingProfilePicPath)) {
            try {
                Files.delete(existingProfilePicPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String profilePic = "default.png";
        currentUser.setProfilePic(profilePic);
        userList.setProfileImageByUsername(currentUser.getUsername(), profilePic);
        userListDataSource.writeData(userList);

        try {
            FXRouter.goTo("mainPage", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void resetPassword() {
        try {
            FXRouter.goTo("reset-password", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}