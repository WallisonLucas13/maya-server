package com.example.ia.mayaAI.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    public static List<String> extractLinks(String message) {
        String urlPattern = "(?i)\\bhttps?://[\\w\\-.~:/?#\\[\\]@!$&'()*+,;=%]+\\b";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        List<String> links = new ArrayList<>();
        while (matcher.find()) {
            links.add(matcher.group());
        }
        return links;
    }
}
