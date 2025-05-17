
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Partida {

    private String jugador1;
    private String jugador2;
    private String dificultad;
    private List<String> cartas;

    public Partida(String jugador1, String jugador2, String dificultad) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.dificultad = dificultad;
        this.cartas = generarCartas(dificultad);  // ← AQUÍ se usa el método que falta
    }

    private List<String> generarCartas(String dificultad) {
        int total;
        switch (dificultad.toLowerCase()) {
            case "facil":
            case "fácil":
                total = 4;
                break;
            case "intermedio":
            case "media":
                total = 8;
                break;
            case "dificil":
            case "difícil":
                total = 16;
                break;
            default:
                total = 4;
        }

        List<String> pares = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            pares.add(String.valueOf(i));
            pares.add(String.valueOf(i)); // duplicar para formar pares
        }

        Collections.shuffle(pares);
        return pares;
    }

    public List<String> getCartas() {
        return cartas;
    }

    public String getJugador1() {
        return jugador1;
    }

    public String getJugador2() {
        return jugador2;
    }

    
    
}
