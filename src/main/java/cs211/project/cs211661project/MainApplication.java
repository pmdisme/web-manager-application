package cs211.project.cs211661project;

import cs211.project.services.FXRouter;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXRouter.bind(this, stage, "Event Manager");
        configRoute();
        FXRouter.goTo("login");
    }

    public static void configRoute()
    {
        //ถ้าจะเพิ่มหน้าให้เพิ่มไว้ที่ MainPageController.java FX.RouterPane.when("<>", viewPath + "<>.fxml");
        String viewPath = "cs211/project/views/";
        FXRouter.when("register", viewPath + "register.fxml");
        FXRouter.when("mainPage", viewPath + "mainPage.fxml");
        FXRouter.when("login", viewPath + "login.fxml");
        FXRouter.when("adminPage", viewPath + "adminPage.fxml");
        FXRouter.when("creators", viewPath + "creators.fxml");
        FXRouter.when("instructions", viewPath + "instructions.fxml");
        FXRouter.when("reset-password", viewPath + "reset-password.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}