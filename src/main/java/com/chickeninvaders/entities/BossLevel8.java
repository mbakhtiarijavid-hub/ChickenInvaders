package com.chickeninvaders.entities;

public class BossLevel8 extends Boss {
    private double vBase;
    private double vPhase = 0;
    private double accel = 0.02;

    public BossLevel8(double x, double y) {
        super(x, y, 170, 1.3);
        this.vBase = y;
        this.width = 200;
        this.height = 170;
    }

    @Override public int scoreValue() { return 1000; }
    @Override public long attackIntervalMs() { return 1000; }
    @Override public double eggSpeed() { return 3.2; }
    @Override public String name() { return "Final Boss"; }

    @Override public double[][] attackDirections() {
        double[][] dirs = new double[8][2];
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            dirs[i][0] = Math.cos(angle);
            dirs[i][1] = Math.sin(angle);
        }
        return dirs;
    }

    @Override
    public void updateMovement(int panelWidth) {
        hMoveSpeed += accel * direction * 0.02;
        hMoveSpeed = Math.max(0.8, Math.min(2.2, hMoveSpeed));
        x += direction * hMoveSpeed;
        if (x <= 20 || x + width >= panelWidth - 20) direction *= -1;
        vPhase += 0.025;
        y = vBase + Math.sin(vPhase) * 50;
    }
}
