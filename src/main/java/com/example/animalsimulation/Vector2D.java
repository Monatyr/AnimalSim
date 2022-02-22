package com.example.animalsimulation;

import java.util.Objects;

public class Vector2D {

    public int x;
    public int y;

    public Vector2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Vector2D))
            return false;
        Vector2D that = (Vector2D) other;

        return(this.x == that.x && this.y == that.y);
    }

    public void addToVector(Vector2D other){
        this.x += other.x;
        this.y += other.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.x, this.y);
    }
}
