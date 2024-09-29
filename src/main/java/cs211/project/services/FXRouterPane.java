package cs211.project.services;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;

public final class FXRouterPane {
    private static final String WINDOW_TITLE = "";
    private static final Double WINDOW_WIDTH = 1180.0D;
    private static final Double WINDOW_HEIGHT = 800.0D;
    private static final Double FADE_ANIMATION_DURATION = 1180.0D;
    private static FXRouterPane router;
    private static Object mainRef;
    private static String windowTitle;
    private static Double windowWidth;
    private static Double windowHeight;
    private static String animationType;
    private static Double animationDuration;
    private static AbstractMap<String, RouteScene> routes = new HashMap();
    private static RouteScene currentRoute;
    @FXML
    static
    StackPane content;

    private FXRouterPane() {
    }

    public static void clearContent() {
        content = null;
    }
    public static void bind(Object ref, StackPane cnt) {
        checkInstances(ref, cnt);
    }

    public static void bind(Object ref, StackPane cnt, String winTitle) {
        checkInstances(ref, cnt);
        windowTitle = winTitle;
    }

    public static void bind(Object ref, StackPane cnt, double winWidth, double winHeight) {
        checkInstances(ref, cnt);
        windowWidth = winWidth;
        windowHeight = winHeight;
    }

    public static void bind(Object ref, StackPane cnt, String winTitle, double winWidth, double winHeight) {
        checkInstances(ref, cnt);
        windowTitle = winTitle;
        windowWidth = winWidth;
        windowHeight = winHeight;
    }

    private static void checkInstances(Object ref, StackPane cnt) {
        if (mainRef == null) {
            mainRef = ref;
        }

        if (router == null) {
            router = new FXRouterPane();
        }

        if (content == null) {
            content = cnt;
        }

    }

    public static void when(String routeLabel, String scenePath) {
        RouteScene routeScene = new RouteScene(scenePath);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, String winTitle) {
        RouteScene routeScene = new RouteScene(scenePath, winTitle);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, double sceneWidth, double sceneHeight) {
        RouteScene routeScene = new RouteScene(scenePath, sceneWidth, sceneHeight);
        routes.put(routeLabel, routeScene);
    }

    public static void when(String routeLabel, String scenePath, String winTitle, double sceneWidth, double sceneHeight) {
        RouteScene routeScene = new RouteScene(scenePath, winTitle, sceneWidth, sceneHeight);
        routes.put(routeLabel, routeScene);
    }

    public static void goTo(String routeLabel) throws IOException {
        RouteScene route = (RouteScene)routes.get(routeLabel);
        loadNewRoute(route);
    }

    public static void goTo(String routeLabel, Object data) throws IOException {
        RouteScene route = (RouteScene)routes.get(routeLabel);
        route.data = data;
        loadNewRoute(route);
    }

    private static void loadNewRoute(RouteScene route) throws IOException {
        currentRoute = route;
        String scenePath = "/" + route.scenePath;
        Parent resource = (Parent)FXMLLoader.load((new Object() {
        }).getClass().getResource(scenePath));
        content.getChildren().removeAll();
        content.getChildren().setAll(resource);
        routeAnimation(resource);
    }

    public static void startFrom(String routeLabel) throws Exception {
        goTo(routeLabel);
    }

    public static void startFrom(String routeLabel, Object data) throws Exception {
        goTo(routeLabel, data);
    }

    public static void setAnimationType(String anType) {
        animationType = anType;
    }

    public static void setAnimationType(String anType, double anDuration) {
        animationType = anType;
        animationDuration = anDuration;
    }

    private static void routeAnimation(Parent node) {
        String anType = animationType != null ? animationType.toLowerCase() : "";
        byte var3 = -1;
        switch(anType.hashCode()) {
            case 3135100:
                if (anType.equals("fade")) {
                    var3 = 0;
                }
            default:
                switch(var3) {
                    case 0:
                        Double fd = animationDuration != null ? animationDuration : FADE_ANIMATION_DURATION;
                        FadeTransition ftCurrent = new FadeTransition(Duration.millis(fd), node);
                        ftCurrent.setFromValue(0.0D);
                        ftCurrent.setToValue(1.0D);
                        ftCurrent.play();
                    default:
                }
        }
    }

    public static Object getData() {
        return currentRoute.data;
    }

    private static class RouteScene {
        private String scenePath;
        private String windowTitle;
        private double sceneWidth;
        private double sceneHeight;
        private Object data;

        private RouteScene(String scenePath) {
            this(scenePath, getWindowTitle(), getWindowWidth(), getWindowHeight());
        }

        private RouteScene(String scenePath, String windowTitle) {
            this(scenePath, windowTitle, getWindowWidth(), getWindowHeight());
        }

        private RouteScene(String scenePath, double sceneWidth, double sceneHeight) {
            this(scenePath, getWindowTitle(), sceneWidth, sceneHeight);
        }

        private RouteScene(String scenePath, String windowTitle, double sceneWidth, double sceneHeight) {
            this.scenePath = scenePath;
            this.windowTitle = windowTitle;
            this.sceneWidth = sceneWidth;
            this.sceneHeight = sceneHeight;
        }

        private static String getWindowTitle() {
            return FXRouterPane.windowTitle != null ? FXRouterPane.windowTitle : "";
        }

        private static double getWindowWidth() {
            return FXRouterPane.windowWidth != null ? FXRouterPane.windowWidth : FXRouterPane.WINDOW_WIDTH;
        }

        private static double getWindowHeight() {
            return FXRouterPane.windowHeight != null ? FXRouterPane.windowHeight : FXRouterPane.WINDOW_HEIGHT;
        }
    }
}