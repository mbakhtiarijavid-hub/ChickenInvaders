package com.chickeninvaders.entities;

public class NormalEnemy extends Enemy {
    public NormalEnemy(double x, double y, int row, int col) { super(x, y, row, col); }
    @Override public int scoreValue() { return 10; }
    @Override public String typeName() { return "Normal"; }
}
