package com.chickeninvaders.entities;

public class Cell {
    public final int row, col;
    public int remainingSpawns;
    public Class<? extends Enemy> enemyClass;
    public Enemy currentEnemy;

    public Cell(int row, int col, int remainingSpawns, Class<? extends Enemy> enemyClass) {
        this.row = row;
        this.col = col;
        this.remainingSpawns = remainingSpawns;
        this.enemyClass = enemyClass;
    }

    public boolean isCleared() { return currentEnemy == null && remainingSpawns <= 0; }
}
