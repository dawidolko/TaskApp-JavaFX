package pl.rozowi.app.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import pl.rozowi.app.MainApplication;

import java.io.IOException;

/**
 * Controller that displays the application's splash screen.
 * Manages the screen's fade-in and fade-out animations and the transition to the
 * login screen.
 */
public class SplashScreenController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView logoImageView;

    /**
     * Initializes the controller - starts the splash screen animations.
     * Runs the logo zoom animation and a smooth fade-out of the whole screen.
     * When the animation ends, automatically switches to the login screen.
     */
    public void initialize() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), logoImageView);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), rootPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setDelay(Duration.seconds(3));

        fadeTransition.setOnFinished(event -> {
            try {
                MainApplication.switchScene("/fxml/login.fxml", "TaskApp - Panel logowania");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        scaleTransition.play();
        fadeTransition.play();
    }
}
