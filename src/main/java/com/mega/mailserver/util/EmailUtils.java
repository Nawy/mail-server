package com.mega.mailserver.util;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public abstract class EmailUtils {

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9]([a-z0-9]|\\.)*[a-z0-9]$");

    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    public static boolean isValidEmailName(String name) {
        return PATTERN.matcher(name).matches() && emailValidator.isValid(name + "@mail.ru");
    }

    public static boolean isValidEmail(String email) {
        String emailName = email.split("@")[0];
        return isValidEmailName(emailName) && emailValidator.isValid(email);
    }
}
