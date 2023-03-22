package br.uefs.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public static String currentDateTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String formatedDate = now.format(formatter);

        return formatedDate;
    }

    public static String currentDateTimeRFC(){
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        LocalDateTime now = LocalDateTime.now();
        String formatedDate = formatter.format(now.atZone(ZoneId.of("UTC")));

        return formatedDate;
    }

}
