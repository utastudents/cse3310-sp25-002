package uta.cse3310.PairUp;

import java.util.concurrent.*;

/*
    The MatchmakingScheduler class will handle 
    asynchronous pinging of the matching class, to
    ensure no player gets stuck in the matchmaking
    process as long as another player is in the queue
*/
public class MatchmakingScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final PairUp pairUp;

    public MatchmakingScheduler() {
        pairUp = new PairUp();
    }

    public void start() {
        // Task that will be ran asynchronously
        Runnable task = () -> {
            pairUp.ping();
        };

        // Task scheduled to start after 30 seconds, and every 60 seconds after that
        scheduler.scheduleAtFixedRate(task, 30, 60, TimeUnit.SECONDS);
    }
}