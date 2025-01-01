package pl.csanecki.memory.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MillisTimer {

    private final MillisRefreshment millisRefreshment;
    private final Collection<MillisTimerSubscriber> subscribers = new ArrayList<>();

    private ScheduledExecutorService timer;
    private long passedMillis;

    private MillisTimer(MillisRefreshment millisRefreshment) {
        this.millisRefreshment = millisRefreshment;
    }

    public static MillisTimer ofTenMilliseconds() {
        return new MillisTimer(MillisRefreshment.Ten);
    }

    public static MillisTimer ofOneHundredMilliseconds() {
        return new MillisTimer(MillisRefreshment.OneHundred);
    }

    public static MillisTimer ofOneThousandMilliseconds() {
        return new MillisTimer(MillisRefreshment.OneThousand);
    }

    public void registerSubscriber(MillisTimerSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    public void start() {
        passedMillis = 0;
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(() -> {
            passedMillis += millisRefreshment.millis;
            subscribers.forEach(millisTimerSubscriber -> millisTimerSubscriber.update(getResultAsMilliseconds(), millisRefreshment));
        }, 0, millisRefreshment.millis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (timer != null) {
            timer.shutdown();
        }
    }

    public Duration getResultAsMilliseconds() {
        return Duration.ofMillis(passedMillis);
    }

}
