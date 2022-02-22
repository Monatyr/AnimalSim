package com.example.animalsimulation;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;

public class MainView extends VBox {

    private Canvas canvas;
    private int numOfSimulations;

    private Simulation simulation1;
    private Simulation simulation2;

    private MapView map1;
    private MapView map2;

    public MainView(int numOfSimulations, Simulation simulation){

        this.numOfSimulations = numOfSimulations;
        this.simulation1 = simulation;

        if(this.numOfSimulations == 1){
            this.canvas = new Canvas(1000, 350);
            map1 = new MapView(simulation1);

            this.getChildren().add(map1);
        }
        else{
            this.canvas = new Canvas(1000, 700);
            this.simulation2 = this.simulation1.copy();

            map1 = new MapView(simulation1);
            map2 = new MapView(simulation2);

            this.getChildren().addAll(map1, map2);
        }

    }


    public void draw(){
        if(this.numOfSimulations == 1){
            map1.draw();
        }
        else{
            map1.draw();
            map2.draw();
        }
    }
}
