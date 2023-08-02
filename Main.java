import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JFrame {
    private final List<MyRectangle> arrayOfRectangles = new ArrayList<>();
    private final JLabel bottomLabel;
    private double numberOfRectangles = 0;
    private double numberOfKilledRectangles = 0;
    private double result = 0;
    private AnimationThread animationThread;
    private RectangleGeneratorThread rectangleGeneratorThread;
    private TimerToGame timerToGame;
    private final JPanel centerPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
    public Main() {
        setLayout(new BorderLayout());
        //gorny panel
        centerPanel = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (arrayOfRectangles) {
                    for (MyRectangle x : arrayOfRectangles) {
                        if(x.isVisibility()) {
                            g.setColor(Color.BLUE);
                            g.fillRect(x.getX(), x.getY(), x.getWidth(), x.getHeight());
                        }
                    }
                }
            }
        };
        //efekty dodatkowe gornego panelu
        centerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        centerPanel.setBackground(Color.ORANGE);
        add(centerPanel, BorderLayout.CENTER);
        //watek animacji przesuwania tego kwadratu na ekranie
        animationThread = new AnimationThread();
        animationThread.start();
        //watek generowania kwadratow
        rectangleGeneratorThread = new RectangleGeneratorThread();
        rectangleGeneratorThread.start();
        //watek liczenia czasu gry
        timerToGame = new TimerToGame();
        timerToGame.start();

        //dzialanie myszy
        centerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for(MyRectangle myRectangle : arrayOfRectangles){
                    int recX = myRectangle.getX();
                    int recY = myRectangle.getY();
                    if(e.getX() >= recX && e.getX() <= recX + 30 && e.getY() >= recY && e.getY() <= recY + 30 && myRectangle.isVisibility()){
                        ++numberOfKilledRectangles;
                        myRectangle.setVisibility(false); //ustawiam widocznosc na false
                    }
                    result = Math.round((numberOfKilledRectangles/numberOfRectangles) * 100);
                    bottomLabel.setText("Current score: " + result + "%");
                }
            }
        });

        //dolny label
        bottomLabel = new JLabel("Current score: " + (result) + "%");
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Score");
        bottomLabel.setBorder(titledBorder);
        add(bottomLabel, BorderLayout.SOUTH);
        bottomLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //cechy okienka
        setTitle("Projekt02");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1020, 840));
        pack();
        setVisible(true);
    }
    class AnimationThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (arrayOfRectangles) {
                    for (MyRectangle x : arrayOfRectangles) {
                        x.setY(x.getY() + 1); //przesuwa
                        if (x.getY() + x.getHeight() >= getHeight()) {
                            x.setY(0); //ustawia kwadrat na gore
                        }
                    }
                }
                repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    class RectangleGeneratorThread extends Thread {
        @Override
        public void run() {
            while (true) {
                int panelWidth = getWidth();
                int randomX;

                if (panelWidth > 30) {
                    Random random = new Random();
                    randomX = random.nextInt(panelWidth - 30);
                } else {
                    randomX = 0;
                }

                MyRectangle myRectangle = new MyRectangle(randomX, 0, 30, 30);
                synchronized (arrayOfRectangles) {
                    arrayOfRectangles.add(myRectangle);
                    numberOfRectangles++;
                    result = Math.round((numberOfKilledRectangles/numberOfRectangles) * 100);
                }
                repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    class TimerToGame extends Thread{
        @Override
        public void run() {
            int timer = 0;
            while(timer <= 60){
                timer++;
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException exception){
                    exception.printStackTrace();
                }
            }
            System.out.println("KONIEC");
            animationThread.interrupt();
            rectangleGeneratorThread.interrupt();
            String message;
            if(result > 50){
                message = "You won!";
            }else{
                message = "You lost!";
            }
            JOptionPane.showMessageDialog(centerPanel, message);
        }
    }
    class MyRectangle {
        private int x;
        private int y;
        private int width;
        private int height;
        boolean visibility;
        public MyRectangle(int x,int y,int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            visibility = true;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setY(int y) {
            this.y = y;
        }

        public boolean isVisibility() {
            return visibility;
        }

        public void setVisibility(boolean visibility) {
            this.visibility = visibility;
        }
    }
}