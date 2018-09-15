package com.example.dell.lab1iot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
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
public class Exercise4 extends Activity {

    BoardManager manager = new BoardManager();
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise4);
        try {
            manager.initGPIO();
            manager.initPwm();
        } catch (IOException e) {
            Log.w(TAG, "Unable to access PWM", e);
        }
        Button button = findViewById(R.id.button);
        mHandler.post(mBlinkRunnable);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFlag();
                colorHandler();
            }
        });

        Log.i(TAG, "Start blinking LED GPIO pin");

    }



    private Runnable mBlinkRunnable = new Runnable() {
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
            Log.d(TAG, manager.dutyCycle + ": " + manager.mLedStateR + " - " + manager.mLedStateG + " - " + manager.mLedStateB);
            // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
            mHandler.postDelayed(mBlinkRunnable, manager.INTERVAL_BETWEEN_BLINKS_MS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending Runnable from the handler.
        mHandler.removeCallbacks(mBlinkRunnable);
        // Close the PWM port.
        Log.i(TAG, "Closing port");
        try {
            manager.mPwm.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            manager.mPwm = null;
        }
    }
}
