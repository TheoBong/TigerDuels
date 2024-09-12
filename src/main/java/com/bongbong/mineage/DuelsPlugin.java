package com.bongbong.mineage;

import co.aikar.commands.PaperCommandManager;
import com.bongbong.mineage.impl.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class DuelsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        State state = new State(new HashMap<>());
        TaskScheduler scheduler = new TaskScheduler(this, getServer().getScheduler());

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new DuelCommand(state));
        commandManager.registerCommand(new AcceptInviteCommand(scheduler, state));
        commandManager.registerCommand(new CancelDuelCommand(state));
        commandManager.registerCommand(new AcceptDuelCommand(scheduler, state));
        commandManager.registerCommand(new EndDuelCommand(state));
        commandManager.registerCommand(new InviteCommand(state));

        registerListener(new EventListener(state));
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
