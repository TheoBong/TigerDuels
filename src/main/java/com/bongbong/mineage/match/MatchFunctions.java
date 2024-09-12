package com.bongbong.mineage.match;

import com.bongbong.mineage.kit.KitType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

class MatchFunctions {

    static void hideAllOtherPlayers(Player player, List<MatchPlayer> exceptions) {

    }

    static void showAllOtherPlayers(Player player) {

    }

    static void setupWaiter(MatchPlayer player) {
        player.getPlayer().getInventory().clear();
        player.getPlayer().setFlying(false);
        player.getPlayer().setHealth(20);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        player.getPlayer().setFoodLevel(20);
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

    static void resetPlayer(MatchPlayer player) {

    }

}
