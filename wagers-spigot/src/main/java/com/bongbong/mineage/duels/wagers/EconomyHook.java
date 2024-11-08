package com.bongbong.mineage.duels.wagers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class EconomyHook {
    private static Economy economy;

    static boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }

    public static double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static void addMoney(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static void takeMoney(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

}
