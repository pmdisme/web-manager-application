package cs211.project.controllers;

import cs211.project.services.FXRouter;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class CreatorsController {
    @FXML private ImageView tarnImageView;
    @FXML private ImageView newImageView;
    @FXML private ImageView nuttImageView;
    @FXML private ImageView iceImageView;

    public void initialize() {
        tarnImageView.setImage(new Image(getClass().getResource("/cs211/project/images/tarn.jpg").toExternalForm()));
        newImageView.setImage(new Image(getClass().getResource("/cs211/project/images/new.jpg").toExternalForm()));
        nuttImageView.setImage(new Image(getClass().getResource("/cs211/project/images/nutt.jpg").toExternalForm()));
        iceImageView.setImage(new Image(getClass().getResource("/cs211/project/images/ice.jpg").toExternalForm()));

        tarnImageView.setFitHeight(170);
        tarnImageView.setFitWidth(170);
        newImageView.setFitHeight(170);
        newImageView.setFitWidth(170);
        nuttImageView.setFitHeight(170);
        nuttImageView.setFitWidth(170);
        iceImageView.setFitHeight(170);
        iceImageView.setFitWidth(170);
    }

    @FXML
    private void goToLogin() {
        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
