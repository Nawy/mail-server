package com.mega.mailserver.util;

import java.util.regex.Pattern;

public abstract class EmailUtils {

    private static final Pattern PATTERN = Pattern.compile("^\\w+([\\.-]?\\w+)*$");
    private static final Pattern FULL_EMAIL_PATTERN = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$");

    public static boolean isValidEmailName(String name) {
        return PATTERN.matcher(name).matches();
    }

    public static boolean isValidEmail(String email) {
        return FULL_EMAIL_PATTERN.matcher(email).matches();
    }
}
