package com.bongbong.mineage.duels.velocity.impl;

import com.velocitypowered.api.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TaskScheduler {
    private final Object plugin;
    private final Scheduler scheduler;

    public void runTask(Runnable runnable) {
        scheduler.buildTask(plugin, runnable).schedule();
    }

    public void runTaskDelay(int delaySeconds, Runnable runnable) {
        scheduler.buildTask(plugin, runnable).delay(delaySeconds, TimeUnit.SECONDS).schedule();
    }

    public void runTaskTimer(Runnable runnable, long delaySeconds, long periodSeconds) {
        scheduler.buildTask(plugin, runnable)
                .delay(delaySeconds, TimeUnit.SECONDS)
                .repeat(periodSeconds, TimeUnit.SECONDS)
                .schedule();
    }

}
