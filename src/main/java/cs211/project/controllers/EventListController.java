package cs211.project.controllers;

import cs211.project.models.Event;
import cs211.project.models.User;
import cs211.project.models.collections.EventList;
import cs211.project.services.Datasource;
import cs211.project.services.EventListFileDatasource;
import cs211.project.services.FXRouterPane;
import cs211.project.services.JoinEventFileDataSource;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

public class EventListController {
    private String[] eventList;
    private int maxRow = 3;
    private Datasource<EventList> datasource;
    private JoinEventFileDataSource joinEventDataSource;
    private EventList eventListData;
    private LocalDate currentDate;
    private boolean isSearch;
    private String selectedCategory;
    private User currentUser;
    @FXML
    private Pane categoryPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private GridPane eventGrid;
    @FXML
    private ScrollPane eventScrollPane;
    @FXML private Button allCategoryButton;
    @FXML private Button categoryExpoButton;
    @FXML private Button categoryFestivalButton;
    @FXML private Button categorySeminarButton;
    @FXML private Button categoryHouseButton;
    @FXML private Button categoryFoodButton;
    @FXML private Button categoryEntertainmentButton;
    @FXML private Button categoryConcertButton;
    @FXML private Button categoryTravelButton;
    @FXML private Button categoryArtButton;
    @FXML private Button categorySportButton;
    @FXML private Button categoryReligionButton;
    @FXML private Button categoryPetButton;
    @FXML private Button categoryEducationButton;
    @FXML private Button categoryOtherButton;
    @FXML
    private Button categoryOpenButton;

    private boolean categoryOn = false;

