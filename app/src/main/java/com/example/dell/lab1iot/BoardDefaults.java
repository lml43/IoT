package com.example.dell.lab1iot;

/**
 * Created by dell on 8/29/2018.
 */

public class BoardDefaults {

    public static String getGPIOForLED(String ledColor) {
        switch (ledColor) {
            case "Red":
                return "BCM26";
            case "Green":
                return "BCM19";
            case "Blue":
                return "BCM11";
            default:
                throw new IllegalStateException("Unknown led color:" + ledColor);
        }
    }

    public static String getPWM(int port) {
        if (port == 1)
            return "PWM1";
        else
            return "PWM0";
    }

}