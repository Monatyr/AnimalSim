package com.example.animalsimulation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapCell {

    Comparator Comp = new EnergyComparator();
    public SortedSet<Animal> animals = new TreeSet<Animal>(Comp);
    public boolean plant;
    public Vector2D position;
    private WorldMap map;

    public MapCell(WorldMap map, Vector2D position, boolean plant) {
        this.map = map;
        this.position = position;
        this.plant = plant;
    }


    public MapCell copy(WorldMap newMap){
        MapCell mapcell = new MapCell(newMap, this.position, this.plant);

        for(Animal animal : this.animals){
            Animal newAnimal = new Animal(animal.startEnergy, this.position, newMap);
            mapcell.animals.add(newAnimal);
            newMap.animals.add(newAnimal);
        }

        return mapcell;
    }


    public boolean hasAnimals() {
        return !(animals.isEmpty());
    }

    public boolean hasPlant() {
        return plant;
    }

    public void plantEaten() {
        this.plant = false;
    }

    public void plantGrown() {
        this.plant = true;
    }

    public boolean possibleParents(int startEnergy) {             //funkcja sprawdzająca czy w danej komórce znajdują się osobniki, które mogą się rozmnożyć
        if (this.animals.size() < 2)
            return false;
        else if(this.animals.first().energy >= startEnergy/2) {
            Animal temp1 = this.animals.first();
            this.animals.remove(temp1);

            if (this.animals.first().energy >= startEnergy / 2) {
                this.animals.add(temp1);
                return true;
            }
            this.animals.add(temp1);
            return false;
        }
        return false;
    }

    public boolean isNeighbourEmpty(Vector2D position){                      //sprawdzam czy dane pole jest puste (nie ma na nim zwierzęcia, ani trawy)
        return !(this.map.cells.containsKey(position));
    }


    public Vector2D cellForChild(){                                                                     //funkcja znajdująca pole na narodziny dziecka
        //Vector2D result1 = new Vector2D(this.position.x%this.map.width, this.position.y%this.map.height);

        List<Vector2D> emptyNeighbours = new ArrayList<Vector2D>();
        List<Vector2D> allNeighbours = new ArrayList<Vector2D>();

        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if(i == 0 && j ==0)
                    continue;

                int tempX = this.position.x+j;
                int tempY = this.position.y+i;

                if(tempX < 0)
                    tempX += this.map.width;
                else
                    tempX %= this.map.width;
                if(tempY < 0)
                    tempY += this.map.height;
                else
                    tempY %= this.map.height;

                Vector2D temp = new Vector2D(tempX, tempY);
                if(isNeighbourEmpty(temp))
                    emptyNeighbours.add(temp);
                allNeighbours.add(temp);
            }
        }

        if(emptyNeighbours.size() == 0){
            return allNeighbours.get(ThreadLocalRandom.current().nextInt(0, 8));
        }
        return emptyNeighbours.get(ThreadLocalRandom.current().nextInt(0, emptyNeighbours.size()));
    }

    public Animal reproduce(){
        if(this.possibleParents(this.map.startEnergy)){
            Animal p1 = this.animals.first();
            animals.remove(p1);
            Animal p2 = this.animals.first();
            animals.remove(p2);
            Animal child = new Animal(p1, p2, map.day, p1.startEnergy, cellForChild(), this.map);

            p1.energy -= p1.energy/4;
            p2.energy -= p2.energy/4;

            p1.children.add(child);                              //dodawanie dzieci jako zwykłych potomków
            p2.children.add(child);

            this.animals.add(p1);                                        //ponowne umieszczanie rodziców w komórce
            this.animals.add(p2);

            this.animals.add(child);

            return child;                                           //funkcja zwraca dziecko, żeby dodać je do listy zwierząt w mapie
        }
        return null;
    }

    public int findNumberOfStrongest(){                                 //funkcja znajduje liczbę zwierząt, które są tak samo silne w danej komórce (maksymalna wartość energy)
        if(this.animals.size() == 0)
            return 0;

        int result = 0;
        int strongestEnergy = this.animals.first().energy;

        for(Animal animal : this.animals){
            if(animal.energy == strongestEnergy)
                result++;
            else
                break;
        }
        return result;
    }

    public Animal strongestAnimal(){
        if(!(this.animals.isEmpty())){
            return this.animals.first();
        }

        return null;
    }
}
