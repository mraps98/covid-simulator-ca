package edu.semo.casim;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    private Stage stage;
    private BorderPane content;
    private CAGridAnimator anim;
    private TextField txtWidth;
    private TextField txtHeight;
    private TextField txtInfected;
    private Label lblMaxInfected;
    private Label lblPercentDead;
    private Button btnAnimate;
    private Slider sldSpeed;
    private Label lblGen;

    @Override
    public void start(@SuppressWarnings("exports") Stage s) throws IOException {
        stage = s;

        // create the content pane
        content = new BorderPane();

        // create the bottom button bar
        HBox buttons = new HBox();
        Button btnNew = new Button("Create");
        txtWidth = new TextField("800");
        txtHeight = new TextField("800");
        txtInfected = new TextField("-1");
        buttons.getChildren().addAll(new Label("Percent Infected"), txtInfected, new Label("Width:"), txtWidth, new Label("Height:"), txtHeight, btnNew);
        content.setBottom(buttons);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));
        buttons.setSpacing(10);

        // create the top bar
        buttons = new HBox();
        buttons.setPadding(new Insets(10));
        buttons.setSpacing(10);
        content.setTop(buttons);
        btnAnimate = new Button("Go");
        btnAnimate.setDisable(true);
        buttons.getChildren().add(btnAnimate);
        sldSpeed = new Slider(0.01, 1.0, 1.0);
        buttons.getChildren().addAll(new Label("Speed: "), sldSpeed);
        lblGen = new Label();
        buttons.getChildren().addAll(new Label("Generation: "), lblGen);
        lblMaxInfected = new Label("0");
        buttons.getChildren().addAll(new Label("Max infected: "), lblMaxInfected);
        lblPercentDead = new Label("0");
        buttons.getChildren().addAll(new Label("percent dead: "), lblPercentDead);

        // set up button events
        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newClicked();
            }
        });
        btnAnimate.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                animateClicked();
            }

        });

        // set up slider event
        sldSpeed.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                anim.setSpeed(newValue.doubleValue());
            }

        });


        //set up the scene
        Scene scene = new Scene(content, 900, 900);
        stage.setTitle("Cellular Automaton");
        stage.setScene(scene);
        stage.show();
    }


    private void newClicked()
    {
        int width = Integer.parseInt(txtWidth.getText());
        int height = Integer.parseInt(txtHeight.getText());
        int percentInfected = Integer.parseInt(txtInfected.getText());

        //create the grid renderer
        anim = new CAGridAnimator(new Covid7State(width, height));
        ScrollPane scrollGrid = new ScrollPane(anim);
        content.setCenter(scrollGrid);
        BorderPane.setAlignment(scrollGrid, Pos.CENTER);
        if(percentInfected != -1){
            anim.getCA().randomize(percentInfected);
        }else{
            anim.getCA().randomize();
        }
        anim.render();
        anim.setSpeed(0.01);

        //enable the animation button and set the speed
        btnAnimate.setDisable(false);
        btnAnimate.setText("Go");
        lblMaxInfected.setText("0");
        lblPercentDead.setText("0%");
        anim.setSpeed(sldSpeed.getValue());
        updateGeneration();

        anim.setUpdater(new CAGridAnimator.Updater(){

            @Override
            public void update(CAGridAnimator grid) {
                updateGeneration();
                updateMaxInfected();
                updatePercentDead();
            }
            
        });
    }


    private void animateClicked()
    {
        if(btnAnimate.getText().equals("Go")) {
            btnAnimate.setText("Stop");
            anim.start();
        } else {
            btnAnimate.setText("Go");
            anim.stop();
        }
    }


    private void updateGeneration()
    {
        lblGen.setText(Integer.toString(anim.getCA().getGeneration()));
    }

    private void updateMaxInfected(){
        lblMaxInfected.setText(Integer.toString(((Covid7State) anim.getCA()).getMaxInfected()));
    }

    private void updatePercentDead(){
        lblPercentDead.setText(Double.toString(((Covid7State) anim.getCA()).getPercentDead()) + "%");
    }


    public static void main(String[] args) {
        launch(args);
    }

}
