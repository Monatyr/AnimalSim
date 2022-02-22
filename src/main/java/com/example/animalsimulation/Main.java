package com.example.animalsimulation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{

        int width = 50;
        int height = 35;
        double jungleRatio = 1.0/3.0;
        int startEnergy = 100;
        int plantEnergy = 50;
        int startNumOfAnimals = 100;
        int numOfSimulations = 2;


        Simulation simulation = new Simulation(width,height,jungleRatio,startEnergy, plantEnergy, startNumOfAnimals);
        MainView mainView = new MainView(numOfSimulations, simulation);
        Scene scene = new Scene(mainView, 850, 338*numOfSimulations);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("World Simulation");
        stage.show();

        mainView.draw();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
