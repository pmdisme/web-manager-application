package cs211.project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class EventElementController {
    @FXML
    private Label eventNameLabel;
    @FXML
    private ImageView eventImageView;
    @FXML
    private Label participantNumLabel;
    @FXML
    Label categoryLabel;

    public void participantCount(int currentParticipants, int maxParticipants) {
        if (maxParticipants == -1) {
            participantNumLabel.setText("Available: UNLIMITED");
        } else if (currentParticipants < maxParticipants) {
            int available = maxParticipants - currentParticipants;
            participantNumLabel.setText("Available: " + available);
        } else {
            participantNumLabel.setText("Available: FULL");
        }
    }

    public void setPage(String name, String imgName, String category) {
        String filePath = "data"+ File.separator + "eventPicture" + File.separator + imgName;
        File file = new File(filePath);
        Image eventImage = new Image(file.toURI().toString(),176,129,false,false);
        eventNameLabel.setText(name);
        eventImageView.setImage(eventImage);
        categoryLabel.setText("หมวดหมู่ : " + category);
    }
}
