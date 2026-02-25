import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.Timer;

public class TelaJogo {

    private Jogo jogo = new Jogo();

    private ImageIcon bauIcon;
    private ImageIcon moedaIcon;
    private ImageIcon diamanteIcon;

    private JLabel[] slots = new JLabel[9];

    private double saldoAtualAnimado = 0;
    private Timer animacaoSaldo;

    // 🔹 NOVO: linha vencedora
    private int[] linhaVencedora;
    private Timer animacaoGlow;

    public TelaJogo() {

        Color fundo = new Color(20, 20, 20);
        Color painel = new Color(35, 35, 35);
        Color verde = new Color(0, 200, 120);
        Color vermelho = new Color(220, 70, 70);
        Color azul = new Color(0x0000FF)

        bauIcon = redimensionar(carregarIcone("tesou.png"), 70, 70);
        moedaIcon = redimensionar(carregarIcone("din.png"), 70, 70);
        diamanteIcon = redimensionar(carregarIcone("diaman.png"), 70, 70);

        ImageIcon[] simbolos = { bauIcon, moedaIcon, diamanteIcon };

        JFrame frame = new JFrame("🎰 Jogo da Sorte");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(fundo);

        JLabel saldoLabel = new JLabel("", SwingConstants.CENTER);
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saldoLabel.setForeground(verde);
        saldoLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        saldoAtualAnimado = jogo.getSaldo();
        saldoLabel.setText("Saldo: R$ " + saldoAtualAnimado);

        JPanel tabuleiro = new JPanel(new GridLayout(3, 3, 8, 8));
        tabuleiro.setBackground(painel);
        tabuleiro.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (int i = 0; i < 9; i++) {
            slots[i] = new JLabel(bauIcon, SwingConstants.CENTER);
            slots[i].setOpaque(true);
            slots[i].setBackground(Color.BLACK);
            slots[i].setBorder(BorderFactory.createLineBorder(verde, 2));
            tabuleiro.add(slots[i]);
        }

        JLabel resultadoLabel = new JLabel("Faça sua aposta 🎲", SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultadoLabel.setForeground(Color.WHITE);

        JTextField apostaField = new JTextField(8);
        apostaField.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton jogarBtn = new JButton("JOGAR");
        jogarBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jogarBtn.setBackground(verde);
        jogarBtn.setForeground(Color.BLACK);
        jogarBtn.setFocusPainted(false);

        JPanel painelAposta = new JPanel();
        painelAposta.setBackground(painel);
        painelAposta.add(new JLabel("Aposta R$: "));
        painelAposta.add(apostaField);
        painelAposta.add(jogarBtn);

        jogarBtn.addActionListener(e -> {

            String texto = apostaField.getText();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Digite o valor da aposta!");
                return;
            }

            double valor;
            try {
                valor = Double.parseDouble(texto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Digite apenas números!");
                return;
            }

            jogarBtn.setEnabled(false);
            resultadoLabel.setText("🎰 Girando...");

            for (JLabel slot : slots) {
                slot.setBorder(BorderFactory.createLineBorder(verde, 2));
            }

            Timer timer = new Timer(100, null);
            final int[] contador = {0};

            timer.addActionListener(ev -> {

                for (int i = 0; i < 9; i++) {
                    slots[i].setIcon(simbolos[(int)(Math.random() * simbolos.length)]);
                }

                contador[0]++;

                if (contador[0] >= 20) {
                    timer.stop();

                    boolean ganhou = verificarVitoria();

                    if (ganhou) {
                        jogo.ganhar(valor * 5);
                        resultadoLabel.setText("🎉 VOCÊ GANHOU!");
                        resultadoLabel.setForeground(azul);
                    } else {
                        jogo.perder(valor);
                        resultadoLabel.setText("❌ VOCÊ PERDEU!");
                        resultadoLabel.setForeground(vermelho);
                    }

                    animarSaldo(saldoLabel, jogo.getSaldo());

                    apostaField.setText("");
                    jogarBtn.setEnabled(true);
                }
            });

            timer.start();
        });

        JPanel sul = new JPanel(new GridLayout(2, 1));
        sul.setBackground(painel);
        sul.add(resultadoLabel);
        sul.add(painelAposta);

        frame.add(saldoLabel, BorderLayout.NORTH);
        frame.add(tabuleiro, BorderLayout.CENTER);
        frame.add(sul, BorderLayout.SOUTH);

        frame.setSize(360, 640);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean verificarVitoria() {

        int[][] c = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        for (int[] x : c) {
            ImageIcon a = (ImageIcon) slots[x[0]].getIcon();
            ImageIcon b = (ImageIcon) slots[x[1]].getIcon();
            ImageIcon d = (ImageIcon) slots[x[2]].getIcon();

            if (a == b && b == d) {
                linhaVencedora = x;
                animarLinhaVencedora();
                return true;
            }
        }
        return false;
    }


    private void animarLinhaVencedora() {

        animacaoGlow = new Timer(300, null);
        final boolean[] ligado = {false};

        animacaoGlow.addActionListener(e -> {
            ligado[0] = !ligado[0];
            for (int i : linhaVencedora) {
                slots[i].setBorder(
                        BorderFactory.createLineBorder(
                                ligado[0] ? Color.YELLOW : new Color(0, 200, 120),
                                ligado[0] ? 5 : 2
                        )
                );
            }
        });

        animacaoGlow.start();
        new Timer(3000, e -> animacaoGlow.stop()).start();
    }


    private void animarSaldo(JLabel label, double novoSaldo) {

        double inicio = saldoAtualAnimado;
        double diferenca = novoSaldo - inicio;

        animacaoSaldo = new Timer(30, null);
        final int[] passo = {0};

        animacaoSaldo.addActionListener(e -> {
            passo[0]++;
            double valor = inicio + (diferenca * passo[0] / 20);
            label.setText("Saldo: R$ " + String.format("%.2f", valor));

            if (passo[0] >= 20) {
                saldoAtualAnimado = novoSaldo;
                animacaoSaldo.stop();
            }
        });

        animacaoSaldo.start();
    }

    private ImageIcon carregarIcone(String nome) {
        URL url = getClass().getResource(nome);
        return url == null ? new ImageIcon() : new ImageIcon(url);
    }

    private ImageIcon redimensionar(ImageIcon icon, int w, int h) {
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}