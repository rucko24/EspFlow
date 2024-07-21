package com.esp.espflow.data.service.esptoolservice;

public final class WelcomeUtil {

    public static String generateWelcome(String name) {
        return String.format("Welcome %s", name);
    }

    private WelcomeUtil() {
    }
}