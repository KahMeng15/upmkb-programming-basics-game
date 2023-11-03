package com.zetcode;

import java.awt.EventQueue;
import javax.swing.*;

public class Pacman extends JFrame {

    public Pacman() {

        initUI();
    }

    private void initUI() {

        add(new Board());
        
        ImageIcon gicon = new ImageIcon("src/resources/images/gicon.png");
        
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(382, 430);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(gicon.getImage());
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new Pacman();
            ex.setVisible(true);
        });
    }
}
