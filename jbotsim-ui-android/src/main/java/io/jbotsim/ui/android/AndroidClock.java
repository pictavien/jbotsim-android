package io.jbotsim.ui.android;

import android.os.Handler;
import android.os.Looper;
import io.jbotsim.core.Clock;
import io.jbotsim.core.ClockManager;

import java.util.Timer;
import java.util.TimerTask;

public class AndroidClock extends Clock {
    private int delay = 10;
    private OnClockTask tick = null;
    private Timer timer = new Timer(true);

    public AndroidClock(ClockManager manager) {
        super(manager);
    }

    /**
     * Returns the time unit of the clock, in milliseconds.
     */
    @Override
    public int getTimeUnit() {
        return delay;
    }

    /**
     * Sets the time unit of the clock to the specified value in millisecond.
     *
     * @param delay The desired time unit (1 corresponds to the fastest rate)
     */
    @Override
    public void setTimeUnit(int delay) {
        if (delay != this.delay) {
            pause();
            this.delay = delay;
            start();
        }
    }

    /**
     * Indicates whether the clock is currently running or paused.
     *
     * @return <tt>true</tt> if running, <tt>false</tt> if paused.
     */
    @Override
    public boolean isRunning() {
        return tick != null;
    }

    /**
     * Starts the clock.
     */
    @Override
    public void start() {
        if (isRunning())
            return;
        tick = new OnClockTask();
        timer.schedule(tick, delay, delay);
    }

    /**
     * Pauses the clock.
     */
    @Override
    public void pause() {
        if (! isRunning())
            return;
        tick.cancel();
        tick = null;
        timer.purge();
    }

    /**
     * Resumes the clock if it was paused.
     */
    @Override
    public void resume() {
        start();
    }

    private class OnClockTask extends TimerTask {
        @Override
        public void run() {
            if (System.currentTimeMillis() - scheduledExecutionTime() >= 5)
                return;  // Too late; skip this execution.
            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run() {
                    manager.onClock();
                }
            });

        }
    }
}
