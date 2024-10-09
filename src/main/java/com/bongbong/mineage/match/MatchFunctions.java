package com.bongbong.mineage.match;

import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

class MatchFunctions {

    static void showPlayersInMatch(Match match) {
        for (MatchTeam team : match.getTeams())
            for (MatchPlayer player : team.getAllPlayers())

                for (MatchTeam team1 : match.getTeams())
                    for (MatchPlayer player1 : team1.getAllPlayers()) {
                        player.getPlayer().showPlayer(player1.getPlayer());
                        player1.getPlayer().showPlayer(player.getPlayer());
                    }
    }

    static void hideAllOtherPlayers(Player player) {
        for (Player otherPlayer : Bukkit.getServer().getOnlinePlayers()) {

            player.hidePlayer(otherPlayer);
            otherPlayer.hidePlayer(player);
        }
    }

    static void showAllOtherPlayers(Player player) {
        Bukkit.getServer().getOnlinePlayers().forEach(otherPlayer -> {
            player.showPlayer(otherPlayer);
            otherPlayer.showPlayer(player);
        });
    }

    static void strip(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFlying(false);
        player.setHealth(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);

        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    static void giveKit(MatchPlayer matchPlayer, KitType kit) {
        Player player = matchPlayer.getPlayer();

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
    }

    static void savePlayerInitials(MatchPlayer matchPlayer) {
        Player player = matchPlayer.getPlayer();

        matchPlayer.setInitialLocation(player.getLocation());
        matchPlayer.setInitialInventory(InventoryUtil.serializeInventory(player.getInventory().getContents()));
        matchPlayer.setInitialArmor(InventoryUtil.serializeInventory(player.getInventory().getArmorContents()));

        matchPlayer.setInitialFood(player.getFoodLevel());
        matchPlayer.setInitialGamemode(player.getGameMode());
        matchPlayer.setInitialHealth(player.getHealth());
        matchPlayer.setInitialPotionEffects(player.getActivePotionEffects());
    }


    static void revertPlayerInitials(MatchPlayer matchPlayer) {
        Player player = matchPlayer.getPlayer();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setArmorContents(InventoryUtil.deserializeInventory(matchPlayer.getInitialArmor()));
        player.getInventory().setContents(InventoryUtil.deserializeInventory(matchPlayer.getInitialInventory()));

        player.setFoodLevel(matchPlayer.getInitialFood());
        player.setGameMode(matchPlayer.getInitialGamemode());
        player.setHealth(matchPlayer.getInitialHealth());
        player.addPotionEffects(matchPlayer.getInitialPotionEffects());
        player.setFireTicks(0);

        player.teleport(matchPlayer.getInitialLocation());
    }
}
