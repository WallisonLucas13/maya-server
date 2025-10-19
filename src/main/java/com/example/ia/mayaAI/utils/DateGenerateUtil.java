package com.example.ia.mayaAI.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateGenerateUtil {
    public static Instant now(){
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    };
}
