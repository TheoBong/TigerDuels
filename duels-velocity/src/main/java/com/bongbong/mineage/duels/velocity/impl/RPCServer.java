package com.bongbong.mineage.duels.velocity.impl;

import com.bongbong.mineage.duels.proto.*;
import com.bongbong.mineage.duels.velocity.match.MatchPlayer;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.UUID;

public class RPCServer {

    public RPCServer(State state, int port) throws IOException {
        ServerBuilder.forPort(port)
                .addService(new PlayerDeathHandler(state))
                .build().start();
    }

    @RequiredArgsConstructor
    static class PlayerDeathHandler extends MatchServiceGrpc.MatchServiceImplBase {
        private final State state;

        @Override
        public void postPlayerDie(PlayerRequest req, StreamObserver<SuccessResponse> responseObserver) {
            UUID playerUuid = UUID.fromString(req.getPlayer());

            for (MatchPlayer matchPlayer : state.getPlayersInMatch())
                if (matchPlayer.getPlayer().getUniqueId() == playerUuid)
                    state.getMatch(matchPlayer.getPlayer()).playerDied(matchPlayer);

            SuccessResponse reply = SuccessResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
