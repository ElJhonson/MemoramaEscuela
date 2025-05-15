
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Memorama extends javax.swing.JFrame {

    private JButton[] botones;
    private Icon[] imagenes;
    private Icon imagenReverso = new ImageIcon(getClass().getResource("/imagenes/reverso.png"));

    private int pares;

    public Memorama(String contrincante, String dificultad, PrintWriter out, String usuarioActual) {
        initComponents();
        setTitle("Partida con " + contrincante + " - Dificultad: " + dificultad);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        dificultad = dificultad.trim().toLowerCase();
        System.out.println("DIFICULTAD RECIBIDA: [" + dificultad + "]");

        switch (dificultad) {
            case "fácil":
            case "facil":
                pares = 8;
                break;
            case "intermedio":
                pares = 16;
                break;
            case "difícil":
            case "dificil":
                pares = 32;
                break;
            default:
                System.out.println("Dificultad no reconocida, se usará modo fácil");
                pares = 8;
        }

        int totalCartas = pares * 2;

        // Cargar imágenes
        imagenes = new Icon[totalCartas];
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i <= pares; i++) {
            indices.add(i);
            indices.add(i); // Dos veces para formar par
        }
        Collections.shuffle(indices);
        for (int i = 0; i < totalCartas; i++) {
            imagenes[i] = new ImageIcon(getClass().getResource("/imagenes/" + indices.get(i) + ".png"));

        }

        botones = new JButton[totalCartas];

        PTablero.removeAll();

        int columnas = (int) Math.sqrt(totalCartas);
        int filas = (int) Math.ceil((double) totalCartas / columnas);
        PTablero.setLayout(new GridLayout(filas, columnas, 5, 5));

        for (int i = 0; i < totalCartas; i++) {
            botones[i] = new JButton();
            botones[i].setIcon(imagenReverso);
            botones[i].putClientProperty("indice", i);
            botones[i].addActionListener(e -> {
                JButton btn = (JButton) e.getSource();
                int idx = (int) btn.getClientProperty("indice");
                btn.setIcon(imagenes[idx]);
            });
            PTablero.add(botones[i]);
        }

        PTablero.revalidate();
        PTablero.repaint();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (out != null && contrincante != null && !contrincante.trim().isEmpty()) {
                    out.println("FIN_PARTIDA;" + contrincante);
                }
            }
        });

        pack();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        JUsuario2 = new javax.swing.JLabel();
        JUsuario1 = new javax.swing.JLabel();
        PTablero = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Rockwell", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MEMORAMA");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 360, 50));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 870, 70));

        JUsuario2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        JUsuario2.setForeground(new java.awt.Color(0, 0, 0));
        JUsuario2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JUsuario2.setText("USUARIO 2");
        jPanel1.add(JUsuario2, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 260, 130, -1));

        JUsuario1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        JUsuario1.setForeground(new java.awt.Color(0, 0, 0));
        JUsuario1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        JUsuario1.setText("USUARIO 1");
        jPanel1.add(JUsuario1, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 110, 130, -1));

        PTablero.setBackground(new java.awt.Color(153, 153, 153));
        PTablero.setLayout(new java.awt.BorderLayout());
        jPanel1.add(PTablero, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 740, 540));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 160, 70, -1));

        jLabel3.setBackground(new java.awt.Color(204, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Aciertos");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 140, 70, -1));

        jLabel4.setBackground(new java.awt.Color(204, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Errores");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 200, 70, -1));
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 220, 70, -1));
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 370, 70, -1));

        jLabel5.setBackground(new java.awt.Color(204, 0, 0));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 0, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Errores");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 350, 70, -1));
        jPanel1.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 310, 70, -1));

        jLabel6.setBackground(new java.awt.Color(204, 0, 0));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(204, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Aciertos");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 290, 70, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 870, 630));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JUsuario1;
    private javax.swing.JLabel JUsuario2;
    private javax.swing.JPanel PTablero;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables

}
