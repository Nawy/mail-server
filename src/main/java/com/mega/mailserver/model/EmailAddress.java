package com.mega.mailserver.model;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Objects;

public class EmailAddress {
    private String name;
    private String domain;
    private InternetAddress address;

    public EmailAddress(final String name, final String domain) throws AddressException {
        this.name = name;
        this.domain = domain;
        this.address = new InternetAddress(name + "@" + domain);
    }

    public EmailAddress(final String address) throws AddressException {
        String[] parts = Objects.requireNonNull(address).split("@");
        if(parts.length != 2) {
            throw new IllegalArgumentException("Wrong adress " + address);
        }
        this.name = parts[0];
        this.domain = parts[1];

        this.address = new InternetAddress(address);
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public InternetAddress getAddress() {
        return address;
    }
}
