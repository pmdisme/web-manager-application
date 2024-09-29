package cs211.project.controllers;

import cs211.project.services.FXRouter;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InstructionsController {

    @FXML
    private Text instructionText;
    @FXML private Hyperlink downloadManualHyperlink;

    @FXML
    public void initialize() {
        try {
            Path path = Paths.get("data" + File.separator + "instruction.txt");
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            instructionText.setText(content);
        } catch (IOException e) {
            instructionText.setText("Error reading the instruction file.");
        }
        downloadManualHyperlink.setOnAction(e -> {
            openLinkInBrowser("https://drive.google.com/uc?export=download&id=1rgjgU9F-Hn23BByTgdinOpmXwekISr3Q");
        });

    }

    @FXML
    private void goToLogin() {
        try {
            FXRouter.goTo("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void openLinkInBrowser(String url) {
        try {
            URI uri = new URI(url);
            java.awt.Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }
}
