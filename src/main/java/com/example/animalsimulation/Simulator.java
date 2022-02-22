package com.example.animalsimulation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class Simulator {

    private Timeline timeline;
    private MapView mapView;
    private Simulation simulation;

    public Simulator(MapView mapView, Simulation simulation){
        this.mapView = mapView;
        this.simulation = simulation;
        this.timeline = new Timeline(new KeyFrame(Duration.millis(50), this::simulateDay));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void simulateDay(ActionEvent event){
        this.simulation.simulateDay();
        this.mapView.draw();
    }


    public void start(){
        this.timeline.play();
    }

    public void stop(){
        this.timeline.stop();
    }
}
