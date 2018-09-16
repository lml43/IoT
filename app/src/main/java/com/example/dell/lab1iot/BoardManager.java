package com.example.dell.lab1iot;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

import static android.content.ContentValues.TAG;

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
    int RED_INTERVAL;
    int GREEN_INTERVAL;
    int BLUE_INTERVAL;
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

    void changeLedStateR() throws IOException {
        mLedStateR = !mLedStateR;
        mLedGpioR.setValue(mLedStateR);
    }

    void changeLedStateG() throws IOException {
        mLedStateG = !mLedStateG;
        mLedGpioG.setValue(mLedStateG);
    }

    void changeLedStateB() throws IOException {
        mLedStateB = !mLedStateB;
        mLedGpioB.setValue(mLedStateB);
    }

    void setIntervalR(int interval) {
        RED_INTERVAL = interval;
    }

    void setIntervalG(int interval) {
        GREEN_INTERVAL = interval;
    }

    void setIntervalB(int interval) {
        BLUE_INTERVAL = interval;
    }

    void increaseFlag(int max) {
        if (flag == max - 1)
            flag = 0;
        else
            flag++;
        Log.i(TAG, " ---" + flag);
    }
}
