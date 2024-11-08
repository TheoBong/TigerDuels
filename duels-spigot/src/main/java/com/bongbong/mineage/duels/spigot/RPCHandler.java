package com.bongbong.mineage.duels.spigot;

import com.bongbong.mineage.duels.proto.*;
import com.bongbong.mineage.duels.shared.Kit;
import com.bongbong.mineage.duels.spigot.utils.InventoryUtil;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RPCHandler {
    RPCHandler(int port) throws IOException {

        Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new PlayerJoinHandler())
                .addService(new MatchStartHandler())
                .build().start();
    }

    static boolean postDeath(Player player, Channel channel, Logger logger) {
        MatchServiceGrpc.MatchServiceBlockingStub blockingStub = MatchServiceGrpc.newBlockingStub(channel);

        PlayerRequest request = PlayerRequest.newBuilder()
                .setPlayer(player.getUniqueId().toString())
                .build();

        SuccessResponse response;
        try {
            response = blockingStub.postPlayerDie(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }

        return response.getSuccess();
    }

    static class PlayerJoinHandler extends MatchServiceGrpc.MatchServiceImplBase {
        @Override
        public void postPlayerJoin(PlayerJoinRequest req, StreamObserver<SuccessResponse> responseObserver) {
            UUID leader = UUID.fromString(req.getLeader());
            Player player = Bukkit.getPlayer(UUID.fromString(req.getPlayer()));
            List<Player> enemies = playersFromString(req.getEnemies());
            List<Player> teammates = playersFromString(req.getTeammates());

            hideAllPlayers(player);
            showPlayers(player, teammates, enemies);

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

            if (leader == player.getUniqueId())
                scoreboard.registerNewTeam(leader.toString()).setAllowFriendlyFire(false);

            Team team = scoreboard.getTeam(leader.toString());
            team.addPlayer(player);

            player.teleport(Arena.ARENA_MIDDLE);
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.getActivePotionEffects().clear();

            SuccessResponse reply = SuccessResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    static class MatchStartHandler extends MatchServiceGrpc.MatchServiceImplBase {
        @Override
        public void postMatchStart(MatchStartRequest req, StreamObserver<SuccessResponse> responseObserver) {
            List<Player> team1 = playersFromString(req.getPlayersTeam1());
            List<Player> team2 = playersFromString(req.getPlayersTeam2());
            Kit.KitType kit = Kit.KitType.getKitByName(req.getKit());
            assert kit != null;

            for (Player player : team1) {
                setInventory(kit, player);
                player.teleport(Arena.ARENA_SPAWN_1);
            }

            for (Player player : team2) {
                setInventory(kit, player);
                player.teleport(Arena.ARENA_SPAWN_2);
            }

            SuccessResponse reply = SuccessResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    static List<Player> playersFromString(String string) {
        List<Player> players = new ArrayList<>();
        for (String player : string.split(","))
            players.add(Bukkit.getPlayer(UUID.fromString(player)));

        return players;
    }

    static void hideAllPlayers(Player player) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            other.hidePlayer(player);
            player.hidePlayer(other);
        }
    }

    static void showPlayers(Player player, List<Player> teammates, List<Player> enemies) {
        for (Player teammate : teammates) {
            teammate.showPlayer(player);
            player.showPlayer(teammate);
        }

        for (Player enemy : enemies) {
            enemy.showPlayer(player);
            player.showPlayer(enemy);
        }
    }

    static void setInventory(Kit.KitType kit, Player player) {
        player.getInventory().setArmorContents(InventoryUtil.deserializeInventory(kit.getArmor()));
        player.getInventory().setContents(InventoryUtil.deserializeInventory(kit.getInventory()));
    }
}

