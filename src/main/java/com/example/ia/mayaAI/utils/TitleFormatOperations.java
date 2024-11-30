package com.example.ia.mayaAI.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleFormatOperations {

    public static String extractTitleBlock(String message) {
        String regex = "<([\\s\\S]*?)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    public static String clearTitleBlock(String message) {
        String regex = "<([\\s\\S]*?)>|\"\"";
        return message.replaceAll(regex, "").trim().replaceAll("[\"\\n\\r]+$", "");
    }
}
