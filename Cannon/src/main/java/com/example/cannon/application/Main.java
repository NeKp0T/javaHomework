package com.example.cannon.application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {
    private static final int STEP_TIME = 10; // ms

    private GameInstance game;
    private Canvas objectsCanvas;
    private ApplicationState state = ApplicationState.WAITING_COMMAND;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Cannon");

        game = new GameInstance(2);

        var mainScene = new Scene(createContent(game));

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        mainScene.setOnKeyPressed(event -> {
            if (state != ApplicationState.WAITING_COMMAND) {
                return;
            }
            switch (event.getCode()) {
                case D:
                    game.commandPlayer(GameInstance.Control.RIGHT);
                    break;
                case A:
                    game.commandPlayer(GameInstance.Control.LEFT);
                    break;
                case Q:
                    game.commandPlayer(GameInstance.Control.AIM_LEFT);
                    break;
                case E:
                    game.commandPlayer(GameInstance.Control.AIM_RIGHT);
                    break;
                case SPACE:
                    game.commandPlayer(GameInstance.Control.FIRE);
                    break;
                case DIGIT1:
                    game.commandPlayer(GameInstance.Control.SELECT_0);
                    break;
                case DIGIT2:
                    game.commandPlayer(GameInstance.Control.SELECT_1);
                    break;
                case DIGIT3:
                    game.commandPlayer(GameInstance.Control.SELECT_2);
                    break;
            }

            cycle();
        });
        cycle();
    }

    private void endGame(int winner) {
        if (state == ApplicationState.GAME_ENDED) {
            return;
        }
        state = ApplicationState.GAME_ENDED;

        var alert = new Alert(Alert.AlertType.INFORMATION);

        if (winner >= 0) {
            alert.setTitle("Win");
            alert.setHeaderText("We have a winner");
            alert.setContentText("It's player " + (winner + 1));
        } else {
            alert.setTitle("Draw");
            alert.setHeaderText(null);
            alert.setContentText("They are all dead");
        }

        alert.showAndWait();
        System.out.println("winner alerted");
    }

    private void cycle() {
        state = ApplicationState.SIMULATING;
        var simulatingTimeline = new Timeline();
        var simulateKeyframe = new KeyFrame(
                Duration.millis(STEP_TIME),
                ax -> {
                    if (game.worldPhysicsStep()) {
                        simulatingTimeline.playFromStart();
                    } else {
                        state = ApplicationState.WAITING_COMMAND;

                        if (game.getWinner() != -1) {
                            Platform.runLater(() -> endGame(game.getWinner()));
                        }
                    }
                    renderObjects();
                });
        simulatingTimeline.getKeyFrames().add(simulateKeyframe);
        simulatingTimeline.playFromStart();
    }

    private Pane createContent(GameInstance game) {
        var root = new BorderPane();

        var terrainCanvas = new Canvas();
        terrainCanvas.setHeight(game.getTerrainHeight());
        terrainCanvas.setWidth(game.getTerrainWidth());
        objectsCanvas = new Canvas();
        objectsCanvas.setHeight(game.getTerrainHeight());
        objectsCanvas.setWidth(game.getTerrainWidth());

        game.setCanvas(terrainCanvas);

        renderObjects();
        root.setCenter(terrainCanvas);
        root.getChildren().add(objectsCanvas);

        return root;
    }

    private void renderObjects() {
        objectsCanvas.getGraphicsContext2D().clearRect(0, 0, objectsCanvas.getWidth(), objectsCanvas.getHeight());
        game.renderObjects(objectsCanvas.getGraphicsContext2D());
    }

    private enum ApplicationState {
        WAITING_COMMAND, SIMULATING, GAME_ENDED
    }
}