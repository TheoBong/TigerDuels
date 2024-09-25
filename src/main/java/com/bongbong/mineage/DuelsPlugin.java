package com.bongbong.mineage;

import co.aikar.commands.PaperCommandManager;
import com.bongbong.mineage.impl.*;
import com.bongbong.mineage.kit.KitType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public class DuelsPlugin extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "duels.admin";

    @Override
    public void onEnable() {
        State state = new State(new HashMap<>());
        TaskScheduler scheduler = new TaskScheduler(this, getServer().getScheduler());

        KitType.init();

        if (!EconomyHook.setupEconomy()) getLogger().log(
                Level.WARNING, "Economy is not working (vault)! Wagers will error");

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new DuelCommand(state, scheduler));
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
