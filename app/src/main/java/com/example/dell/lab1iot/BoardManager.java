package com.example.dell.lab1iot;

import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Created by dell on 9/15/2018.
 */

public class BoardManager {
    PeripheralManager manager = PeripheralManager.getInstance();
    Handler mHandler = new Handler();
    boolean mLedStateR = false;
    boolean mLedStateG = false;
    boolean mLedStateB = false;
    int[] arr = {1, 2, 3, 4, 5};
    int flag = 3;
    int dutyCycle = 50;
    int INTERVAL_BETWEEN_BLINKS_MS = 500;
    Gpio mLedGpioR = null;
    Gpio mLedGpioG = null;
    Gpio mLedGpioB = null;
    Pwm mPwm = null;

    void initGPIO() throws IOException {
        mLedGpioR = manager.openGpio(BoardDefaults.getGPIOForLED("Red"));
        mLedGpioR.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        mLedGpioG = manager.openGpio(BoardDefaults.getGPIOForLED("Green"));
        mLedGpioG.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

        mLedGpioB = manager.openGpio(BoardDefaults.getGPIOForLED("Blue"));
        mLedGpioB.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
    }

    void initPwm() throws IOException {
        mPwm = manager.openPwm(BoardDefaults.getPWM(0));
        mPwm.setPwmFrequencyHz(120);
        mPwm.setPwmDutyCycle(dutyCycle);
        mPwm.setEnabled(true);
    }
}
