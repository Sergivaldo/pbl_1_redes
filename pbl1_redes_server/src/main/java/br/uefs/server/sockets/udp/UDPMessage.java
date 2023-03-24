package br.uefs.server.sockets.udp;

import java.util.HashMap;
import java.util.Map;

public class UDPMessage {

    /**
     * Converte a mensagem recebida do medidor em um objeto map
     * @param message Mensagem recebida do medidor
     * @return Map com cada parte da mensagem
     */
    public Map<String,String> toMap(String message){
        String[] messageParts = message.split(";");
        Map<String,String> messageMap = new HashMap<>();
        messageMap.put("code",messageParts[0]);
        messageMap.put("consumption",messageParts[1]);
        messageMap.put("measured_at",messageParts[2]);

        return messageMap;
    }
}
