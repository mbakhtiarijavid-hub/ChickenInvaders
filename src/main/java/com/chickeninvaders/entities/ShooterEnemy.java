package com.chickeninvaders.entities;

public class ShooterEnemy extends Enemy {
    public ShooterEnemy(double x, double y, int row, int col) { super(x, y, row, col); }
    @Override public int scoreValue() { return 25; }
    @Override public String typeName() { return "Shooter"; }
    @Override public boolean canShootAtPlane() { return true; }
}
