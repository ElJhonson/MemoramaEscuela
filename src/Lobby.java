
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Lobby extends javax.swing.JFrame {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5555;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nombreLocal;
    JTextField cajasTexto[] = new JTextField[4];
    JButton botones[] = new JButton[2];
    DefaultListModel modeloLista = new DefaultListModel();
    Memorama partida;
    private boolean enPartida = false;  // inicialmente falso

    private boolean connected = false;
    private String usuarioSeleccionado;
    private static String usuarioActual;

    public Lobby(Socket socket, PrintWriter out, BufferedReader in, String nombreLocal) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.nombreLocal = nombreLocal;
        initComponents();
        setTitle("Jugador: " + nombreLocal);
        modeloLista = new DefaultListModel<>();
        LClientes.setModel(modeloLista);

        setLocationRelativeTo(this);
        LNombre.setText(this.nombreLocal);

        LClientes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        LClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LClientesMouseClicked(evt);
            }
        });

        escuchaServidor();
    }

    private boolean partidaTerminada = false;

    private void escuchaServidor() {
        Thread escucha = new Thread(() -> {
            try {
                String linea;
                while ((linea = in.readLine()) != null) {
                    if (linea.startsWith("USUARIOS_CONECTADOS:")) {
                        System.out.println("Procesando usuarios conectados: " + linea);

                        String usuarios = linea.substring("USUARIOS_CONECTADOS:".length());
                        if (!usuarios.equals("VACIO")) {
                            String[] lista = usuarios.split(",");
                            actualizarListaUsuarios(usuarios);
                            for (String usuarioInfo : lista) {
                                String[] partes = usuarioInfo.split(";");
                                if (partes.length >= 2) {
                                    String nombre = partes[0];
                                    String estado = partes[1];
                                    // Aquí actualizas la UI
                                }
                            }
                        }

                        actualizarListaUsuarios(usuarios);
                    } else if (linea.startsWith("INVITACION_DE:")) {
                        String[] partes = linea.substring("INVITACION_DE:".length()).split(";");
                        String invitador = partes[0];
                        String dificultad = partes[1];

                        int respuesta = JOptionPane.showConfirmDialog(null,
                                invitador + " te ha invitado a jugar en dificultad " + dificultad + ". ¿Aceptar?",
                                "Invitación de juego",
                                JOptionPane.YES_NO_OPTION);

                        if (respuesta == JOptionPane.YES_OPTION) {
                            out.println("INVITACION_RESPUESTA;ACEPTAR;" + invitador + ";" + dificultad);
                        } else {
                            out.println("INVITACION_RESPUESTA;RECHAZAR;" + invitador);
                        }

                    } else if (linea.startsWith("INVITACION_ACEPTADA:")) {
                        enPartida = true;
                        String[] partes = linea.substring("INVITACION_ACEPTADA:".length()).split(";");
                        String invitado = partes[0];
                        String dificultad = partes[1];

//                        JOptionPane.showMessageDialog(null,
//                                invitado + " aceptó tu invitación. Esperando inicio de partida...");
                        partidaTerminada = false;

                        // Ya no abras Memorama aquí
                    } else if (linea.startsWith("PARTIDA_CONFIRMADA:")) {
                        String[] partes = linea.substring("PARTIDA_CONFIRMADA:".length()).split(";");
                        String invitador = partes[0];
                        String dificultad = partes[1];

                        partidaTerminada = false;
                        enPartida = true;

                        // No abras Memorama todavía
                    } else if (linea.startsWith("INICIAR_PARTIDA:")) {
                        String[] partes = linea.substring("INICIAR_PARTIDA:".length()).split(";");
                        String contrincante = partes[0];
                        String dificultad = partes[1];
                        String ordenCartas = partes[2];

                          // minimizar ventana actual
                        SwingUtilities.invokeLater(() -> {
                            partida = new Memorama(contrincante, dificultad, out, usuarioActual, ordenCartas);
                            partida.setVisible(true);
                            this.setState(JFrame.ICONIFIED);
                        });
                    } else if (linea.startsWith("INVITACION_RECHAZADA:")) {
                        String rechazante = linea.substring("INVITACION_RECHAZADA:".length());
                        JOptionPane.showMessageDialog(null,
                                rechazante + " rechazó tu invitación.");
                    } else if (linea.startsWith("CIERRE_FORZADO:")) {
                        if (!partidaTerminada) {
                            partidaTerminada = true;
                            enPartida = false;

                            String otroJugador = linea.substring("CIERRE_FORZADO:".length());

                            JOptionPane.showMessageDialog(null,
                                    "Tu contrincante (" + otroJugador + ") ha salido de la partida. Volverás al lobby.");
                            this.setState(JFrame.NORMAL);
                            if (otroJugador != null && !otroJugador.trim().isEmpty()) {
                                out.println("FIN_PARTIDA;" + otroJugador);
                                this.setState(JFrame.NORMAL);
                                enPartida = false;

                            }

                            if (partida != null) {
                                partida.dispose();
                                partida = null;
                            }
                        }
                    } else {
                        System.out.println("Mensaje recibido: " + linea);
                    }

                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        });
        escucha.start();
    }

    private void LClientesMouseClicked(java.awt.event.MouseEvent evt) {
        Object seleccionado = LClientes.getSelectedValue();
        if (seleccionado != null) {
            usuarioSeleccionado = seleccionado.toString();
            // Se lee la conversación completa del usuario actual (archivo "usuarioActual.txt")

        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        CBDificultad = new javax.swing.JComboBox<>();
        BInvitar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        LClientes = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        LNombre = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Emoji", 1, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 51));
        jLabel1.setText("Dificultad");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 70, -1));

        CBDificultad.setFont(new java.awt.Font("Segoe UI Semibold", 0, 13)); // NOI18N
        CBDificultad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FACIL", "INTERMEDIO", "DIFICIL" }));
        jPanel1.add(CBDificultad, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 150, -1));

        BInvitar.setBackground(new java.awt.Color(255, 0, 51));
        BInvitar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        BInvitar.setForeground(new java.awt.Color(255, 255, 255));
        BInvitar.setText("INVITAR");
        BInvitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BInvitarActionPerformed(evt);
            }
        });
        jPanel1.add(BInvitar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 160, -1, -1));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        LClientes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Jugadores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        LClientes.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(LClientes);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 260, 420));

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI Emoji", 1, 52)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("MEMORAMA");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 360, 80));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Bienvenido ");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, -1, -1));

        LNombre.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        LNombre.setForeground(new java.awt.Color(255, 255, 255));
        LNombre.setText("Bienvenido ");
        jPanel2.add(LNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 100, 90, 20));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 120));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 640));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BInvitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BInvitarActionPerformed
        if (LClientes.getModel().getSize() == 0) {
            JOptionPane.showMessageDialog(null, "No hay jugadores disponibles.");
            return;
        }

        String jugadorSeleccionado = LClientes.getSelectedValue();
        if (jugadorSeleccionado == null) {
            JOptionPane.showMessageDialog(null, "Selecciona un jugador para invitar.");
            return;
        }
        if (jugadorSeleccionado.endsWith("(ocupado)")) {
            JOptionPane.showMessageDialog(null, "No puedes invitar a un jugador que está ocupado.");
            return;
        }

        if (enPartida) {
            JOptionPane.showMessageDialog(null, "Ya estás en una partida, no puedes invitar.");
            return;
        }

        String dificultad = CBDificultad.getSelectedItem().toString();
        // Remover estado del string para enviar solo el nombre
        String nombreJugador = jugadorSeleccionado.substring(0, jugadorSeleccionado.indexOf('(')).trim();

        out.println("INVITAR;" + nombreJugador + ";" + dificultad);

    }//GEN-LAST:event_BInvitarActionPerformed

    private void actualizarListaUsuarios(String users) {
        SwingUtilities.invokeLater(() -> {
            String[] separar = users.split(",");
            modeloLista.clear();
            for (String usuarioEstado : separar) {
                usuarioEstado = usuarioEstado.trim();
                if (!usuarioEstado.isEmpty()) {
                    String[] partes = usuarioEstado.split(";");
                    if (partes.length >= 1) {
                        String nombre = partes[0].trim();
                        String estado = (partes.length > 1) ? partes[1].trim() : "desconocido";

                        if (!nombre.equals(nombreLocal)) {
                            String textoMostrar = nombre + (estado.equalsIgnoreCase("ocupado") ? " (ocupado)" : " (disponible)");
                            modeloLista.addElement(textoMostrar);
                        }
                    }
                }
            }
        });
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Lobby.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Lobby(socket, in, out, nombreLocal).setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BInvitar;
    private javax.swing.JComboBox<String> CBDificultad;
    private javax.swing.JList<String> LClientes;
    private javax.swing.JLabel LNombre;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
