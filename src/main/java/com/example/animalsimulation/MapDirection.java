package com.example.animalsimulation;

public enum MapDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    NORTHEAST,
    SOUTHEAST,
    SOUTHWEST,
    NORTHWEST;

    public String toString() {
        switch(this){
            case NORTH:
                return "Północ";
        }
        return null;
    }

    public MapDirection next(){         //po wylosowaniu obrotu i ze zbioru (0-7) należy obrócić zwierzę o 45 stopni w prawo i razy
        switch(this){
            case NORTH:
                return NORTHEAST;
            case NORTHEAST:
                return EAST;
            case EAST:
                return SOUTHEAST;
            case SOUTHEAST:
                return SOUTH;
            case SOUTH:
                return SOUTHWEST;
            case SOUTHWEST:
                return WEST;
            case WEST:
                return NORTHWEST;
            case NORTHWEST:
                return NORTH;
        }

        return null;
    }


    public Vector2D toUnitVector(){

        switch(this){
            case NORTH:
                Vector2D vector1 = new Vector2D(0,1);
                return vector1;
            case NORTHEAST:
                Vector2D vector2 = new Vector2D(1,1);
                return vector2;
            case EAST:
                Vector2D vector3 = new Vector2D(1,0);
                return vector3;
            case SOUTHEAST:
                Vector2D vector4 = new Vector2D(1,-1);
                return vector4;
            case SOUTH:
                Vector2D vector5 = new Vector2D(0,-1);
                return vector5;
            case SOUTHWEST:
                Vector2D vector6 = new Vector2D(-1,-1);
                return vector6;
            case WEST:
                Vector2D vector7 = new Vector2D(-1,0);
                return vector7;
            case NORTHWEST:
                Vector2D vector8 = new Vector2D(-1,1);
                return vector8;
        }

        return null;
    }
}
