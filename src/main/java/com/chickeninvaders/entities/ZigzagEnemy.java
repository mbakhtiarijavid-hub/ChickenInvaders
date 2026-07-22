package com.chickeninvaders.entities;

public class ZigzagEnemy extends Enemy {
    public ZigzagEnemy(double x, double y, int row, int col) { super(x, y, row, col); }
    @Override public int scoreValue() { return 20; }
    @Override public String typeName() { return "Zigzag"; }
    @Override public double extraOffsetX() {
        zigzagPhase += 0.12;
        return Math.sin(zigzagPhase) * 18;
    }
}
