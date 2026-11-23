import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Reaction Time Tester - latihan eksplorasi GUI Java Swing.
 *
 * 5 komponen utama:
 *  1. Top-level container   : JFrame
 *  2. Intermediate container: JPanel, JScrollPane
 *  3. Atomic component      : JLabel, JButton, JList
 *  4. Layout classes        : CardLayout, BorderLayout, GridLayout, FlowLayout
 *  5. Event listener        : MouseAdapter, KeyAdapter, WindowAdapter (override method),
 *                             ActionListener (interface)
 */
public class ReactionTimeApp extends JFrame {   // (1) Top-level container

    // Nama kartu untuk CardLayout
    private static final String CARD_MENU = "MENU";
    private static final String CARD_GAME = "GAME";
    private static final String CARD_SKOR = "SKOR";

    private final CardLayout cardLayout = new CardLayout();          
    private final JPanel root = new JPanel(cardLayout);              

    private final List<Long> scores = new ArrayList<>();
    private final DefaultListModel<String> scoreModel = new DefaultListModel<>();

    private GamePanel gamePanel;

    public ReactionTimeApp() {
        super("Reaction Time Tester");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(520, 440);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        root.add(buildMenuCard(), CARD_MENU);
        root.add(gamePanel, CARD_GAME);
        root.add(buildScoreCard(), CARD_SKOR);
        add(root);

        // (5) Event: WindowAdapter -> override windowClosing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int pilihan = JOptionPane.showConfirmDialog(
                        ReactionTimeApp.this,
                        "Yakin ingin keluar?",
                        "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                if (pilihan == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    //MENU
    private JPanel buildMenuCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));         
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(30, 33, 48));

        JLabel title = new JLabel("REACTION TIME TESTER", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel(
                "<html><center>Klik (atau tekan SPASI) secepat mungkin<br>"
                        + "saat layar berubah menjadi HIJAU.</center></html>",
                SwingConstants.CENTER);
        desc.setForeground(new Color(190, 195, 215));

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 8));          
        top.setOpaque(false);
        top.add(title);
        top.add(desc);

        JButton btnMulai = new JButton("Mulai");
        JButton btnSkor = new JButton("Skor Terbaik");
        JButton btnKeluar = new JButton("Keluar");


        btnMulai.addActionListener(e -> showGame());
        btnSkor.addActionListener(e -> cardLayout.show(root, CARD_SKOR));
        btnKeluar.addActionListener(e ->
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 12));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 120, 10, 120));
        buttons.add(btnMulai);
        buttons.add(btnSkor);
        buttons.add(btnKeluar);

        panel.add(top, BorderLayout.NORTH);
        panel.add(buttons, BorderLayout.CENTER);
        return panel;
    }

    // SKOR
    private JPanel buildScoreCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("10 Skor Terbaik (ms)", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JList<String> list = new JList<>(scoreModel);                 
        list.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scroll = new JScrollPane(list);                  

        JButton btnKembali = new JButton("Kembali");
        JButton btnReset = new JButton("Reset Skor");
        btnKembali.addActionListener(e -> cardLayout.show(root, CARD_MENU));
        btnReset.addActionListener(e -> {
            scores.clear();
            refreshScoreList();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottom.add(btnKembali);
        bottom.add(btnReset);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void showGame() {
        cardLayout.show(root, CARD_GAME);
        gamePanel.reset();
    }

    private void addScore(long ms) {
        scores.add(ms);
        refreshScoreList();
    }

    private void refreshScoreList() {
        List<Long> sorted = new ArrayList<>(scores);
        Collections.sort(sorted);
        scoreModel.clear();
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            scoreModel.addElement(String.format("%2d.  %4d ms", i + 1, sorted.get(i)));
        }
    }

    //GAME
    private class GamePanel extends JPanel {

        private static final int IDLE = 0, WAITING = 1, READY = 2, EARLY = 3, RESULT = 4;

        private int state = IDLE;
        private long startNano;
        private final Random random = new Random();
        private javax.swing.Timer waitTimer;

        private final JLabel infoLabel = new JLabel("", SwingConstants.CENTER);
        private final JPanel area = new JPanel(new BorderLayout());  
        private final JLabel areaLabel = new JLabel("", SwingConstants.CENTER);

        GamePanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            infoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            areaLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
            areaLabel.setForeground(Color.WHITE);
            area.add(areaLabel, BorderLayout.CENTER);
            area.setFocusable(true);

            JButton btnKembali = new JButton("Kembali ke Menu");
            btnKembali.addActionListener(e -> {
                stopTimer();
                cardLayout.show(root, CARD_MENU);
            });
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bottom.add(btnKembali);

            add(infoLabel, BorderLayout.NORTH);
            add(area, BorderLayout.CENTER);
            add(bottom, BorderLayout.SOUTH);

            area.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    area.requestFocusInWindow();
                    handleInput();
                }
            });

            area.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        handleInput();
                    }
                }
            });
        }

        void reset() {
            stopTimer();
            state = IDLE;
            setArea(new Color(52, 90, 160), "Klik / tekan SPASI untuk mulai");
            infoLabel.setText("Siap?");
            SwingUtilities.invokeLater(area::requestFocusInWindow);
        }

        private void handleInput() {
            switch (state) {
                case IDLE:
                case EARLY:
                case RESULT:
                    startWaiting();
                    break;
                case WAITING:
                    stopTimer();
                    state = EARLY;
                    setArea(new Color(230, 150, 30), "Terlalu cepat! Klik untuk coba lagi");
                    infoLabel.setText("Tunggu sampai HIJAU");
                    break;
                case READY:
                    long ms = (System.nanoTime() - startNano) / 1_000_000;
                    state = RESULT;
                    addScore(ms);
                    setArea(new Color(52, 90, 160), ms + " ms  -  klik untuk ulangi");
                    infoLabel.setText(rating(ms));
                    break;
                default:
                    break;
            }
        }

        private void startWaiting() {
            state = WAITING;
            setArea(new Color(200, 50, 50), "Tunggu warna HIJAU...");
            infoLabel.setText("Jangan klik dulu!");
            int delay = 1500 + random.nextInt(3000);   // 1,5 - 4,5 detik
            waitTimer = new javax.swing.Timer(delay, e -> {
                state = READY;
                startNano = System.nanoTime();
                setArea(new Color(40, 170, 80), "KLIK SEKARANG!");
                infoLabel.setText("Cepat!");
            });
            waitTimer.setRepeats(false);
            waitTimer.start();
        }

        private void stopTimer() {
            if (waitTimer != null) {
                waitTimer.stop();
            }
        }

        private void setArea(Color bg, String text) {
            area.setBackground(bg);
            areaLabel.setText(text);
        }

        private String rating(long ms) {
            if (ms < 200) return "Luar biasa! Refleks super cepat";
            if (ms < 270) return "Bagus! Di atas rata-rata";
            if (ms < 350) return "Normal";
            return "Agak lambat, coba lagi!";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReactionTimeApp().setVisible(true));
    }
}
