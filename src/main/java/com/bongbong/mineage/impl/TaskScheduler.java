package com.bongbong.mineage.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

@RequiredArgsConstructor
public class TaskScheduler {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public void runTask(BukkitRunnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }

    public void runTaskDelay(int seconds, Runnable runnable) {
        scheduler.runTaskLater(plugin, runnable, seconds / 20);
    }

    public void runTaskTimer(Runnable runnable, long delay, long period) {
        scheduler.runTaskTimer(plugin, runnable, delay, period);
    }

}
