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

    public TelaJogo() {

        // ===== IMAGENS REDIMENSIONADAS (TAMANHO CELULAR) =====
        bauIcon = redimensionar(carregarIcone("tesou.png"), 80, 80);
        moedaIcon = redimensionar(carregarIcone("din.png"), 80, 80);
        diamanteIcon = redimensionar(carregarIcone("diaman.png"), 80, 80);

        ImageIcon[] simbolos = { bauIcon, moedaIcon, diamanteIcon };

        // ===== JANELA =====
        JFrame frame = new JFrame("Jogo da Sorte - Jogo da Velha");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ===== SALDO =====
        JLabel saldoLabel = new JLabel(
                "Saldo: R$ " + jogo.getSaldo(),
                SwingConstants.CENTER
        );
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));


        JPanel tabuleiro = new JPanel(new GridLayout(3, 3, 10, 10));
        tabuleiro.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        for (int i = 0; i < 9; i++) {
            slots[i] = new JLabel(bauIcon, SwingConstants.CENTER);
            slots[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            tabuleiro.add(slots[i]);
        }

        // ===== RESULTADO =====
        JLabel resultadoLabel = new JLabel("Faça sua aposta!", SwingConstants.CENTER);

        // ===== APOSTA =====
        JTextField apostaField = new JTextField(8);
        JButton jogarBtn = new JButton("JOGAR");

        JPanel painelAposta = new JPanel();
        painelAposta.add(new JLabel("Valor da aposta:"));
        painelAposta.add(apostaField);
        painelAposta.add(jogarBtn);

        // ===== AÇÃO DO BOTÃO =====
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

            Timer timer = new Timer(100, null);
            final int[] contador = {0};

            timer.addActionListener(ev -> {

                for (int i = 0; i < 9; i++) {
                    slots[i].setIcon(
                            simbolos[(int)(Math.random() * simbolos.length)]
                    );
                }

                contador[0]++;

                if (contador[0] >= 20) {
                    timer.stop();

                    boolean ganhou = verificarVitoria();

                    if (ganhou) {
                        jogo.ganhar(valor * 5);
                        resultadoLabel.setText("🎉 JOGO DA VELHA! VOCÊ GANHOU!");
                    } else {
                        jogo.perder(valor);
                        resultadoLabel.setText("❌ Não foi dessa vez!");
                    }

                    saldoLabel.setText("Saldo: R$ " + jogo.getSaldo());
                    apostaField.setText("");
                    jogarBtn.setEnabled(true);
                }
            });

            timer.start();
        });

        // ===== MONTAGEM =====
        frame.add(saldoLabel, BorderLayout.NORTH);
        frame.add(tabuleiro, BorderLayout.CENTER);

        JPanel sul = new JPanel(new GridLayout(2, 1));
        sul.add(resultadoLabel);
        sul.add(painelAposta);

        frame.add(sul, BorderLayout.SOUTH);

        // ===== TAMANHO ESTILO CELULAR =====
        frame.pack();
        frame.setSize(360, 640);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ===== REGRA DO JOGO DA VELHA =====
    private boolean verificarVitoria() {

        int[][] combinacoes = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        for (int[] c : combinacoes) {
            ImageIcon a = (ImageIcon) slots[c[0]].getIcon();
            ImageIcon b = (ImageIcon) slots[c[1]].getIcon();
            ImageIcon d = (ImageIcon) slots[c[2]].getIcon();

            if (a == b && b == d) {
                return true;
            }
        }
        return false;
    }

    // ===== CARREGAR IMAGEM =====
    private ImageIcon carregarIcone(String nome) {
        URL url = getClass().getResource(nome);
        if (url == null) {
            System.out.println("Imagem não encontrada: " + nome);
            return new ImageIcon();
        }
        return new ImageIcon(url);
    }

    // ===== REDIMENSIONAR IMAGEM =====
    private ImageIcon redimensionar(ImageIcon icon, int largura, int altura) {
        Image img = icon.getImage().getScaledInstance(
                largura, altura, Image.SCALE_SMOOTH
        );
        return new ImageIcon(img);
    }
}