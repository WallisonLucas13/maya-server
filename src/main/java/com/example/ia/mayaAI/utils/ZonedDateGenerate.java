package com.example.ia.mayaAI.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateGenerate {

    public static LocalDateTime generate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toLocalDateTime();
    }
}
