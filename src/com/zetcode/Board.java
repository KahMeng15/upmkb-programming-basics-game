package com.zetcode;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Board extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);
    private final Font smallerFont = new Font("Helvetica", Font.PLAIN, 12);
    
    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying1 = false;
    private boolean dying2 = false;
    
    private boolean music = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    
    private int N_GHOSTS = 6;
    private int pacsLeft1, pacsLeft2, score1, score2;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image ghost;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private Image bpacman1, bpacman2up, bpacman2left, bpacman2right, bpacman2down;
    private Image bpacman3up, bpacman3down, bpacman3left, bpacman3right;
    private Image bpacman4up, bpacman4down, bpacman4left, bpacman4right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;
    
    private int highScore = 0;
    private boolean youDiedScreen = false;
    
    private boolean multiplayer;
    private boolean isPlayerOneAlive;
    private boolean isPlayerTwoAlive;
    
    
    //pacman2
    private int pacman2_x, pacman2_y, pacmand2_x, pacmand2_y;
    private int req_dx2, req_dy2, view_dx2, view_dy2;
    private int pacAnimCount2 = PAC_ANIM_DELAY;
    private int pacAnimDir2 = 1;
    private int pacmanAnimPos2 = 0;
    
    //music
    private Clip backgroundMusicClip;


    private final short levelData[] = {
            19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
            25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
            1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Board() {

        loadImages();
        initVariables();
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void doAnim() {

        pacAnimCount--;
        pacAnimCount2--;

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
        
        if (pacAnimCount2 <= 0) { 
        pacAnimCount2 = PAC_ANIM_DELAY;
        pacmanAnimPos2 = pacmanAnimPos2 + pacAnimDir2;

        if (pacmanAnimPos2 == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos2 == 0) {
            pacAnimDir2 = -pacAnimDir2;
            }
        }
    }

    private void playGame(Graphics2D g2d) {

        if (dying1) {

            death1();

        } else if (dying2) {
            
            death2();
        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 0,0));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        //g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String line1 = "Press 1 for Singleplayer";
        String line2 = "Press 2 for Multiplayer"; // Second line of text
        

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        String p1 = "Player 1: Use the arrow keys";
        String p2 = "Player 2: Use the WASD Keys"; // Second line of text
        
        {
        g2d.setColor(new Color(0, 0, 200));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 90, SCREEN_SIZE - 100, 60);
        String title = "Pacman";
        Font mtitle = new Font("Helvetica", Font.BOLD, 40);
        FontMetrics title2 = this.getFontMetrics(mtitle);
        g2d.setColor(Color.yellow);
        g2d.setFont(mtitle);
        int x1 = (SCREEN_SIZE - title2.stringWidth(title)) / 2;
        int y = SCREEN_SIZE / 2;
        g2d.drawString(title, x1, y - 45); // Adjust 'y' position as needed
        }
        
        {
        g2d.setColor(Color.green);
        g2d.setFont(small);
        int x1 = (SCREEN_SIZE - metr.stringWidth(line1)) / 2;
        int y = SCREEN_SIZE / 2;
        g2d.drawString(line1, x1, y - 10); // Adjust 'y' position as needed
        }
        {
        g2d.setColor(Color.cyan);
        g2d.setFont(small);
        int x2 = (SCREEN_SIZE - metr.stringWidth(line2)) / 2;
        int y = SCREEN_SIZE / 2;
        g2d.drawString(line2, x2, y + 10); // Adjust 'y' position as needed
        } 
        
        {
        g2d.setColor(new Color(64, 0, 0));
        g2d.fillRect(50, SCREEN_SIZE / 2 + 20, SCREEN_SIZE - 100, 50);
            
        Font control = new Font("Helvetica", Font.ITALIC, 10);
        FontMetrics control2 = this.getFontMetrics(control);
            
        g2d.setColor(Color.white);
        g2d.setFont(control);
        int x3 = (SCREEN_SIZE - control2.stringWidth(p1)) / 2;
        int x4 = (SCREEN_SIZE - control2.stringWidth(p2)) / 2;
        int y = SCREEN_SIZE / 2;
   
        g2d.drawString(p1, x3, y + 43); // Adjust 'y' position as needed
        g2d.drawString(p2, x4, y + 53); // Adjust 'y' position as needed
        
        }
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;
        String s2;

        g.setFont(smallerFont);
        g.setColor(new Color(96, 128, 255));
        ////
        
        // Display the remaining lives for Pacman 1 and Pacman 2
        //s = "Pacman 1 Lives: " + pacsLeft1;
        //g.drawString(s, 20, SCREEN_SIZE + 16);

        //s = "Pacman 2 Lives: " + pacsLeft2;
        //g.drawString(s, SCREEN_SIZE / 2 + 20, SCREEN_SIZE + 16);

        //s = "Score: " + score;
        //g.drawString(s, SCREEN_SIZE / 2 + 0, SCREEN_SIZE + 16);
    
        for (i = 0; i < pacsLeft1; i++) {
            g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 5, this);
        }
        if (multiplayer) {
            for (i = 0; i < pacsLeft2; i++) {
                g.drawImage(pacman3left, i * 28 + SCREEN_SIZE / 2 + 100, SCREEN_SIZE + 5, this);
            }
            s2 = "P2 Score: " + score2;
            g.drawString(s2, SCREEN_SIZE / 2 -80, SCREEN_SIZE + 27);
            
        }
        
        ////score text
        s = "P1 Score: " + score1;
        g.drawString(s, SCREEN_SIZE / 2 - 80, SCREEN_SIZE + 13);
        
        //pacman lives
        //for (i = 0; i < pacsLeft1; i++) {
        //    g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
        //}
    }
    
     private void drawHighScore(Graphics2D g) {
        ///highscore text
        g.setFont(smallerFont);
        g.setColor(new Color(255, 255, 0));
        String highScoreText = "High Score: " + highScore;
        g.drawString(highScoreText, SCREEN_SIZE / 2 + 7, SCREEN_SIZE + 13);
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

         if (score1 > highScore) {
            highScore = score1;
        }
        
        if (finished) {

            int max = 50;
            int total = score1 + score2;
            
            max += total;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    
    private void death1 () {
        if (inGame) {
            if (dying1) {
                pacsLeft1--;
                System.out.println("pacman1 lives :" + pacsLeft1);
                respawnPacman1();
            } else if (dying2) {
                pacsLeft2--;
                System.out.println("pacman2 lives :" + pacsLeft2);
                respawnPacman2();
            }
            
            if (multiplayer){
                System.out.println("death true multiplayer triggered");
                if (pacsLeft1 == 0 && pacsLeft2 == 0) {
                    inGame = false;
                    youDiedScreen = true;
                    backgroundMusicClip.stop();
                }
            } else {
                System.out.println("death false multiplayer triggered");
                if (pacsLeft1 == 0) {
                    System.out.println("death false multiplayer 0 triggered");
                    inGame = false;
                    youDiedScreen = true;
                    backgroundMusicClip.stop();
                    }
                }
            }
            
            if (pacsLeft1 == 0) {
                pacman_x = -1000;
                pacman_y = -1000;
            }
            
            if (pacsLeft2 == 0) {
                pacman2_x = -1000;
                pacman2_y = -1000;
            }
        
            //continueLevel();
        }
    
        private void death2 () {
        if (inGame) {
            if (dying1) {
                pacsLeft1--;
                System.out.println("pacman1 lives :" + pacsLeft1);
                respawnPacman1();
            } else if (dying2) {
                pacsLeft2--;
                System.out.println("pacman2 lives :" + pacsLeft2);
                respawnPacman2();
            }
            
            if (multiplayer){
                System.out.println("death true multiplayer triggered");
                if (pacsLeft1 == 0 && pacsLeft2 == 0) {
                    inGame = false;
                    youDiedScreen = true;
                    backgroundMusicClip.stop();
                }
            } else {
                System.out.println("death false multiplayer triggered");
                if (pacsLeft1 == 0) {
                    System.out.println("death false multiplayer 0 triggered");
                    inGame = false;
                    youDiedScreen = true;
                    backgroundMusicClip.stop();
                    }
                }
            }
        
            if (pacsLeft1 == 0) {
                pacman_x = -1000;
                pacman_y = -1000;
            }
            
            if (pacsLeft2 == 0) {
                pacman2_x = -1000;
                pacman2_y = -1000;
            }
        
            //continueLevel();
        }

    private void moveGhosts(Graphics2D g2d) {

        short i;
        int pos;
        int count;

        for (i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            int death1 = 0;
            int death2 = 0;
            
            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {
                
                dying1 = true;
            }
            
            if (pacman2_x > (ghost_x[i] - 12) && pacman2_x < (ghost_x[i] + 12)
                && pacman2_y > (ghost_y[i] - 12) && pacman2_y < (ghost_y[i] + 12)
                && inGame) {
             
                dying2 = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(ghost, x, y, this);
    }

    private void movePacman() {

        int pos;
        short ch;
        int pos2;
        short ch2;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score1++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        // Second Pacman movement
        if (pacman2_x % BLOCK_SIZE == 0 && pacman2_y % BLOCK_SIZE == 0) {
            pos2 = pacman2_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman2_y / BLOCK_SIZE);
            ch2 = screenData[pos2];

            if ((ch2 & 16) != 0) {
                screenData[pos2] = (short) (ch2 & 15);
                score2++;
            }
            
            if (req_dx2 == -pacmand2_x && req_dy2 == -pacmand2_y) {
                pacmand2_x = req_dx2;
                pacmand2_y = req_dy2;
                view_dx2 = pacmand2_x;
                view_dy2 = pacmand2_y;
            }

            if (req_dx2 != 0 || req_dy2 != 0) {
                if (!((req_dx2 == -1 && req_dy2 == 0 && (ch2 & 1) != 0)
                        || (req_dx2 == 1 && req_dy2 == 0 && (ch2 & 4) != 0)
                        || (req_dx2 == 0 && req_dy2 == -1 && (ch2 & 2) != 0)
                        || (req_dx2 == 0 && req_dy2 == 1 && (ch2 & 8) != 0))) {
                    pacmand2_x = req_dx2;
                    pacmand2_y = req_dy2;
                }
            }

            // Check for standstill
            if ((pacmand2_x == -1 && pacmand2_y == 0 && (ch2 & 1) != 0)
                    || (pacmand2_x == 1 && pacmand2_y == 0 && (ch2 & 4) != 0)
                    || (pacmand2_x == 0 && pacmand2_y == -1 && (ch2 & 2) != 0)
                    || (pacmand2_x == 0 && pacmand2_y == 1 && (ch2 & 8) != 0)) {
                pacmand2_x = 0;
                pacmand2_y = 0;
            }
        }

        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
        pacman2_x = pacman2_x + PACMAN_SPEED * pacmand2_x;
        pacman2_y = pacman2_y + PACMAN_SPEED * pacmand2_y;
    }

    private void drawPacman(Graphics2D g2d) {
        if (view_dx == -1) {
            drawPacnanLeft(g2d);
        } else if (view_dx == 1) {
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }    
         
        if (view_dx2 == -1) {
            drawPacnanLeft(g2d, pacman2_x, pacman2_y);
        } else if (view_dx2 == 1) {
            drawPacmanRight(g2d, pacman2_x, pacman2_y);
        } else if (view_dy2 == -1) {
            drawPacmanUp(g2d, pacman2_x, pacman2_y);
        } else {
            drawPacmanDown(g2d, pacman2_x, pacman2_y);
        }
    }

    ///pacman2 drawing
   private void drawPacmanUp(Graphics2D g2d, int x, int y) {
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(bpacman2up, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 2:
                g2d.drawImage(bpacman3up, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 3:
                g2d.drawImage(bpacman4up, pacman2_x + 1, pacman2_y + 1, this);
                break;
            default:
                g2d.drawImage(bpacman1, pacman2_x + 1, pacman2_y + 1, this);
                break;
        }
   } 
   
   private void drawPacmanDown(Graphics2D g2d, int x, int y) {
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(bpacman2down, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 2:
                g2d.drawImage(bpacman3down, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 3:
                g2d.drawImage(bpacman4down, pacman2_x + 1, pacman2_y + 1, this);
                break;
            default:
                g2d.drawImage(bpacman1, pacman2_x + 1, pacman2_y + 1, this);
                break;
        }
   }
   
   private void drawPacnanLeft(Graphics2D g2d, int x, int y) {
       
       switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(bpacman2left, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 2:
                g2d.drawImage(bpacman3left, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 3:
                g2d.drawImage(bpacman4left, pacman2_x + 1, pacman2_y + 1, this);
                break;
            default:
                g2d.drawImage(bpacman1, pacman2_x + 1, pacman2_y + 1, this);
                break;
        }
   }
   
   private void drawPacmanRight(Graphics2D g2d, int x, int y) {
      
        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(bpacman2right, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 2:
                g2d.drawImage(bpacman3right, pacman2_x + 1, pacman2_y + 1, this);
                break;
            case 3:
                g2d.drawImage(bpacman4right, pacman2_x + 1, pacman2_y + 1, this);
                break;
            default:
                g2d.drawImage(bpacman1, pacman2_x + 1, pacman2_y + 1, this);
                break;
        } 
   }
   
    
    ///pacman1 drawing
    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        pacsLeft1 = 3;
        pacsLeft2 = 3;
        score1 = 0;
        score2 = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        startLevel();
    }

private void displayYouDiedScreen(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setColor(new Color(0, 32, 48));
    g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
    g2d.setColor(Color.white);
    g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

    String youDiedText = "You died! Your score: " + score1;
    String restartGame = "Press S to play again";
    Font small = new Font("Helvetica", Font.BOLD, 14);
    FontMetrics metr = this.getFontMetrics(small);

    g2d.setColor(Color.white);
    g2d.setFont(small);
    g2d.drawString(youDiedText, (SCREEN_SIZE - metr.stringWidth(youDiedText)) / 2, SCREEN_SIZE / 2 - 10);
    g2d.drawString(restartGame, (SCREEN_SIZE - metr.stringWidth(restartGame)) / 2, SCREEN_SIZE / 2 + 10);
}    
    
    private void startLevel() {

        short i;
        int dx = 1;
        int random;
        
        for (i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }
        
        if (multiplayer == false){
            pacman_x = 7 * BLOCK_SIZE;
            pacman_y = 11 * BLOCK_SIZE;
        } else if (multiplayer == true){
            pacman_x = 8 * BLOCK_SIZE;
            pacman_y = 11 * BLOCK_SIZE;
        }
            pacmand_x = 0;
            pacmand_y = 0;
            req_dx = 0;
            req_dy = 0;
            view_dx = -1;
            view_dy = 0;
        
        if (multiplayer == true){
            pacman2_x = 6 * BLOCK_SIZE;
            pacman2_y = 11 * BLOCK_SIZE;
            pacmand2_x = 0;
            pacmand2_y = 0;
            req_dx2 = 0;
            req_dy2 = 0;
            view_dx2 = -1;
            view_dy2 = 0;
        } else if (multiplayer == false){
            pacman2_x = -1000;
            pacman2_y = -1000;
        }
        dying1 = false;
        dying2 = false; 
        
        
    }
    
    private void playMusic(){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/audio/bgmusic.wav"));
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);
            backgroundMusicClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void respawnPacman1(){
        if (multiplayer == false){
            pacman_x = 7 * BLOCK_SIZE;
            pacman_y = 11 * BLOCK_SIZE;
        } else if (multiplayer == true){
            pacman_x = 8 * BLOCK_SIZE;
            pacman_y = 11 * BLOCK_SIZE;
        }
            pacmand_x = 0;
            pacmand_y = 0;
            req_dx = 0;
            req_dy = 0;
            view_dx = -1;
            view_dy = 0;
            
            dying1 = false;
    }
    
    private void respawnPacman2(){
        if (multiplayer == true){
            pacman2_x = 6 * BLOCK_SIZE;
            pacman2_y = 11 * BLOCK_SIZE;
            pacmand2_x = 0;
            pacmand2_y = 0;
            req_dx2 = 0;
            req_dy2 = 0;
            view_dx2 = -1;
            view_dy2 = 0;
        } else if (multiplayer == false){
            pacman2_x = -1000;
            pacman2_y = -1000;
        }
        dying2 = false;
    }
    
    private void loadImages() {

        ghost = new ImageIcon("src/resources/images/ghost.png").getImage();
        pacman1 = new ImageIcon("src/resources/images/pacman.png").getImage();
        pacman2up = new ImageIcon("src/resources/images/up1.png").getImage();
        pacman3up = new ImageIcon("src/resources/images/up2.png").getImage();
        pacman4up = new ImageIcon("src/resources/images/up3.png").getImage();
        pacman2down = new ImageIcon("src/resources/images/down1.png").getImage();
        pacman3down = new ImageIcon("src/resources/images/down2.png").getImage();
        pacman4down = new ImageIcon("src/resources/images/down3.png").getImage();
        pacman2left = new ImageIcon("src/resources/images/left1.png").getImage();
        pacman3left = new ImageIcon("src/resources/images/left2.png").getImage();
        pacman4left = new ImageIcon("src/resources/images/left3.png").getImage();
        pacman2right = new ImageIcon("src/resources/images/right1.png").getImage();
        pacman3right = new ImageIcon("src/resources/images/right2.png").getImage();
        pacman4right = new ImageIcon("src/resources/images/right3.png").getImage();
        bpacman1 = new ImageIcon("src/resources/images/2pacman.png").getImage();
        bpacman2up = new ImageIcon("src/resources/images/2up1.png").getImage();
        bpacman3up = new ImageIcon("src/resources/images/2up2.png").getImage();
        bpacman4up = new ImageIcon("src/resources/images/2up3.png").getImage();
        bpacman2down = new ImageIcon("src/resources/images/2down1.png").getImage();
        bpacman3down = new ImageIcon("src/resources/images/2down2.png").getImage();
        bpacman4down = new ImageIcon("src/resources/images/2down3.png").getImage();
        bpacman2left = new ImageIcon("src/resources/images/2left1.png").getImage();
        bpacman3left = new ImageIcon("src/resources/images/2left2.png").getImage();
        bpacman4left = new ImageIcon("src/resources/images/2left3.png").getImage();
        bpacman2right = new ImageIcon("src/resources/images/2right1.png").getImage();
        bpacman3right = new ImageIcon("src/resources/images/2right2.png").getImage();
        bpacman4right = new ImageIcon("src/resources/images/2right3.png").getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
        
        if (youDiedScreen) {
        displayYouDiedScreen(g);
        }
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();
        drawHighScore(g2d);
        
         
        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        // Draw both Pacmans
        g2d.drawImage(ii, 5, 5, this);
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (inGame) {
            
        if (multiplayer == false){
            if (key == KeyEvent.VK_LEFT) {
                req_dx = -1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                req_dx = 1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_UP) {
                req_dx = 0;
                req_dy = -1;
            } else if (key == KeyEvent.VK_DOWN) {
                req_dx = 0;
                req_dy = 1;
            } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                inGame = false;
                backgroundMusicClip.stop();
            }
            
        } else if (multiplayer == true){
                System.out.println("wasd working");
                if (key == KeyEvent.VK_LEFT && pacsLeft1 != 0) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT && pacsLeft1 != 0) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP && pacsLeft1 != 0) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN && pacsLeft1 != 0) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                    backgroundMusicClip.stop();
                   
                } else if (key == KeyEvent.VK_A && pacsLeft2 != 0) {  // Second Pacman - Move left
                    req_dx2 = -1;
                    req_dy2 = 0;
                } else if (key == KeyEvent.VK_D && pacsLeft2 != 0) {  // Second Pacman - Move right
                    req_dx2 = 1;
                    req_dy2 = 0;
                } else if (key == KeyEvent.VK_W && pacsLeft2 != 0) {  // Second Pacman - Move up
                    req_dx2 = 0;
                    req_dy2 = -1;
                } else if (key == KeyEvent.VK_S&& pacsLeft2 != 0) {  // Second Pacman - Move down
                    req_dx2 = 0;
                    req_dy2 = 1;
                }
        }
        
            
            
            
        
        } else {
                if (key == 's' || key == 'S') {
                    inGame = true;
                    initGame();
                }
                if (key == '1') {
                    multiplayer = false;
                    isPlayerOneAlive = true;
                    inGame = true;
                    initGame();
                    playMusic();
                    System.out.println("Multiplayer set to false: " + multiplayer);
                }
                if (key == '2') {
                    multiplayer = true;
                    isPlayerOneAlive = true;
                    isPlayerTwoAlive = true;
                    inGame = true;
                    initGame();
                    playMusic();
                    System.out.println("Multiplayer set to true: " + multiplayer);
                }
        
        }
    }
}

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
}
