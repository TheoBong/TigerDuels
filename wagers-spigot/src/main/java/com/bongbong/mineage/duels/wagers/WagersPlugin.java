package com.bongbong.mineage.duels.wagers;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public class WagersPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!EconomyHook.setupEconomy()) {
            getLogger().log(Level.SEVERE, "Economy is not working (vault)! Wagers will error");
            onDisable();
        }

        try {
            new RPCHandler(getConfig().getInt("GRPC_PORT"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
