package com.bongbong.mineage.match;

import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.utils.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

class MatchFunctions {

    static void hideAllOtherPlayers(Player player, List<MatchPlayer> exceptions) {

    }

    static void showAllOtherPlayers(Player player) {

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

    static void setupPlayer(MatchPlayer matchPlayer) {
        matchPlayer.setDead(false);

        if (matchPlayer.isDisconnected()) return;

        resetPlayer(matchPlayer);
    }

    static void giveKit(MatchPlayer matchPlayer, KitType kit) {
        Player player = matchPlayer.getPlayer();

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
    }

    static void resetPlayer(MatchPlayer matchPlayer) {
        revertPlayerInitials(matchPlayer);
    }

    static void savePlayerInitials(MatchPlayer matchPlayer) {
        Player player = matchPlayer.getPlayer();

        matchPlayer.setInitialLocation(player.getLocation());
        matchPlayer.setInitialInventory(InventoryUtil.serializeInventory(player.getInventory().getContents()));
        matchPlayer.setInitialArmor(InventoryUtil.serializeInventory(player.getInventory().getArmorContents()));

        matchPlayer.setInitialFly(player.isFlying());
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

        player.setFlying(matchPlayer.isInitialFly());
        player.setFoodLevel(matchPlayer.getInitialFood());
        player.setGameMode(matchPlayer.getInitialGamemode());
        player.setHealth(matchPlayer.getInitialHealth());
        player.addPotionEffects(matchPlayer.getInitialPotionEffects());
        player.setFireTicks(0);

        player.teleport(matchPlayer.getInitialLocation());
    }
}
