package com.chickeninvaders.data;

public class User {
    private String username;
    private String password;
    private int highScore;
    private int lastLevel;
    private boolean musicOn;
    private boolean shotSoundOn;
    private boolean crashSoundOn;
    private boolean endSoundOn;
    private String selectedPlane;

    public User(String username, String password) {
        this(username, password, 0, 1, true, true, true, true, "Default");
    }

    public User(String username, String password, int highScore, int lastLevel,
                boolean musicOn, boolean shotSoundOn, boolean crashSoundOn, boolean endSoundOn,
                String selectedPlane) {
        this.username = username;
        this.password = password;
        this.highScore = highScore;
        this.lastLevel = lastLevel;
        this.musicOn = musicOn;
        this.shotSoundOn = shotSoundOn;
        this.crashSoundOn = crashSoundOn;
        this.endSoundOn = endSoundOn;
        this.selectedPlane = (selectedPlane == null || selectedPlane.isEmpty()) ? "Default" : selectedPlane;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public int getLastLevel() { return lastLevel; }
    public void setLastLevel(int lastLevel) { this.lastLevel = lastLevel; }
    public boolean isMusicOn() { return musicOn; }
    public void setMusicOn(boolean v) { musicOn = v; }
    public boolean isShotSoundOn() { return shotSoundOn; }
    public void setShotSoundOn(boolean v) { shotSoundOn = v; }
    public boolean isCrashSoundOn() { return crashSoundOn; }
    public void setCrashSoundOn(boolean v) { crashSoundOn = v; }
    public boolean isEndSoundOn() { return endSoundOn; }
    public void setEndSoundOn(boolean v) { endSoundOn = v; }
    public String getSelectedPlane() { return selectedPlane; }
    public void setSelectedPlane(String selectedPlane) { this.selectedPlane = selectedPlane; }
}
