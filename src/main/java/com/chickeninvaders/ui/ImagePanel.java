package com.chickeninvaders.ui;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private final Image background;

    public ImagePanel(Image background) {
        this.background = background;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(new Color(10, 10, 40));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

    }
}
