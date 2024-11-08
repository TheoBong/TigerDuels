package com.bongbong.mineage.duels.velocity.match;

import com.bongbong.mineage.duels.proto.*;
import com.bongbong.mineage.duels.shared.Kit;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class MatchFunctions {
    static boolean postPlayerJoin(UUID leader, List<UUID> teammates, List<UUID> enemies,
                                  UUID player, Channel channel) {

        MatchServiceGrpc.MatchServiceBlockingStub blockingStub = MatchServiceGrpc.newBlockingStub(channel);

        PlayerJoinRequest request = PlayerJoinRequest.newBuilder()
                .setLeader(leader.toString())
                .setTeammates(getStringFromUUIDList(teammates))
                .setEnemies(getStringFromUUIDList(enemies))
                .setPlayer(player.toString())
                .build();

        SuccessResponse response;
        try {
            response = blockingStub.postPlayerJoin(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(e);
        }

        return response.getSuccess();
    }

    static boolean postMatchStart(List<UUID> team1, List<UUID> team2, Kit.KitType kit, Channel channel) {

        MatchServiceGrpc.MatchServiceBlockingStub blockingStub = MatchServiceGrpc.newBlockingStub(channel);

        MatchStartRequest request = MatchStartRequest.newBuilder()
                .setPlayersTeam1(getStringFromUUIDList(team1))
                .setPlayersTeam2(getStringFromUUIDList(team2))
                .setKit(kit.name())
                .build();

        SuccessResponse response;
        try {
            response = blockingStub.postMatchStart(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(e);
        }

        return response.getSuccess();
    }

    private static String getStringFromUUIDList(List<UUID> list) {
        List<String> list2 = new ArrayList<>();
        for (UUID uuid : list) list2.add(uuid.toString());
        return String.join(",", list2);
    }
}
