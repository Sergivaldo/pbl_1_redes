package br.uefs.server.sockets.udp;

import java.util.HashMap;
import java.util.Map;

public class UDPMessage {
    public Map<String,String> toMap(String message){
        String[] messageParts = message.split(";");
        Map<String,String> messageMap = new HashMap<>();
        messageMap.put("code",messageParts[0]);
        messageMap.put("consumption",messageParts[1]);
        messageMap.put("measured_at",messageParts[2]);

        return messageMap;
    }
}
