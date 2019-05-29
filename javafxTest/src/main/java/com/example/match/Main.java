package com.example.match;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class Main extends Application {
    private static final Duration SHOW_PICK_TIME = Duration.seconds(5);
    private Timeline showingTimeline;

    private final static int CELL_LENGTH = 100;
    private GameInstance gameInstance;
    private final Label statusLine = new Label();
    private GameCallbacks callbacks;
    private int field_size;
    private Cell[][] cells;


    @Override
    public void start(Stage primaryStage) {
        do {
            field_size = inputFieldSize();
        } while (field_size <= 0 || field_size % 2 != 0);

        showingTimeline = new Timeline();
        var simulateKeyframe = new KeyFrame(
                SHOW_PICK_TIME,
                ax -> stopShowing());
        showingTimeline.getKeyFrames().add(simulateKeyframe);

        primaryStage.setTitle("Match 2");
        primaryStage.setScene(new Scene(createContent()));

        try {
            gameInstance = new GameInstance(field_size / 2, callbacks);
        } catch (GameCreateException e) {
            throw new RuntimeException(e); // should not happen
        }
        
        updateStatusLine();

        primaryStage.show();
    }

    private void stopShowing() {
        System.out.println("stop waiting");
        gameInstance.stopShowing();
        updateStatusLine();
        waitingWhileShowing = false;
    }

    private void updateStatusLine() {
        statusLine.setText(gameInstance.getStatusLine());
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private Pane createContent() {

        var root = new BorderPane();

        var table = new GridPane();
        table.setPrefSize(CELL_LENGTH * field_size, CELL_LENGTH * field_size);
        table.setAlignment(Pos.CENTER);
        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        table.setPadding(new Insets(15));

        cells = new Cell[field_size][field_size];

        for (int i = 0; i < field_size; i++) {
            for (int j = 0; j < field_size; j++) {

                var cell = new Cell(i, j);
                cells[i][j] = cell;

                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                table.add(cell, i, j);
                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);

            }
        }

        root.setCenter(table);

        var bottom = new HBox(10);
        statusLine.setText("Creating a game, please wait");
        statusLine.setFont(new Font(15));
        bottom.getChildren().add(statusLine);

        root.setBottom(bottom);

        callbacks = new GameCallbacks() {
            @Override
            public void onCellOpen(int x, int y, int value) {
                cells[x][y].setValue(value);
            }

            @Override
            public void onCellClose(int x, int y) {
                cells[x][y].setEmpty();
            }
        };

        return root;
    }

    private class Cell extends StackPane {

        private final Button button = new Button();
        private final int coordinateX;
        private final int coordinateY;
        private int value = -1;
        private final Text text = new Text();

        private Cell(int coordinateX, int coordinateY) {
            this.coordinateX = coordinateX;
            this.coordinateY = coordinateY;

            setPrefSize(CELL_LENGTH, CELL_LENGTH);

            button.setOnAction(event -> {
                makeMove(coordinateX, coordinateY);

//                draw();
            });

            button.setPrefSize(CELL_LENGTH, CELL_LENGTH);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.prefHeightProperty().bind(heightProperty());
            button.prefWidthProperty().bind(widthProperty());
            getChildren().add(button);

            text.xProperty().bind(layoutXProperty().add(widthProperty().divide(2)));
            text.yProperty().bind(layoutYProperty().add(heightProperty().divide(2)));
//            text.wrappingWidthProperty().bind(widthProperty().multiply(0.8));
            getChildren().add(text);

        }

        public void draw() {
            if (value != -1) {
                text.setText(String.valueOf(value));
            } else {
                text.setText("");
            }
        }

        public void setValue(int value) {
            this.value = value;
            draw();
        }

        public void setEmpty() {
            value = -1;
            draw();
        }
    }

    private boolean waitingWhileShowing = false;
    private void makeMove(int x, int y) {
        if (waitingWhileShowing) {
            System.out.println("makeMove: waiting");
            return;
        }
        System.out.println("makeMove: making a move");

        gameInstance.click(x, y);
        updateStatusLine();
        if (gameInstance.getState() == GameInstance.State.SHOWING_RESULT) {
            waitingWhileShowing = true;
            System.out.println("Start waiting");
            showingTimeline.playFromStart();
        }
    }


    /**
     * Shows a dialog, prompting to enter field size
     * @return inputted field size or <code>-1</code> if didn't get correct input
     */
    private int inputFieldSize() {
        TextInputDialog dialog = new TextInputDialog("4");

        dialog.setTitle("Start a game");
        dialog.setHeaderText("Enter positive even field size:");
        dialog.setContentText("Field size:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return -1;
        }
        try {
            return Integer.valueOf(result.get());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}

/*
package com.example.tictactoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static com.example.tictactoe.GameInstance.field_size;

public class Main extends Application {

    private final static int CELL_LENGTH = 200;
    private final GameInstance gameInstance = new GameInstance();
    private final Label statusLine = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private Pane createContent() {

        var root = new BorderPane();

        var table = new GridPane();
        table.setPrefSize(CELL_LENGTH * field_size, CELL_LENGTH * field_size);
        table.setAlignment(Pos.CENTER);
        table.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        table.setPadding(new Insets(15));

        for (int i = 0; i < field_size; i++) {
            for (int j = 0; j < field_size; j++) {

                var cell = new Cell(i, j);

                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setMaxHeight(Double.MAX_VALUE);
                table.add(cell, i, j);
                GridPane.setHgrow(cell, Priority.ALWAYS);
                GridPane.setVgrow(cell, Priority.ALWAYS);

            }
        }

        root.setCenter(table);

        var bottom = new HBox(10);
        statusLine.setText(gameInstance.getStatus());
        statusLine.setFont(new Font(30));
        bottom.getChildren().add(statusLine);

        root.setBottom(bottom);

        return root;
    }

    private class Cell extends Pane {

        private final Button button = new Button();
        private final int coordinateX;
        private final int coordinateY;

        private Cell(int coordinateX, int coordinateY) {
            this.coordinateX = coordinateX;
            this.coordinateY = coordinateY;

            setPrefSize(CELL_LENGTH, CELL_LENGTH);

            button.setOnAction(event -> {
                makeMove(coordinateX, coordinateY);

                draw();
            });
            button.setPrefSize(CELL_LENGTH, CELL_LENGTH);
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.prefHeightProperty().bind(heightProperty());
            button.prefWidthProperty().bind(widthProperty());
            getChildren().add(button);


        }

        public void draw() {
            switch(gameInstance.get(coordinateX, coordinateY)) {
                case CROSS:
                    drawCross();
                    break;
                case NOUGHT:
                    drawCircle();
                    break;
            }
        }

        private void drawCross(){
            double width = getWidth(), height = getHeight();
            double scale = 0.2f;
            Line line1 = new Line(scale * width, scale * height, (1 - scale) * width, (1 - scale) * height);
            Line line2 = new Line(scale * width, (1 - scale) * height, (1 - scale) * width, scale * height);

            line1.startXProperty().bind(widthProperty().divide(1 / scale));
            line1.startYProperty().bind(heightProperty().divide(1 / scale));
            line1.endXProperty().bind(widthProperty().divide(1 / (1 - scale)));
            line1.endYProperty().bind(heightProperty().divide(1 / (1 - scale)));
            getChildren().add(line1);

            line2.startXProperty().bind(widthProperty().divide(1 / scale));
            line2.startYProperty().bind(heightProperty().divide(1 / (1 - scale)));
            line2.endXProperty().bind(widthProperty().divide(1 / (1 - scale)));
            line2.endYProperty().bind(heightProperty().divide(1 / scale));
            getChildren().add(line2);

        }

        private void drawCircle() {
            double w = getWidth(), h = getHeight();
            double radius = 0.4f;
            Ellipse ellipse = new Ellipse(w / 2, h / 2, radius * w, radius * h);
            ellipse.setStroke(Color.BLACK);
            ellipse.setFill(null);
            getChildren().add(ellipse);

            ellipse.centerXProperty().bind(widthProperty().divide(2));
            ellipse.centerYProperty().bind(heightProperty().divide(2));
            ellipse.radiusXProperty().bind(widthProperty().divide(1 / radius));
            ellipse.radiusYProperty().bind(heightProperty().divide(1 / radius));
        }


    }

    private void makeMove(int x, int y) {
        try {
            gameInstance.put(x, y);
        } catch (ImpossibleMoveException e) {
            // Wrong move
            // Let's do nothing
        }
        statusLine.setText(gameInstance.getStatus());
    }


}
*/
