package org.example.network;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.net.*;
import org.example.pos.ConfiguracionIP;

@Component
public class UdpDiscoveryServer {

    private static final int DISCOVERY_PORT = 8888;
    private static final String DISCOVERY_REQUEST = "DISCOVER_TACARBON_POS";
    private static final String DISCOVERY_RESPONSE = "POS_FOUND";

    private boolean running = true;
    private Thread serverThread;

    @PostConstruct
    public void start() {

        serverThread = new Thread(() -> {

            try (DatagramSocket socket =
                         new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"))) {

                socket.setBroadcast(true);
                byte[] buffer = new byte[1024];

                while (running) {

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    System.out.println("Solicitud recibida desde: "
                            + packet.getAddress().getHostAddress());

                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (DISCOVERY_REQUEST.equals(message)) {

                        // 🔥 AQUÍ decidimos qué IP usar
                        String manualIp = ConfiguracionIP.loadIp();
                        String ipAUsar;

                        if (manualIp != null && !manualIp.isEmpty()) {
                            ipAUsar = manualIp;
                            System.out.println("Usando IP manual: " + ipAUsar);
                        } else {
                            ipAUsar = getLocalIp();
                            System.out.println("Usando IP automática: " + ipAUsar);
                        }

                        String response = DISCOVERY_RESPONSE + ":" + ipAUsar + ":8080";

                        DatagramPacket responsePacket = new DatagramPacket(
                                response.getBytes(),
                                response.length(),
                                packet.getAddress(),
                                packet.getPort()
                        );

                        socket.send(responsePacket);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
        System.out.println("UDP Discovery Server iniciado.");
    }

    // Método automático REAL
    private String getLocalIp() throws Exception {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
    }
}
