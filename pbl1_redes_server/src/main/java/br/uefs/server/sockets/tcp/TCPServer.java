package br.uefs.server.sockets.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
    private final int port;

    public TCPServer(int port){
        this.port = port;
    }

    /**
     * Inicia um servidor TCP via socket. Sempre que
     * uma conexão com o cliente é aceita, o servidor
     * irá criar uma thread nova para responder a requisição do cliente.
     */
    public void run() {
        try{
            String host = InetAddress.getLocalHost().getHostAddress();
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Servidor TCP iniciado em: http://"+host+":"+port);
            while (true) {
                Socket client = serverSocket.accept();
                new TCPClient(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}