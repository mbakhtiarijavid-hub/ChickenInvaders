package com.chickeninvaders.entities;

public class FastEnemy extends Enemy {
    public FastEnemy(double x, double y, int row, int col) { super(x, y, row, col); }
    @Override public int scoreValue() { return 15; }
    @Override public String typeName() { return "Fast"; }
}
