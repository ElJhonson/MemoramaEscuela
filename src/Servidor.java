
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Servidor extends javax.swing.JFrame {

    private static final int PORT = 5555;
    private static final Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());

    private static final Set<String> connectedUsers = new HashSet<>();
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private static JTextArea taMensajes;
    private static final Map<String, PrintWriter> usuariosConectados = new HashMap<>();
    // Map<username, estado>
    private static Map<String, String> estadosUsuarios = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Partida> partidasActivas = new HashMap<>();

    public Servidor() {
        initComponents();
        taMensajes = TAMensajes;
        setTitle("Servidor");
        setLocationRelativeTo(this);
    }

    public static void iniciarServidor() {
        taMensajes.append("Servidor iniciado en el puerto " + PORT + "\n");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarUsuariosConectados() {
        StringBuilder sb = new StringBuilder();
        synchronized (estadosUsuarios) {
            estadosUsuarios.forEach((usuario, estado) -> {
                if (!"desconectado".equals(estado)) {
                    sb.append(usuario).append(";").append(estado).append(",");
                }
            });
        }
        String listaUsuarios = sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";

        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println("USUARIOS_CONECTADOS:" + (listaUsuarios.isEmpty() ? "VACIO" : listaUsuarios));
            }
        }
    }

    private static void actualizarEstadoUsuario(String usuario, String estado) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return;
        }
        UsuarioUtil.estadoJuego(usuario, estado);  // actualiza BD
        synchronized (estadosUsuarios) {
            if ("desconectado".equals(estado)) {
                estadosUsuarios.remove(usuario);
            } else {
                estadosUsuarios.put(usuario, estado);
            }
        }
        enviarUsuariosConectados();
    }

    private static Partida obtenerPartidaDeUsuario(String nombreUsuario) {
        synchronized (partidasActivas) {
            for (Partida partida : partidasActivas.values()) {
                if (partida.getJugador1().equals(nombreUsuario) || partida.getJugador2().equals(nombreUsuario)) {
                    return partida;
                }
            }
        }
        return null;
    }

    private static class ClientHandler extends Thread {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            synchronized (clientHandlers) {
                clientHandlers.add(this);
            }
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String mensajeInicial = in.readLine();  // Leemos el primer mensaje
                if (mensajeInicial == null) {
                    return;
                }

                String[] partes = mensajeInicial.split(";");
                String tipo = partes[0];  // El primer parámetro define el tipo (LOGIN o REGISTRO)

                if ("LOGIN".equalsIgnoreCase(tipo) && partes.length == 3) {
                    String usuario = partes[1];
                    String pass = partes[2];
                    if (UsuarioUtil.iniciarSesion(usuario, pass)) {
                        out.println("LOGIN_OK");
                        UsuarioUtil.estadoConexion(usuario, true);
                        this.userName = usuario;

                        actualizarEstadoUsuario(userName, "disponible");

                        synchronized (usuariosConectados) {
                            usuariosConectados.put(userName, out);
                            estadosUsuarios.put(userName, "disponible"); // estado inicial

                        }
                        enviarUsuariosConectados();

                    } else {
                        out.println("LOGIN_FAIL");
                        UsuarioUtil.estadoConexion(usuario, false);
                        return;
                    }
                } else if ("REGISTRO".equalsIgnoreCase(tipo) && partes.length == 6) {
                    String nombre = partes[1];
                    String email = partes[2];
                    String telefono = partes[3];
                    String usuario = partes[4];
                    String pass = partes[5];

                    if (UsuarioUtil.registrar(nombre, email, telefono, usuario, pass)) {
                        out.println("REGISTRO_OK");
                        UsuarioUtil.estadoConexion(usuario, true);
                        UsuarioUtil.estadoJuego(usuario, "disponible");

                        this.userName = usuario;
                        synchronized (usuariosConectados) {
                            usuariosConectados.put(userName, out);
                            estadosUsuarios.put(userName, "disponible");

                        }
                        actualizarEstadoUsuario(usuario, "disponible");

                        enviarUsuariosConectados();

                    } else {
                        out.println("REGISTRO_FAIL");
                        UsuarioUtil.estadoConexion(usuario, false);
                        UsuarioUtil.estadoJuego(usuario, "desconectado");

                        return; // cerramos la conexión si el registro falla
                    }
                } else {
                    out.println("COMANDO_INVALIDO");
                    return; // Si el comando es inválido, cerramos la conexión
                }

                synchronized (connectedUsers) {
                    connectedUsers.add(userName);
                    taMensajes.append(userName + " se ha conectado.\n");
                }

                String input;
                while ((input = in.readLine()) != null) {
                    taMensajes.append(userName + ": " + input + "\n");
                    if (input.startsWith("INVITAR;")) {
                        String[] datos = input.split(";");
                        if (datos.length == 3) {
                            String invitado = datos[1];
                            String dificultad = datos[2];

                            PrintWriter salidaInvitado;
                            synchronized (usuariosConectados) {
                                salidaInvitado = usuariosConectados.get(invitado);
                            }
                            if (salidaInvitado != null && UsuarioUtil.estaDisponible(invitado)) {
                                salidaInvitado.println("INVITACION_DE:" + userName + ";" + dificultad);
                            } else {
                                out.println("ERROR: Usuario no disponible para invitación.");
                            }
                        }
                    } else if (input.startsWith("INVITACION_RESPUESTA;")) {
                        String[] datos = input.split(";");
                        if (datos.length >= 3) {
                            String respuesta = datos[1];
                            String origen = datos[2];
                            String dificultad = datos.length == 4 ? datos[3] : "";

                            PrintWriter salidaOrigen;
                            synchronized (usuariosConectados) {
                                salidaOrigen = usuariosConectados.get(origen);
                            }

                            if (salidaOrigen != null) {
                                if ("ACEPTAR".equalsIgnoreCase(respuesta)) {
                                    actualizarEstadoUsuario(userName, "ocupado");
                                    actualizarEstadoUsuario(origen, "ocupado");
                                    enviarUsuariosConectados();

                                    // Crear partida
                                    Partida partida = new Partida(userName, origen, dificultad);
                                    String partidaID = userName + "-" + origen;
                                    synchronized (partidasActivas) {
                                        partidasActivas.put(partidaID, partida);
                                    }

                                    // Mandar cartas a ambos
                                    List<String> cartas = partida.getCartas();
                                    String cartasStr = String.join(",", cartas);
                                    salidaOrigen.println("INICIAR_PARTIDA:" + userName + ";" + dificultad + ";" + cartasStr);
                                    out.println("INICIAR_PARTIDA:" + origen + ";" + dificultad + ";" + cartasStr);

                                    salidaOrigen.println("INVITACION_ACEPTADA:" + userName + ";" + dificultad);
                                    out.println("PARTIDA_CONFIRMADA:" + origen + ";" + dificultad);
                                } else {
                                    actualizarEstadoUsuario(userName, "disponible");
                                    enviarUsuariosConectados();
                                    salidaOrigen.println("INVITACION_RECHAZADA:" + userName);

                                }
                            }
                        }
                    } else if (input.equals("PARTIDA_TERMINADA")) {
                        if (userName != null) {
                            actualizarEstadoUsuario(userName, "disponible");
                        }

                        enviarUsuariosConectados(); // Para actualizar la lista en todos

                    }else if (input.startsWith("VOLTEAR_CARTA;")) {
                        String[] datos = input.split(";");
                        if (datos.length == 2) {
                            int indice = Integer.parseInt(datos[1]);

                            Partida partida = obtenerPartidaDeUsuario(userName);
                            if (partida != null) {
                                // Notificar al otro jugador
                                String otroJugador = partida.getJugador1().equals(userName)
                                        ? partida.getJugador2()
                                        : partida.getJugador1();

                                PrintWriter salidaOtro;
                                synchronized (usuariosConectados) {
                                    salidaOtro = usuariosConectados.get(otroJugador);
                                }

                                if (salidaOtro != null) {
                                    salidaOtro.println("VOLTEAR_CARTA:" + indice);
                                    salidaOtro.flush();

                                }
                            }
                        }
                    }
                    
                    
                    else if (input.startsWith("FIN_PARTIDA;")) {

                        partes = input.split(";");
                        if (partes.length >= 2) {
                            String contrincante = partes[1].trim();

                            // Validar nombres válidos
                            if (userName == null || userName.trim().isEmpty()
                                    || contrincante == null || contrincante.trim().isEmpty()) {
                                System.out.println("FIN_PARTIDA recibido con datos inválidos. Ignorado.");
                                return;
                            }

                            // Cambiar estado del jugador que la envió
                            UsuarioUtil.estadoJuego(userName, "disponible");
                            actualizarEstadoUsuario(userName, "disponible");

                            actualizarEstadoUsuario(contrincante, "disponible");

                            // Notificar al contrincante que la partida terminó
                            PrintWriter salidaContrincante;
                            synchronized (usuariosConectados) {
                                salidaContrincante = usuariosConectados.get(contrincante);
                            }

                            if (salidaContrincante != null) {
                                salidaContrincante.println("CIERRE_FORZADO:" + userName);
                                UsuarioUtil.estadoJuego(contrincante, "disponible");
                            }

                            // Actualizar lista a todos
                            enviarUsuariosConectados();
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (userName != null) {
                    synchronized (connectedUsers) {
                        connectedUsers.remove(userName);
                    }
                    synchronized (usuariosConectados) {
                        usuariosConectados.remove(userName);
                    }
                    UsuarioUtil.estadoConexion(userName, false);
                    actualizarEstadoUsuario(userName, "desconectado");

                    taMensajes.append(userName + " se ha desconectado.\n");
                    enviarUsuariosConectados();

                }
                try {
                    socket.close();
                } catch (IOException e) {
                    taMensajes.append("Error al cerrar la conexión de " + userName + ".\n");
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }

            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BConectar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TAMensajes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BConectar.setBackground(new java.awt.Color(255, 0, 0));
        BConectar.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        BConectar.setForeground(new java.awt.Color(255, 255, 255));
        BConectar.setText("Iniciar");
        BConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BConectarActionPerformed(evt);
            }
        });

        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mensajes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(102, 0, 0))); // NOI18N

        TAMensajes.setColumns(20);
        TAMensajes.setRows(5);
        jScrollPane1.setViewportView(TAMensajes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addComponent(BConectar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BConectar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BConectarActionPerformed
        new Thread(() -> {
            iniciarServidor();

        }).start();
        BConectar.setEnabled(false);

    }//GEN-LAST:event_BConectarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Servidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Servidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Servidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Servidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Servidor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BConectar;
    private javax.swing.JTextArea TAMensajes;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
