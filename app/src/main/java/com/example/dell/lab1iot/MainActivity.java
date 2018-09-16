package com.example.dell.lab1iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.Pwm;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    BoardManager manager = new BoardManager();
    Handler mHandler = new Handler();
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);

        try {
            manager.initGPIO();
            manager.initPwm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exercise4();

    }

    /* Exercise 1 */
    private void exercise1() {
        manager.setIntervalR(manager.INTERVAL_BETWEEN_BLINKS_MS);
        mHandler.post(mBlinkRunnableR);
    }

    private Runnable mBlinkRunnableR = new Runnable() {
        @Override
        public void run() {
            try {
                manager.changeLedStateR();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mBlinkRunnableR, manager.RED_INTERVAL);
        }
    };

    private Runnable mBlinkRunnableG = new Runnable() {
        @Override
        public void run() {
            try {
                manager.changeLedStateG();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mBlinkRunnableG, manager.GREEN_INTERVAL);
        }
    };

    private Runnable mBlinkRunnableB = new Runnable() {
        @Override
        public void run() {
            try {
                manager.changeLedStateB();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mHandler.postDelayed(mBlinkRunnableB, manager.BLUE_INTERVAL);
        }
    };

    /* Exercise 2 */
    private void exercise2() {
        setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS * 2);
        mHandler.post(mBlinkRunnableR);
        mHandler.post(mBlinkRunnableG);
        mHandler.post(mBlinkRunnableB);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.increaseFlag(4);
                setIntervalByFlag(manager.flag);
            }
        });
    }

    private void setIntervalByFlag(int flag){
        switch (flag) {
            case 0: setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS * 2);
            case 1: setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS);
            case 2: setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS / 5);
            case 3: setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS * 4);
            default: setInterval(manager.INTERVAL_BETWEEN_BLINKS_MS * 2);
        }
    }

    private void setInterval(int interval) {
        manager.setIntervalR(interval);
        manager.setIntervalG(interval);
        manager.setIntervalB(interval);
    }

    /* Exercise 4 */
    private void exercise4() {

        mHandler.post(mBlinkRunnable4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFlag();
                colorHandler();
            }
        });

    }

    private Runnable mBlinkRunnable4 = new Runnable() {
        @Override
        public void run() {
            if (manager.mPwm == null) {
                Log.w(TAG, "Stopping runnable since mPwm is null");
                return;
            }
            try {
                reducePwm(manager.mPwm);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, manager.dutyCycle + ": " + manager.mLedStateR + " - " + manager.mLedStateG + " - " + manager.mLedStateB);
            // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
            mHandler.postDelayed(mBlinkRunnable4, manager.INTERVAL_BETWEEN_BLINKS_MS);
        }
    };

    private void changeFlag() {
        if (manager.flag == 4) {
            manager.flag = 0;
        } else {
            manager.flag++;
        }
        Log.d(TAG, "Flag: " + manager.flag);
    }

    private void colorHandler() {
        switch (manager.flag) {
            case 0: {
                manager.mLedStateR = true;
                manager.mLedStateG = false;
                manager.mLedStateB = false;
                break;
            }
            case 1: {
                manager.mLedStateR = false;
                manager.mLedStateG = true;
                manager.mLedStateB = false;
                break;
            }
            case 2: {
                manager.mLedStateR = false;
                manager.mLedStateG = false;
                manager.mLedStateB = true;
                break;
            }
            case 3: {
                manager.mLedStateR = true;
                manager.mLedStateG = true;
                manager.mLedStateB = true;
                break;
            }
            case 4: {
                manager.mLedStateR = false;
                manager.mLedStateG = false;
                manager.mLedStateB = false;
                break;
            }
        }
        try {
            manager.mLedGpioR.setValue(!manager.mLedStateR);
            manager.mLedGpioB.setValue(!manager.mLedStateB);
            manager.mLedGpioG.setValue(!manager.mLedStateG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reducePwm(Pwm pwm) throws IOException {
        manager.dutyCycle -= 5;
        if (manager.dutyCycle == 0) manager.dutyCycle = 50;
        pwm.setPwmDutyCycle(manager.dutyCycle);
    }

    /* ----------------------------- */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending Runnable from the handler.
        mHandler.removeCallbacks(mBlinkRunnable4);
        // Close the PWM port.
        Log.i(TAG, "Closing port");
        try {
            manager.mLedGpioB.close();
            manager.mLedGpioR.close();
            manager.mLedGpioG.close();
            manager.mPwm.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            manager.mLedGpioB = null;
            manager.mLedGpioR = null;
            manager.mLedGpioG = null;
            manager.mPwm = null;
        }
    }
}