package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {
    public static boolean saver = false;
    private static JFrame frame;
    private static JFrame framev;
    
    public static boolean displayLeaderboard(int highScore, String playerName) {
        framev = new JFrame("Leaderboard"); // Instantiate a new JFrame
        framev.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<String> leaderboardData = loadLeaderboardData("leaderboard.txt");

        String newEntry = playerName + ": " + highScore;

        boolean scoreAdded = false;

        if (leaderboardData.isEmpty() || leaderboardData.size() < 5) {
            leaderboardData.add(newEntry);
            scoreAdded = true;
        } else {
            int lowestScore = Integer.MAX_VALUE;
            int lowestScoreIndex = -1;

            for (int i = 0; i < leaderboardData.size(); i++) {
                int score = Integer.parseInt(leaderboardData.get(i).split(": ")[1]);
                if (score < lowestScore) {
                    lowestScore = score;
                    lowestScoreIndex = i;
                }
            }

            if (highScore > lowestScore) {
                leaderboardData.remove(lowestScoreIndex);
                leaderboardData.add(newEntry);
                scoreAdded = true;
            }
        }

        if (scoreAdded) {
        Collections.sort(leaderboardData, (s1, s2) -> {
            String[] parts1 = s1.split(": ");
            String[] parts2 = s2.split(": ");
            if (parts1.length < 2 || parts2.length < 2) {
                // Handle improperly formatted data
                return 0; // You can choose a default behavior or handle this case differently
            }

            int score1 = Integer.parseInt(parts1[1]);
            int score2 = Integer.parseInt(parts2[1]);
            return Integer.compare(score2, score1); // Descending order
        });

            DefaultListModel<String> leaderboardModel = new DefaultListModel<>();
            for (int i = 0; i < leaderboardData.size(); i++) {
                String entry = leaderboardData.get(i);
                String[] parts = entry.split(": ");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                String rank = Integer.toString(i + 1); // Ranking numbers

                // Use a custom renderer to format and style the leaderboard
                String formattedEntry = String.format("  %-2s %-15s %s", rank, name, score);
                leaderboardModel.addElement(formattedEntry);
            }

            JList<String> leaderboardList = new JList<>(leaderboardModel);

            // Use a monospaced font for consistent alignment
            leaderboardList.setFont(new Font("Monospaced", Font.BOLD, 16));
            leaderboardList.setForeground(Color.YELLOW);
            leaderboardList.setBackground(Color.BLACK);
            leaderboardList.setSelectionBackground(Color.RED);

            JScrollPane scrollPane = new JScrollPane(leaderboardList);
            framev.add(scrollPane);

            // Calculate the frame height based on the number of entries
            int numEntries = leaderboardModel.getSize();
            int frameHeight = Math.min(600, numEntries * 22 + 50); // Limit max height

            framev.setLocation(650, 320);
            ImageIcon licon = new ImageIcon("src/resources/images/licon.png");
            framev.setIconImage(licon.getImage());

            framev.setSize(300, frameHeight); // Set frame size

            framev.setVisible(true);

            // Close the frame when 'L' key is pressed
            JRootPane rootPane = SwingUtilities.getRootPane(framev);
            rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "closeFrame");
            rootPane.getActionMap().put("closeFrame", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    framev.dispose();
                }
            });

            saver = saveLeaderboardData(leaderboardData, "leaderboard.txt");
        } else {
            JOptionPane.showMessageDialog(null, "Sorry, your score is too low!");
        }

        return true;
    }


   private static List<String> loadLeaderboardData(String fileName) {
        List<String> leaderboardData = new ArrayList<>();
        try {
            File file = new File(fileName);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        leaderboardData.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return leaderboardData;
    }

    private static boolean saveLeaderboardData(List<String> leaderboardData, String fileName) {
        try {
            File file = new File(fileName);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String entry : leaderboardData) {
                    writer.write(entry);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
   public static void displayLeaderboard() {
    frame = new JFrame("Leaderboard");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    List<String> leaderboardData = loadLeaderboardData("leaderboard.txt");

    // Check and filter out improperly formatted entries
    leaderboardData = leaderboardData.stream()
            .filter(entry -> entry.matches(".+: \\d+"))
            .collect(Collectors.toList());

    // Sort the filtered data
    Collections.sort(leaderboardData, (s1, s2) -> Integer.compare(
            Integer.parseInt(s2.split(": ")[1]),
            Integer.parseInt(s1.split(": ")[1])
    ));

    DefaultListModel<String> leaderboardModel = new DefaultListModel<>();
    for (int i = 0; i < leaderboardData.size(); i++) {
        String entry = leaderboardData.get(i);
        String[] parts = entry.split(": ");
        String rank = Integer.toString(i + 1);
        leaderboardModel.addElement(String.format("  %-2s %-15s %s", rank, parts[0], parts[1]));
    }

    JList<String> leaderboardList = new JList<>(leaderboardModel);
    leaderboardList.setFont(new Font("Monospaced", Font.BOLD, 16));
    leaderboardList.setForeground(Color.YELLOW);
    leaderboardList.setBackground(Color.BLACK);
    leaderboardList.setSelectionBackground(Color.RED);

    frame.add(new JScrollPane(leaderboardList));
    int frameHeight = Math.min(600, leaderboardModel.getSize() * 22 + 50);
    frame.setSize(300, frameHeight);
    frame.setLocation(650, 320);
    frame.setIconImage(new ImageIcon("src/resources/images/licon.png").getImage());
    frame.setVisible(true);

    // Close the frame when 'L' key is pressed
    JRootPane rootPane = SwingUtilities.getRootPane(frame);
    rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), "closeFrame");
    rootPane.getActionMap().put("closeFrame", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    });

    saveLeaderboardData(leaderboardData, "leaderboard.txt");
}
}
