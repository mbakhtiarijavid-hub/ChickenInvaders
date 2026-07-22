package com.chickeninvaders.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;

public class SoundManager {

    public enum Category { MUSIC, SHOT, CRASH, END }

    private static final String BASE = "resources/sound-effects/";
    private static final String MAIN_THEME = BASE + "Chicken Invaders 2 Remastered OST - Main Theme.mp3";
    private static final String END_THEME = BASE + "Chicken Invaders 2 Remastered OST - Ending Theme.mp3";
    private static final String SHOT_SFX = BASE + "mixkit-short-laser-gun-shot-1670.wav";
    private static final String CRASH_SFX = BASE + "mixkit-epic-impact-afar-explosion-2782.wav";
    private static final String GAMEOVER_SFX = BASE + "mixkit-retro-arcade-game-over-470.wav";

    private boolean musicOn = true, shotOn = true, crashOn = true, endOn = true;
    private Clip musicClip;
    private Thread musicThread;
    private volatile boolean stopMusicFlag = false;

    private Boolean mp3Available;
    private Class<?> mp3PlayerClass;
    private java.lang.reflect.Constructor<?> mp3PlayerCtor;

    public void setEnabled(Category c, boolean value) {
        switch (c) {
            case MUSIC -> { musicOn = value; if (!value) stopMusic(); }
            case SHOT -> shotOn = value;
            case CRASH -> crashOn = value;
            case END -> endOn = value;
        }
    }

    public boolean isEnabled(Category c) {
        return switch (c) {
            case MUSIC -> musicOn;
            case SHOT -> shotOn;
            case CRASH -> crashOn;
            case END -> endOn;
        };
    }

    private Clip tryLoadClipDirect(String path) {
        try {
            File f = new File(path);
            if (!f.exists() || f.length() == 0) return null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private Clip loadClip(String path) {
        Clip clip = tryLoadClipDirect(path);
        if (clip != null) return clip;
        if (path.toLowerCase().endsWith(".mp3")) {
            String wavPath = path.substring(0, path.length() - 4) + ".wav";
            clip = tryLoadClipDirect(wavPath);
        }
        return clip;
    }

    private void playOneShot(String path, boolean allowed) {
        if (!allowed) return;
        Clip c = loadClip(path);
        if (c != null) {
            c.setFramePosition(0);
            c.start();
            return;
        }
        if (path.toLowerCase().endsWith(".mp3")) playMp3OneShot(path);
    }

    public void playShot() { playOneShot(SHOT_SFX, shotOn); }
    public void playCrash() { playOneShot(CRASH_SFX, crashOn); }
    public void playGameOver() { playOneShot(GAMEOVER_SFX, endOn); }
    public void playWin() { playOneShot(END_THEME, endOn); }

    public boolean isMusicPlaying() {
        if (musicClip != null && musicClip.isRunning()) return true;
        return musicThread != null && musicThread.isAlive();
    }

    public void ensureMusicPlaying() {
        if (musicOn && !isMusicPlaying()) startMusic();
    }

    public void startMusic() {
        if (!musicOn) return;
        stopMusic();
        musicClip = loadClip(MAIN_THEME);
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
            return;
        }
        startMp3Loop(MAIN_THEME);
    }

    public void stopMusic() {
        stopMusicFlag = true;
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    private boolean mp3SupportAvailable() {
        if (mp3Available == null) {
            try {
                mp3PlayerClass = Class.forName("javazoom.jl.player.Player");
                mp3PlayerCtor = mp3PlayerClass.getConstructor(java.io.InputStream.class);
                mp3Available = true;
            } catch (Throwable t) {
                mp3Available = false;
            }
        }
        return mp3Available;
    }

    private void startMp3Loop(String path) {
        if (!mp3SupportAvailable()) return;
        File f = new File(path);
        if (!f.exists() || f.length() == 0) return;
        stopMusicFlag = false;
        musicThread = new Thread(() -> {
            while (!stopMusicFlag && musicOn) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    Object player = mp3PlayerCtor.newInstance(fis);
                    mp3PlayerClass.getMethod("play").invoke(player);
                } catch (Throwable t) {
                    break;
                }
            }
        }, "bg-music-mp3");
        musicThread.setDaemon(true);
        musicThread.start();
    }

    private void playMp3OneShot(String path) {
        if (!mp3SupportAvailable()) return;
        File f = new File(path);
        if (!f.exists() || f.length() == 0) return;
        Thread t = new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(f)) {
                Object player = mp3PlayerCtor.newInstance(fis);
                mp3PlayerClass.getMethod("play").invoke(player);
            } catch (Throwable ignored) {
            }
        }, "sfx-mp3");
        t.setDaemon(true);
        t.start();
    }
}
