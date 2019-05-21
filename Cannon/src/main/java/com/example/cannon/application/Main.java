package com.example.cannon.application;

import com.example.cannon.model.Block;
import com.example.cannon.model.RoundObject;
import com.example.cannon.model.World;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Function;

import static com.example.cannon.application.ApplicationState.*;


// TODO delete all the System.out logs
// TODO move all interactions with world to GameInstance
public class Main extends Application {
    private static final int STEP_TIME = 10; // ms

    private GameInstance game;
    private Canvas objectsCanvas;
    private ApplicationState state = WAITING_COMMAND;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Cannon");

        game = new GameInstance(2);

        var mainScene = new Scene(createContent(game.world));

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        mainScene.setOnKeyPressed(event -> {
            if (state != WAITING_COMMAND) {
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
            }

            cycle();
        });
        cycle();
    }

    private void cycle() {
        state = SIMULATING;
        var simulatingTimeline = new Timeline();
        var simulateKeyframe = new KeyFrame(
                Duration.millis(STEP_TIME),
                ax -> {
                    if (game.world.physicsStep()) {
                        simulatingTimeline.playFromStart();
                    } else {
                        state = WAITING_COMMAND;
                    }
                    renderObjects();
                });
        simulatingTimeline.getKeyFrames().add(simulateKeyframe);
        simulatingTimeline.playFromStart();
    }

    private Pane createContent(World world) {
        var root = new BorderPane();

        Canvas terrainCanvas = new Canvas();
        terrainCanvas.setHeight(world.getTerrain().height);
        terrainCanvas.setWidth(world.getTerrain().width);
        objectsCanvas = new Canvas();
        objectsCanvas.setHeight(world.getTerrain().height);
        objectsCanvas.setWidth(world.getTerrain().width);

        Function<Integer, Color> colorPicker = t -> {
            if (t == Block.EMPTY) {
                return Color.TRANSPARENT;
            }
            if (t == Block.GROUND) {
                return Color.LIGHTGRAY;
            }
            return Color.GREEN;
        };

        world.getTerrain().setCanvas(terrainCanvas, colorPicker);

        renderObjects();
        root.setCenter(objectsCanvas);
        root.getChildren().add(terrainCanvas);

        return root;
    }

    private void renderObjects() {
        objectsCanvas.getGraphicsContext2D().clearRect(0, 0, objectsCanvas.getWidth(), objectsCanvas.getHeight());
        for (RoundObject obj : game.world.getObjects()) {
            obj.render(objectsCanvas.getGraphicsContext2D());
        }
    }
}