package com.example.ia.mayaAI.utils;

import org.apache.commons.lang3.StringUtils;

public class TextCleaner {
    public static String cleanText(String text) {
        text = StringUtils.normalizeSpace(text);
        text = StringUtils.stripAccents(text);
        return text;
    }
}
