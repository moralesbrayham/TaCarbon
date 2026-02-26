package org.example.network;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.net.*;

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
                    System.out.println("Solicitud recibida desde: " + packet.getAddress().getHostAddress());
                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (DISCOVERY_REQUEST.equals(message)) {

                        String serverIp = getLocalIp();
                        System.out.println("IP detectada: " + serverIp);

                        String response = DISCOVERY_RESPONSE + ":" + serverIp + ":8080";

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

    // 🔥 ESTE MÉTODO VA FUERA
/*
    private String getLocalIp() throws Exception {

    try (final DatagramSocket socket = new DatagramSocket()) {
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        return socket.getLocalAddress().getHostAddress();
    }
}
*/
    private String getLocalIp() {
    return "192.168.1.138";
}

    @PreDestroy
    public void stop() {
        running = false;
    }
}
