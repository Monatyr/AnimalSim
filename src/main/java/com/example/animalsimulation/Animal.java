package com.example.animalsimulation;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Animal{

    private Vector2D position;
    public MapDirection dir = MapDirection.NORTH;
    public int startEnergy;
    public int energy;
    public int genePool[] = new int[32];
    public int birthDay = 0;
    public int deathDay = -1;
    public Animal parent1 = null;
    public Animal parent2 = null;
    private WorldMap map;
    public List<Animal> children = new ArrayList<Animal>();
    public HashSet<Animal> descendants = new HashSet<>();
    //public boolean tracker = false;


    public Animal(int startEnergy, Vector2D pos, WorldMap map){                             //konstruktor dla pierwszych zwierząt, tzn tych bez rodziców
        this.position = pos;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.map = map;
        this.randomGenePool();
    }

    public Animal(Animal p1, Animal p2, int day, int startEnergy, Vector2D pos, WorldMap map){         //kontruktor dla zwierząt powstałych w wyniku rozmnażania
        this.parent1 = p1;
        this.parent2 = p2;
        this.energy = p1.energy/4 + p2.energy/4;
        this.birthDay = day;
        this.startEnergy = startEnergy;
        this.position = pos;
        this.map = map;
        generateGenePool();
    }

    public Vector2D getPostition(){
        return this.position;
    }


    public void changeAngle(){                                              //zmiana kierunku na podstawie genotypu

        int rand = ThreadLocalRandom.current().nextInt(0, 32);
        int numOfTurns = this.genePool[rand];

        for(int i = 0; i < numOfTurns; i++){
            this.dir = this.dir.next();
        }
    }

    public Vector2D move(){                                                      //zmiana pozycji zwierzęcia w kierunku, w którym jest zwrócone

        changeAngle();
        this.position.addToVector(this.dir.toUnitVector());

        if(this.position.x < 0){
            this.position.x += this.map.width;
        }
        else {
            this.position.x %= this.map.width;
        }

        if(this.position.y < 0) {
            this.position.y += this.map.height;
        }
        else{
            this.position.y %= this.map.height;
        }
        this.energy--;
        return (this.position);                                                    //funkcja zwraca nową pozycję zwierzęcia
    }

    public void eatPlant(int energyBonus){         //funkcja zwiększająca energię zwierzęcia po zjedzeniu rośliny oraz usuwająca roślinę
        this.energy += energyBonus;
    }


    public void checkGenePool(){                //funkcja sprawdzająca czy pula genów zawiera wszystkie możliwe obroty, a jeśli nie, to ją o nie uzupełnia

        int tab[] = new int[8];

        for(int el : this.genePool)
            tab[el]++;

        for(int i = 0; i < 8; i++){
            while(tab[i] == 0){
                int i1 = ThreadLocalRandom.current().nextInt(0,8);
                if(tab[i1] > 1){
                    this.genePool[i1] = i;
                    tab[i]++;
                    tab[i1]--;
                }
            }
        }
        Arrays.sort(this.genePool);
    }


    public void generateGenePool(){             //funkcja mieszająca geny obydwu rodziców

        int i1 = ThreadLocalRandom.current().nextInt(1,32);
        int i2 = ThreadLocalRandom.current().nextInt(1,32);

        while(Math.abs(i1-i2) == 0){
            i2 = ThreadLocalRandom.current().nextInt(1,32);
        }

        if(i2<i1){
            int temp = i1;
            i1 = i2;
            i2 = temp;
        }

        for(int i = 0; i < i1; i++){
            this.genePool[i] = this.parent1.genePool[i];
        }
        for(int i = i1; i < i2; i++){
            this.genePool[i] = this.parent2.genePool[i];
        }
        for(int i = i2; i < 32; i++){
            this.genePool[i] = this.parent1.genePool[i];
        }

        checkGenePool();
    }

    public boolean isAlive(){
        return (this.energy > 0);
    }


    public void randomGenePool(){
        for(int i = 0; i < 32; i++){
            this.genePool[i] = ThreadLocalRandom.current().nextInt(0,8);
        }

        checkGenePool();
        Arrays.sort(this.genePool);
    }

    public String genepoolToString(){
        String result = "";

        for(Integer i : this.genePool){
            result+=i.toString();
        }
        return result;
    }

    public Color getColor(){
        if(this.energy >= startEnergy)
            return Color.BROWN;
        else if(this.energy >= 3.0*startEnergy/4.0)
            return Color.CHOCOLATE;
        else if(this.energy >= startEnergy/2.0)
            return Color.CORAL;
        else if(this.energy >= startEnergy/4.0)
            return Color.BURLYWOOD;
        else
            return Color.BLANCHEDALMOND;
    }

    public void findDescendants(Animal greatParent){

        if(this.children.size() == 0)
            return;

        for(Animal animal : this.children){
            if(!(greatParent.descendants.contains(animal))) {
                greatParent.descendants.add(animal);
                animal.findDescendants(greatParent);
            }
        }
    }
}
