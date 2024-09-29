package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.collections.EventList;
import cs211.project.models.User;
import cs211.project.services.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class CreateEventController {
    private User currentUser;
    @FXML private TextField eventNameTextField;
    @FXML private TextArea eventInfoTextArea;
    @FXML private TextField placeTextField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button startTimePicker;
    @FXML private Button endTimePicker;
    @FXML private Label errorLabel;
    @FXML private Label eventImageErrorLabel;
    @FXML private ImageView eventImageView;
    @FXML private Button uploadImageButton;
    @FXML private ChoiceBox<String> eventChoiceBox;
    private LocalTime selectedStartTime;
    private LocalTime selectedEndTime;
    File selectedImage = null;

    private Datasource<EventList> datasource;
    private Event event;
    private EventList eventList;
    private String[] eventCategories = {"งานแสดงสินค้า", "เทศกาล", "อบรมสัมนา", "บ้านและของแต่งบ้าน"
            ,"อาหารและเครื่องดื่ม", "บันเทิง","คอนเสิร์ต/แฟนมีตติ้ง", "ท่องเที่ยว", "ศิลปะ/นิทรรศการ/ถ่ายภาพ", "กีฬา"
            , "ศาสนา", "สัตว์เลี้ยง", "ธุรกิจ/อาชีพ/การศึกษา", "อื่น ๆ"};

    @FXML
    public void initialize() {
        currentUser = (User) FXRouter.getData();
        datasource = new EventListFileDatasource("data", "eventList.csv");
        eventList = datasource.readData();

        eventChoiceBox.getItems().addAll(eventCategories);
        eventInfoTextArea.setWrapText(true);

        eventImageErrorLabel.setText("");
        errorLabel.setText("");
    }

    @FXML
    private void createEvent() throws IOException {
        String name = eventNameTextField.getText().trim();
        String image = "";
        String info = eventInfoTextArea.getText().replace("\n", " ").replace(",", "//comma//").trim();
        String category = eventChoiceBox.getSelectionModel().getSelectedItem();;
        String place = placeTextField.getText().trim();

        LocalDate localStartDate = startDatePicker.getValue();
        LocalDate localEndDate = endDatePicker.getValue();
        String startDate = localStartDate.toString();
        String endDate = localEndDate.toString();

        String eventOwner = currentUser.getUsername();

        String filePath = "data/eventList.csv";
        File file = new File(filePath);
        FileInputStream fileInputStream = null;

        if (name.equals("") || selectedImage==null || info.equals("") || category.equals("")
                || place.equals("") || localStartDate==null || localEndDate==null ||
                selectedStartTime==null || selectedEndTime==null) {
            if (localEndDate.isBefore(localStartDate))
                errorLabel.setText("End date should be greater than start date.");
            else if (selectedEndTime==null || selectedStartTime==null)
                errorLabel.setText("You need to fill start time\nand end time");
            else if (selectedEndTime.isBefore(selectedStartTime))
                errorLabel.setText("End time should be greater than start time.");
            else
                errorLabel.setText("Please fill in the required information.");
            errorLabel.setAlignment(Pos.CENTER_RIGHT);

            if (selectedImage==null)
                eventImageErrorLabel.setText("*Please upload the event image.");
            return;
        }

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
        ArrayList<String> allEvent = new ArrayList<String>();
        String line = "";

        try {
            while ((line = buffer.readLine()) != null) {
                if (line.equals(""))
                    continue;

                String[] data = line.split(",");
                String eventNameInFile = data[0].trim();

                if (eventNameInFile.equals(name)) {
                    errorLabel.setText("This event is already exist.");
                    eventNameTextField.setText("");
                    return;
                }
                allEvent.add(line);
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
        UUID eventUUID = UUID.randomUUID();
        while (eventList.findEventByUUID(eventUUID.toString()) != null) {
            eventUUID = UUID.randomUUID();
        }
        image = name + "." + (Files.probeContentType(Paths.get(selectedImage.getAbsolutePath())).substring(6));
        String newEvent = name + "," + image + "," + info + "," + category + "," + place
                + "," + startDate + "," + endDate + "," + selectedStartTime + "," + selectedEndTime + "," +
                eventOwner+ ",-1,,,," + "," + eventUUID.toString();

        try {
            for(String thisEvent : allEvent) {
                bufferWrite.append(thisEvent);
                bufferWrite.append('\n');
            }
            bufferWrite.append(newEvent);
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

        if(selectedImage != null) {
            String selectedImagePath = selectedImage.getAbsolutePath();
            String targetDirectoryPath = "data/eventPicture";
            Path targetDirectory = Path.of(targetDirectoryPath);
            String fileType = Files.probeContentType(Paths.get(selectedImage.getAbsolutePath()));
            Path targetFilePath = targetDirectory.resolve(name + "." + (fileType.substring(6)));
            try {
                Files.copy(Path.of(selectedImagePath), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        goToMyEvents();
    }

    @FXML
    private void goToMyEvents() {
        try {
            FXRouterPane.goTo("my-events");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void uploadImage() {
        chooseFile();
        if (selectedImage != null) {
            Image image = new Image(selectedImage.getPath());
            eventImageView.setImage(image);
        } else {
            eventImageErrorLabel.setText("*Please upload the event image.");
        }
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File("C:\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All image files","*.jpg","*.png", "*.jpeg", "*.webp",  "*.jfif" , "*.pjpeg" , "*.pjp"));

        Stage stage = (Stage) uploadImageButton.getScene().getWindow();
        selectedImage = fileChooser.showOpenDialog(stage);
    }

    @FXML
    public void handleStartTimePickerButton() {
        selectedStartTime = showCustomTimePickerDialog();
        if (selectedStartTime != null) {
            System.out.println("Select Start Time: " + selectedStartTime);
            startTimePicker.setText(selectedStartTime.toString());
        }
    }

    @FXML
    public void handleEndTimePickerButton() {
        selectedEndTime = showCustomTimePickerDialog();
        if (selectedEndTime != null) {
            System.out.println("Select End Time: " + selectedEndTime);
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

}