    public void initialize() {
        currentUser = (User) FXRouterPane.getData();
        allCategoryButton.getStyleClass().add("category-button-selected");
        isSearch = false;
        ZoneId thaiTimeZone = ZoneId.of("Asia/Bangkok");
        currentDate = LocalDate.now(thaiTimeZone);
        datasource = new EventListFileDatasource("data", "eventList.csv");
        joinEventDataSource = new JoinEventFileDataSource("data", "joinEventData.csv");
        eventListData = datasource.readData();
        searchTextField.setOnKeyReleased(this::handleAutoComplete);
        eventList = eventListData.getEvents().stream()
                .map(Event::getName)
                .toArray(String[]::new);
        Arrays.sort(eventList);
        showList();
        eventScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            // Calculate the maximum Vvalue (maximum scroll position)
            double maxVvalue = eventScrollPane.getVmax();

            // Calculate the current Vvalue (current scroll position)
            double currentVvalue = newValue.doubleValue();

            if (selectedCategory == null && currentVvalue >= maxVvalue * 0.95 && !isSearch) {
                int totalEvents = eventListData.getEvents().size();
                int visibleRows = (int) Math.ceil((double) maxRow / 3);
                int remainingEvents = totalEvents - visibleRows * 3;

                if (remainingEvents > 0) {
                    maxRow += 3;
                    showList();
                } else {
                    maxRow = (int) Math.ceil((double) totalEvents / 3) * 3;
                }
            }
        });
    }

    @FXML
    public void handleSearchButton(){
        eventScrollPane.setVvalue(0);
        if (!searchTextField.getText().isEmpty()) {
            eventGrid.getChildren().clear();
            showList(searchTextField.getText());
            isSearch = true;
        } else if (isSearch){
            maxRow = 4;
            eventScrollPane.setVvalue(0);
            eventGrid.getChildren().clear();
            if (selectedCategory != null) {
                showList("");
            }
            else {
                showList();
            }
            isSearch = false;
        }
        searchTextField.clear();
    }

    //Auto completion
    private void handleAutoComplete(KeyEvent event) {
        String enteredText = searchTextField.getText();
        if (event.getCode() == KeyCode.BACK_SPACE || enteredText.isEmpty() ) {
            return;
        }

        String matchedSuggestion = null;
        int totalMatched = 0;
        for (String suggestion : eventList) {
            if (suggestion.toLowerCase().startsWith(enteredText.toLowerCase())) {
                matchedSuggestion = suggestion;
                break;
            }
        }

        if (matchedSuggestion != null && !matchedSuggestion.equals(enteredText)) {
            searchTextField.setText(matchedSuggestion);
            searchTextField.selectRange(enteredText.length(), matchedSuggestion.length());
        }


        for (String suggestion : eventList) {
            if (suggestion.toLowerCase().contains(enteredText.toLowerCase())) {
                if (totalMatched > 1){
                    break;
                }
                matchedSuggestion = suggestion;
                totalMatched++;
            }
        }

        if (totalMatched == 1 && matchedSuggestion != null && !matchedSuggestion.equals(enteredText) && matchedSuggestion.indexOf(enteredText) != matchedSuggestion.length()-1) {
            searchTextField.setText(matchedSuggestion);
            searchTextField.selectRange(matchedSuggestion.length() ,matchedSuggestion.length());
        }

    }

    public void showList() {
        int row = maxRow - 3;
        int column = 0;
        int i = 0;
        for (Event event : eventListData.getEvents()) {
            if (i < (maxRow - 3) * 3) {
                i++;
                continue;
            }
            if (event.getEndDate().isBefore(currentDate)) {
                continue;
            }
            if(column == 3) {
                row++;
                column = 0;
            }
            if (row == maxRow) {
                break;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/cs211/project/views/eventElement.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            EventElementController event_ = fxmlLoader.getController();
            event_.setPage(event.getName(), event.getPicture(), event.getCategory());

            int participantCount = joinEventDataSource.countParticipantsForEvent(event.getEventUUID());
            int maxParti = event.getMaxParticipants();
            event_.participantCount(participantCount, maxParti);

            anchorPane.setOnMouseClicked(event1 -> {
                try {
                    FXRouterPane.goTo("event-information", new String[] { event.getEventUUID(), currentUser.getUsername() });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            eventGrid.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }
    }


    public void showList(String eventName) {
        eventGrid.getChildren().clear();
        int row = 0;
        int column = 0;
        for (Event event : eventListData.getEvents()) {
            if (eventName != "" && !event.getName().toLowerCase().contains(eventName.toLowerCase())) {
                continue;
            }
            if (event.getEndDate().isBefore(currentDate)) {
                continue;
            }
            if (selectedCategory != null && !event.getCategory().equals(selectedCategory)) {
                continue;
            }
            if(column == 3) {
                row++;
                column = 0;
            }
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/cs211/project/views/eventElement.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            EventElementController event_ = fxmlLoader.getController();
            event_.setPage(event.getName(), event.getPicture(), event.getCategory());

            int participantCount = joinEventDataSource.countParticipantsForEvent(event.getEventUUID());
            int maxParti = event.getMaxParticipants();
            event_.participantCount(participantCount, maxParti);

            anchorPane.setOnMouseClicked(event1 -> {
                try {
                    FXRouterPane.goTo("event-information", new String[] { event.getEventUUID(), currentUser.getUsername() });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            eventGrid.add(anchorPane, column, row);
            column++;

            GridPane.setMargin(anchorPane, new Insets(10));
        }
    }
    @FXML
    public void categoryPaneOpen() {
        Timeline timeline = new Timeline();
        if(!categoryOn) {
            double targetHeight = 50;

            KeyValue keyValue = new KeyValue(categoryPane.prefHeightProperty(), targetHeight);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);

            timeline.getKeyFrames().add(keyFrame);
            categoryPane.setVisible(true);
            timeline.play();
            categoryOn = true;
            categoryOpenButton.getStyleClass().add("category-open-selected");
        }
        else {
            double targetHeight = 0;

            KeyValue keyValue = new KeyValue(categoryPane.prefHeightProperty(), targetHeight);
            KeyValue keyValue2 = new KeyValue(categoryPane.visibleProperty(), false);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.3), keyValue);
            KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.3), keyValue2);

            timeline.getKeyFrames().add(keyFrame);
            timeline.getKeyFrames().add(keyFrame2);
            timeline.play();

            categoryPane.setVisible(false);
            categoryOn = false;
            categoryOpenButton.getStyleClass().remove("category-open-selected");

        }

    }
    @FXML
    public void handleAllCategoryButton() {
        maxRow = 3;
        selectedCategory = null;
        eventGrid.getChildren().clear();
        showList();
        changeStyleClassCategoryButton(allCategoryButton);
    }
    @FXML
    public void handleCategoryExpoButton() {
        selectedCategory = "งานแสดงสินค้า";
        changeStyleClassCategoryButton(categoryExpoButton);
        showList("");
    }
    @FXML
    public void handleCategoryFestivalButton() {
        selectedCategory = "เทศกาล";
        changeStyleClassCategoryButton(categoryFestivalButton);
        showList("");
    }
    @FXML
    public void handleCategorySeminarButton() {
        selectedCategory = "อบรมสัมนา";
        changeStyleClassCategoryButton(categorySeminarButton);
        showList("");
    }
    @FXML
    public void handleCategoryHouseButton() {
        selectedCategory = "บ้านและของแต่งบ้าน";
        changeStyleClassCategoryButton(categoryHouseButton);
        showList("");
    }
    @FXML
    public void handleCategoryFoodButton() {
        selectedCategory = "อาหารและเครื่องดื่ม";
        changeStyleClassCategoryButton(categoryFoodButton);
        showList("");
    }
    @FXML
    public void handleCategoryEntertainmentButton() {
        selectedCategory = "บันเทิง";
        changeStyleClassCategoryButton(categoryEntertainmentButton);
        showList("");
    }
    @FXML
    public void handleCategoryConcertButton() {
        selectedCategory = "คอนเสิร์ต/แฟนมีตติ้ง";
        changeStyleClassCategoryButton(categoryConcertButton);
        showList("");
    }
    @FXML
    public void handleCategoryTravelButton() {
        selectedCategory = "ท่องเที่ยว";
        changeStyleClassCategoryButton(categoryTravelButton);
        showList("");
    }
    @FXML
    public void handleCategoryArtButton() {
        selectedCategory = "ศิลปะ/นิทรรศการ/ถ่ายภาพ";
        changeStyleClassCategoryButton(categoryArtButton);
        showList("");
    }
    @FXML
    public void handleCategorySportButton() {
        selectedCategory = "กีฬา";
        changeStyleClassCategoryButton(categorySportButton);
        showList("");
    }
    @FXML
    public void handleCategoryReligionButton() {
        selectedCategory = "ศาสนา";
        changeStyleClassCategoryButton(categoryReligionButton);
        showList("");
    }
    @FXML
    public void handleCategoryPetButton() {
        selectedCategory = "สัตว์เลี้ยง";
        changeStyleClassCategoryButton(categoryPetButton);
        showList("");
    }
    @FXML
    public void handleCategoryEducationButton() {
        selectedCategory = "ธุรกิจ/อาชีพ/การศึกษา";
        changeStyleClassCategoryButton(categoryEducationButton);
        showList("");
    }
    @FXML
    public void handleCategoryOtherButton() {
        selectedCategory = "อื่น ๆ";
        changeStyleClassCategoryButton(categoryOtherButton);
        showList("");
    }

    public void changeStyleClassCategoryButton(Button button){
        allCategoryButton.getStyleClass().remove("category-button-selected");
        categoryExpoButton.getStyleClass().remove("category-button-selected");
        categoryFestivalButton.getStyleClass().remove("category-button-selected");
        categorySeminarButton.getStyleClass().remove("category-button-selected");
        categoryHouseButton.getStyleClass().remove("category-button-selected");
        categoryFoodButton.getStyleClass().remove("category-button-selected");
        categoryEntertainmentButton.getStyleClass().remove("category-button-selected");
        categoryConcertButton.getStyleClass().remove("category-button-selected");
        categoryTravelButton.getStyleClass().remove("category-button-selected");
        categoryArtButton.getStyleClass().remove("category-button-selected");
        categorySportButton.getStyleClass().remove("category-button-selected");
        categoryReligionButton.getStyleClass().remove("category-button-selected");
        categoryPetButton.getStyleClass().remove("category-button-selected");
        categoryEducationButton.getStyleClass().remove("category-button-selected");
        categoryOtherButton.getStyleClass().remove("category-button-selected");
        button.getStyleClass().add("category-button-selected");
    }

}