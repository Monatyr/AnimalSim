package com.example.animalsimulation;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;


public class Toolbar extends ToolBar {

    private MapView mapView;

    public Toolbar(MapView mapView){
        this.mapView = mapView;

        Button startBtn = new Button("Start");
        startBtn.setOnAction(this::handleStart);

        Button stopBtn = new Button("Stop");
        stopBtn.setOnAction(this::handleStop);

        Button geneBtn = new Button("Most common genepool");
        geneBtn.setOnAction(this::handleGene);

        Button trackBtn = new Button("Track/Untrack");
        trackBtn.setOnAction(this::handleTrack);

        this.getItems().addAll(startBtn, stopBtn, geneBtn, trackBtn);
    }


    private void handleStart(ActionEvent event){            //przycisk wznawiający symulację
        this.mapView.setApplicationState(1);
        this.mapView.getSimulator().start();
    }

    private void handleStop(ActionEvent event) {            //przycisk zatrzymujący symulację
        this.mapView.setApplicationState(0);
        this.mapView.getSimulator().stop();
    }

    private void handleGene(ActionEvent actionEvent) {          //przycisk podświetlający zwierzęta o dominującym genotypie
        this.handleStop(actionEvent);

        String genepool = this.mapView.getSimulation().map.mostCommonGenepool();

        for(Animal animal : this.mapView.getSimulation().map.animals){
            if(animal.genepoolToString().equals(genepool)){
                this.mapView.highlightGenepool(animal.getPostition());
            }
        }
    }

    private void handleTrack(ActionEvent actionEvent) {             //po kliknięciu na dane zwierzę na mapie i wciśnięciu przycisku Track, rozpoczyna się śledzenie
        if(this.mapView.currPos != null) {                          //zwierzęcia aż do jego śmierci lub odkliknięcia zwierzęcia lub zmiany śledzonego zwierzęcia

            Animal animal = this.mapView.simulation.map.cells.get(this.mapView.currPos).strongestAnimal();
            this.mapView.startTrackingAnimal(animal);
        }

    }

}
