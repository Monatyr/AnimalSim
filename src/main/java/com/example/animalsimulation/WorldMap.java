package com.example.animalsimulation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WorldMap {

    public HashMap<Vector2D, MapCell> cells = new HashMap<>();

    public int width;
    public int height;
    public double jungleRatio;
    public int day;
    public int startEnergy;
    public int plantEnergy;
    public int plants = 0;

    public Vector2D steppeLowerLeft;
    public Vector2D steppeUpperRight;
    public Vector2D jungleLowerLeft;
    public Vector2D jungleUpperRight;
    public List<Vector2D> jungleCells = new ArrayList<>();              //lista do sprawdzania czy roślina może urosnąć w dżungli
    List<Animal> animals = new ArrayList<Animal>();
    List<Animal> deceasedAnimals = new ArrayList<Animal>();
    HashMap<String, Counter> genepools = new HashMap<>();


    public WorldMap(int width, int height, double jungleRatio, int startEnergy, int plantEnergy) {
        this.height = height;
        this.width = width;
        this.jungleRatio = jungleRatio;
        this.day = 0;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;

        this.steppeLowerLeft = new Vector2D(0, 0);
        this.steppeUpperRight = new Vector2D(this.width-1, this.height-1);
        this.jungleLowerLeft = new Vector2D((int) ((this.width - this.width * this.jungleRatio)/2.0), (int) ((this.height - this.height * this.jungleRatio)/2.0));
        this.jungleUpperRight = new Vector2D((this.jungleLowerLeft.x + (int)(this.width * this.jungleRatio))-1, (this.jungleLowerLeft.y + (int)(this.height * this.jungleRatio))-1);

        for (int i = this.jungleLowerLeft.y; i <= this.jungleUpperRight.y; i++) {
            for (int j = this.jungleLowerLeft.x; j <= this.jungleUpperRight.x; j++) {
                jungleCells.add(new Vector2D(j, i));
            }
        }


    }


    public WorldMap copy(){
        WorldMap newMap = new WorldMap(this.width, this.height, this.jungleRatio, this.startEnergy, this.plantEnergy);
        newMap.steppeLowerLeft = this.steppeLowerLeft;
        newMap.steppeUpperRight = this.steppeUpperRight;
        newMap.jungleLowerLeft = this.jungleLowerLeft;
        newMap.jungleUpperRight = this.jungleUpperRight;

        for (int i = newMap.jungleLowerLeft.y; i <= newMap.jungleUpperRight.y; i++) {
            for (int j = newMap.jungleLowerLeft.x; j <= newMap.jungleUpperRight.x; j++) {
                jungleCells.add(new Vector2D(j, i));
            }
        }

        for(MapCell cell : this.cells.values()){
            newMap.cells.put(cell.position, cell.copy(newMap));
        }

        return newMap;
    }


    public void AdamAndEve(int numOfAnimals){       //funkcja rozmieszczająca pierwsze zwierzęta na mapie
        int counter = 0;
        while(counter < numOfAnimals){

            int xTemp = ThreadLocalRandom.current().nextInt(0,this.width);
            int yTemp = ThreadLocalRandom.current().nextInt(0, this.height);

            Vector2D tempPos = new Vector2D(xTemp, yTemp);

            if(!(this.cells.containsKey(tempPos))) {

                Animal animal = new Animal(this.startEnergy, tempPos, this);            //dodaję pierwsze zwierzęta do listy zwierząt w mapie
                this.animals.add(animal);

                MapCell newCell = new MapCell(this, tempPos, false);
                newCell.animals.add(animal);
                this.cells.put(tempPos, newCell);                    //dodaję pierwsze zwierzęta do listy w odpowiednich komórkach mapy

                counter++;
            }
        }

    }


    public void reproduceAnimals() {                    //funkcja przechodząca po wszystkich aktywnych komórkach mapy i wywołująca funkcję rozmnażania dla każdej z nich

        for (MapCell cell : cells.values()) {
            Animal child = cell.reproduce();
            if (child != null){
                this.animals.add(child);                //jeśli urodziło się dziecko, to dodajemy je do listy zwierząt w mapie
            }
        }
    }

    public void growPlants() {                                  //funkcja tworząca 2 nowe rośliny - jedną w dżunlgi, drugą na stepie
        List<Vector2D> tempJungle = new ArrayList<>();                   //na początku szukam miejsca na wyrośnięcie rośliny w dżungli, o ile takie istnieje
        for (Vector2D v : jungleCells) {
            if (!(this.cells.containsKey(v)))                   //roślina nie może urosnąć tam, gdzie jest inna roślina lub tam, gdzie stoi zwierzę
                tempJungle.add(v);
        }

        if(tempJungle.size() > 0){
            Vector2D plantPos = tempJungle.get(ThreadLocalRandom.current().nextInt(0, tempJungle.size()));
            MapCell newJungleCell = new MapCell(this, plantPos, true);
            this.cells.put(plantPos, newJungleCell);
            this.plants++;
        }



        int tempX = ThreadLocalRandom.current().nextInt(0, this.width);           //następnie szukam pustego miejsca dla rośliny rosnącej na stepie
        int tempY = ThreadLocalRandom.current().nextInt(0, this.height);

        boolean flag = true;

        while(this.cells.containsKey(new Vector2D(tempX, tempY)) ||
                (tempX >= this.jungleLowerLeft.x && tempX <= this.jungleUpperRight.x
                        && tempY >= this.jungleLowerLeft.y && tempY <= this.jungleUpperRight.y)){

            if(this.cells.size() == (this.height)*(this.width)){
                flag = false;
                break;
            }
            tempX = ThreadLocalRandom.current().nextInt(0, this.width);
            tempY = ThreadLocalRandom.current().nextInt(0, this.height);
        }

        if(flag) {
            Vector2D steppePlantPos = new Vector2D(tempX, tempY);
            MapCell newSteppeCell = new MapCell(this, steppePlantPos, true);
            this.cells.put(steppePlantPos, newSteppeCell);
            plants++;
        }
        this.day++;                                                                             //zwiększenie numeru epoki po przejściu przez wszystkie funkcje
    }

    public void removeDeceased(){                                               //funkcja pozbywająca się zmarłych zwierząt

        Comparator Comp = new EnergyComparator();

        for(MapCell cell : this.cells.values()){

            SortedSet<Animal> newAnimals = new TreeSet<Animal>(Comp);

            for(Animal animal : cell.animals){
                if(animal.isAlive()) {
                    newAnimals.add(animal);
                }
                else{
                    this.animals.remove(animal);
                    animal.deathDay = this.day;
                    this.deceasedAnimals.add(animal);
                }
            }

            cell.animals = newAnimals;
        }
    }

    public void moveAnimals(){                                                  //funkcja ruszająca wszystkie zwierzęta na mapie

        HashMap<Vector2D, MapCell> newCells = new HashMap<>();

        for(MapCell cell : this.cells.values()){

            if(cell.hasPlant()){
                if(newCells.containsKey(cell.position)){
                    newCells.get(cell.position).plantGrown();
                }
                else{
                    newCells.put(cell.position, new MapCell(this, cell.position, true));
                }
            }

            if(cell.hasAnimals()) {
                for (Animal animal : cell.animals) {

                    Vector2D newPos = animal.move();

                    if(newCells.containsKey(newPos)){
                        newCells.get(newPos).animals.add(animal);
                    }
                    else{
                        MapCell newCell = new MapCell(this, newPos, false);
                        newCell.animals.add(animal);
                        newCells.put(newPos, newCell);
                    }

                }
            }

        }

        this.cells = newCells;
    }

    public void eatPlants(){                                            //funkcja zachęcająca zwierzęta do jedzenia roślin
        for(MapCell cell : this.cells.values()){
            if(cell.hasPlant() && cell.hasAnimals()){
                int numOfStrongest = cell.findNumberOfStrongest();              //dzielę energię ze zjedzenia rośliny na równe części dla najsilniejszych zwierząt
                int index = 0;
                for(Animal animal : cell.animals){
                    if(index >= numOfStrongest)
                        break;
                    animal.eatPlant(this.plantEnergy/numOfStrongest);       //dodawanie energii do zwierzęcia
                    index++;
                }
                cell.plantEaten();                                                  //usuwanie rośliny z komórki
                this.plants--;
            }
        }
    }

    public float averageEnergy(){
        float totalEnergy = 0;
        float aliveAnimals = 0;

        if(!(this.animals.isEmpty())) {
            for (Animal animal : this.animals) {
                if (animal.isAlive()) {
                    totalEnergy += animal.energy;
                    aliveAnimals += 1;
                }
            }
        }
        return totalEnergy/aliveAnimals;
    }

    public String mostCommonGenepool(){

        this.genepools = new HashMap<>();

        for(Animal animal : this.animals){
            String key = animal.genepoolToString();

            if(this.genepools.containsKey(key))
                this.genepools.get(key).value++;
            else{
                this.genepools.put(key, new Counter(1, key));
            }
        }

        String mostCommon = "";
        int counter = 0;

        for(Counter el : this.genepools.values()){
            if(el.value > counter){
                counter = el.value;
                mostCommon = el.genepool;
            }
        }

        return mostCommon;
    }

    public double averageLifeSpan(){
        double totalTime = 0;

        for(Animal animal : this.deceasedAnimals){
            totalTime+=animal.deathDay-animal.birthDay;
        }

        return totalTime/this.deceasedAnimals.size();
    }

    public double averageChildrenNumber(){
        double totalNo = 0;

        for(Animal animal : this.animals){
            totalNo += animal.children.size();
        }

        return totalNo/this.animals.size();
    }
}

