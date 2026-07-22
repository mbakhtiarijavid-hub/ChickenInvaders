package com.chickeninvaders.util;

import com.chickeninvaders.entities.Plane;
import com.chickeninvaders.entities.PowerUp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private static final Map<String, Image> cache = new HashMap<>();
    public static final String BASE = "resources";

    public static Image get(String relativePath) {
        if (cache.containsKey(relativePath)) return cache.get(relativePath);
        Image img = null;
        try {
            File f = new File(BASE, relativePath);
            if (f.exists() && f.length() > 0) {
                img = ImageIO.read(f);
            }
        } catch (Exception ignored) {
        }
        cache.put(relativePath, img);
        return img;
    }

    public static Image plane() { return plane(Plane.PlaneType.DEFAULT); }

    public static Image plane(Plane.PlaneType type) {
        return switch (type) {
            case DEFAULT -> get("airplan/1.png");
            case FAST -> get("airplan/2.png");
            case HEAVY -> get("airplan/3.png");
            case SNIPER -> get("airplan/4.png");
        };
    }

    public static Image bullet() { return get("airplan/shot.png"); }
    public static Image explosionSmall() { return get("airplan/Explosion.png"); }
    public static Image explosionBig() { return get("airplan/Explosion2.png"); }

    public static Image background(int levelIndexZeroBased) {
        return levelIndexZeroBased % 2 == 0 ? get("background/background.jpg") : get("background/background2.jpg");
    }

    public static Image menuBackground() { return get("background/background.jpg"); }

    public static Image loginBackground() { return get("background/background2.jpg"); }

    public static Image chicken(String typeName) {
        return switch (typeName) {
            case "Normal" -> get("chicken/normal_chicken.png");
            case "Fast" -> get("chicken/fast_chicken.png");
            case "Zigzag" -> get("chicken/zigzag_chicken.png");
            case "Shooter" -> get("chicken/shooter_chicken.png");
            default -> get("chicken/normal_chicken.png");
        };
    }

    public static Image egg() { return get("chicken/egg.png"); }
    public static Image boss1() { return get("chicken/boss1.png"); }
    public static Image boss2() { return get("chicken/boss2.png"); }

    public static Image powerUp(PowerUp.Type type) {
        return switch (type) {
            case ADD_SHOT -> get("powerup1/add_shot.png");
            case RAPID_FIRE -> get("powerup1/fast_shot.png");
            case EXTRA_LIFE -> get("powerup1/heal.png");
            case SHIELD -> get("powerup1/sheild.png");
            case FREEZE_BOMB -> get("powerup1/freeze.png");
        };
    }
}
