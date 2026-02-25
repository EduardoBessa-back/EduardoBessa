import java.util.Random;

public class Jogo {

    private double saldo = 100.0;
    private int chanceJogador = 50;

    public double getSaldo() {
        return saldo;
    }

    public void ganhar(double valor) {
        saldo += valor;
    }

    public void perder(double valor) {
        saldo -= valor;
        if (saldo < 0) {
            saldo = 0;
        }
    }

    public boolean jogar(double aposta) {

        if (aposta <= 0 || aposta > saldo) {
            return false;
        }

        Random random = new Random();
        int sorteio = random.nextInt(100);

        if (sorteio < chanceJogador) {
            return true;
        } else {
            return false;
        }
    }
}