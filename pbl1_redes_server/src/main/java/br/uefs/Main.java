package br.uefs;

import br.uefs.server.sockets.tcp.TCPServer;
import br.uefs.server.sockets.udp.UDPServer;

public class Main {
    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer(4444);
        tcpServer.start();
        UDPServer udpServer = new UDPServer(4555);
        udpServer.start();
    }
}
