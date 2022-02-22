package com.example.animalsimulation;

import java.util.Comparator;

public class EnergyComparator implements Comparator<Animal> {

    @Override
    public int compare(Animal o1, Animal o2) {

        if(o1.equals(o2))
            return 0;
        if(o1.energy < o2.energy)
            return -1;
        return 1;
    }
}