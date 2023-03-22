package br.uefs.server.sockets.udp;

import br.uefs.api_rest.controller.ClientController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;

public class UDPServer extends Thread{
    private int port;
    public UDPServer(int port){
        this.port = port;
    }

    public void run(){
        try {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            while (true){
                byte[] msg = new byte[256];
                //Prepara o pacote de dados
                DatagramPacket pkg = new DatagramPacket(msg, msg.length);
                //Recebimento da mensagem
                datagramSocket.receive(pkg);
                Map<String,String> message = new UDPMessage().toMap(new String(pkg.getData()).trim());
                ClientController controller = new ClientController();
                controller.updateClientConsumption(message);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
