package com.example.animalsimulation;

public class Simulation {

    WorldMap map;
    public int day;
    public int numOfAnimals;
    private int width;
    private int height;
    private double jungleRatio;
    private int startEnergy;
    private int plantEnergy;
    private int startNumOfAnimals;


    public Simulation(int width, int height, double jungleRatio, int startEnergy, int plantEnergy, int startNumOfAnimals){
        this.width = width;
        this.height = height;
        this.jungleRatio = jungleRatio;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;
        this.startNumOfAnimals = startNumOfAnimals;

        this.day = 0;
        this.map = new WorldMap(width, height, jungleRatio, startEnergy, plantEnergy);
        this.numOfAnimals = startNumOfAnimals;                                                  //początkowa ilość zwierząt na mapie
        this.map.AdamAndEve(this.numOfAnimals);
    }


    public Simulation copy(){                               //funkcja do utworzenia kopii symulacji w celu puszczenia 2 równoległych
        Simulation sim = new Simulation(this.width, this.height, this.jungleRatio, this.startEnergy, this.plantEnergy, this.startNumOfAnimals);

        sim.day = 0;
        sim.map = this.map.copy();
        sim.numOfAnimals = this.numOfAnimals;

        return sim;
    }

    public void simulateDay(){                      //symulacja cyklu jednego dnia

        if(!(this.map.animals.isEmpty())){
            this.map.removeDeceased();
            this.map.moveAnimals();
            this.map.eatPlants();
            this.map.reproduceAnimals();
            this.map.growPlants();
        }
    }

}
