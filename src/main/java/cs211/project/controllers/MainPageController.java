package cs211.project.controllers;

import cs211.project.models.User;
import cs211.project.services.FXRouter;
import cs211.project.services.FXRouterPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;

public class MainPageController {
    @FXML
    private BorderPane window;
    @FXML
    private StackPane content;
    @FXML
    private Button eventButton;
    @FXML
    private Button createEventButton;
    @FXML
    private Button myEventButton;
    @FXML
    private Button myTeamButton;
    @FXML
    private Button userInfoButton;
    @FXML
    private ImageView eventNavBarImage;
    @FXML
    private ImageView createEventNavBarImage;
    @FXML
    private ImageView myEventsNavBarImage;
    @FXML
    private ImageView myTeamsNavBarImage;
    @FXML
    private ImageView userInfoNavBarImage;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView userImageView;

    private User currentUser;
    private ImageView currentNavBarImage;

    @FXML
    public void initialize() {
        currentUser = (User) FXRouter.getData();
        Circle clip = new Circle(50, 50, 50);
        userImageView.setClip(clip);
        nameLabel.setText(currentUser.getName());
        userImageView.setImage(currentUser.getProfilePicture(100, 100, false, false));
        changeStyleClassButton(eventButton);
        FXRouterPane.clearContent();
        FXRouterPane.bind(this, content, "Event Manager");
        configRoute();
        try {
            FXRouterPane.goTo("event-list", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentNavBarImage = eventNavBarImage;
        mouseIn(eventNavBarImage);

        eventButton.setOnMouseEntered(event -> {
            mouseIn(eventNavBarImage);
        });

        createEventButton.setOnMouseEntered(event -> {
            mouseIn(createEventNavBarImage);
        });

        myEventButton.setOnMouseEntered(event -> {
            mouseIn(myEventsNavBarImage);
        });

        myTeamButton.setOnMouseEntered(event -> {
            mouseIn(myTeamsNavBarImage);
        });

        userInfoButton.setOnMouseEntered(event -> {
            mouseIn(userInfoNavBarImage);
        });


        eventButton.setOnMouseExited(event -> {
            mouseOut(eventNavBarImage);
        });

        createEventButton.setOnMouseExited(event -> {
            mouseOut(createEventNavBarImage);
        });

        myEventButton.setOnMouseExited(event -> {
            mouseOut(myEventsNavBarImage);
        });

        myTeamButton.setOnMouseExited(event -> {
            mouseOut(myTeamsNavBarImage);
        });

        userInfoButton.setOnMouseExited(event -> {
            mouseOut(userInfoNavBarImage);
        });
    }
    public static void configRoute()
    {
        String viewPath = "cs211/project/views/";
        FXRouterPane.when("mainPage", viewPath + "mainPage.fxml");
        FXRouterPane.when("event-management", viewPath + "event-management.fxml");
        FXRouterPane.when("event-list", viewPath + "event-list.fxml");
        FXRouterPane.when("event-participant-management", viewPath + "event-participant-management.fxml");
        FXRouterPane.when("user-information", viewPath + "user-information.fxml");
        FXRouterPane.when("create-event", viewPath + "create-event.fxml");
        FXRouterPane.when("event-information", viewPath + "event-information.fxml");
        FXRouterPane.when("my-events", viewPath + "my-events.fxml");
        FXRouterPane.when("event-team-management", viewPath + "event-team-management.fxml");
        FXRouterPane.when("create-team", viewPath + "create-team.fxml");
        FXRouterPane.when("teamofevent-list", viewPath + "teamofevent-list.fxml");
        FXRouterPane.when("myteam-list", viewPath + "myteam-list.fxml");
        FXRouterPane.when("team-communication", viewPath + "team-communication.fxml");
        FXRouterPane.when("team-management", viewPath + "team-management.fxml");
        FXRouterPane.when("edit-participant", viewPath + "edit-participant.fxml");
        FXRouterPane.when("participant-activity",viewPath + "participant-activity.fxml");
        FXRouterPane.when("reset-password", viewPath + "reset-password.fxml");
    }
    @FXML
    public void goToEventList()  {
        try {
            changeStyleClassButton(eventButton);
            changeCurrentNavBarImage(eventNavBarImage);
            FXRouterPane.goTo("event-list", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void goToCreateEvent()  {
        try {
            changeStyleClassButton(createEventButton);
            changeCurrentNavBarImage(createEventNavBarImage);
            FXRouterPane.goTo("create-event", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void goToMyEvents()  {
        try {
            changeStyleClassButton(myEventButton);
            changeCurrentNavBarImage(myEventsNavBarImage);
            FXRouterPane.goTo("my-events", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void goToUserInformation()  {
        try {
            changeStyleClassButton(userInfoButton);
            changeCurrentNavBarImage(userInfoNavBarImage);
            FXRouterPane.goTo("user-information", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleMyTeamButton(){
        try {
            changeStyleClassButton(myTeamButton);
            changeCurrentNavBarImage(myTeamsNavBarImage);
            FXRouterPane.goTo("myteam-list", currentUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeStyleClassButton(Button button){
        eventButton.getStyleClass().remove("navigation-button-selected");
        createEventButton.getStyleClass().remove("navigation-button-selected");
        myEventButton.getStyleClass().remove("navigation-button-selected");
        myTeamButton.getStyleClass().remove("navigation-button-selected");
        userInfoButton.getStyleClass().remove("navigation-button-selected");
        button.getStyleClass().add("navigation-button-selected");
    }

    public void mouseIn(ImageView imageView) {
        Timeline timeline = new Timeline();
        imageView.setVisible(true);
        KeyValue keyValue = new KeyValue(imageView.fitWidthProperty(), 25);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.15), keyValue);

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void mouseOut(ImageView imageView) {
        if (currentNavBarImage != null && currentNavBarImage.equals(imageView)) {
            return;
        }
        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(imageView.fitWidthProperty(), 1);
        KeyValue keyValue2 = new KeyValue(imageView.visibleProperty(), false);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.15), keyValue);
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.15), keyValue2);

        timeline.getKeyFrames().add(keyFrame);
        timeline.getKeyFrames().add(keyFrame2);
        timeline.play();
    }

    @FXML
    public void logout() {
        try {
            FXRouter.goTo("login");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void changeCurrentNavBarImage(ImageView imageView) {
        ImageView oldImageView = currentNavBarImage;
        currentNavBarImage = imageView;
        if (oldImageView != null) {
            mouseOut(oldImageView);
        }
    }

}