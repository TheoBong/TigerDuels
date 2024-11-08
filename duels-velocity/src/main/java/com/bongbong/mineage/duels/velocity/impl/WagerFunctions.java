package com.bongbong.mineage.duels.velocity.impl;

import com.bongbong.mineage.duels.proto.*;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.UUID;

class WagerFunctions {
    static double getBalance(UUID player, Channel channel) {
        MoneyServiceGrpc.MoneyServiceBlockingStub blockingStub = MoneyServiceGrpc.newBlockingStub(channel);

        BalanceRequest request = BalanceRequest.newBuilder()
                .setUuid(player.toString())
                .build();

        BalanceReply response;
        try {
            response = blockingStub.getBalance(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(e);
        }

        return response.getBalance();
    }

    static boolean postWager(UUID winner, UUID loser, double wager, Channel channel) {
        MoneyServiceGrpc.MoneyServiceBlockingStub blockingStub = MoneyServiceGrpc.newBlockingStub(channel);

        WagerRequest request = WagerRequest.newBuilder()
                .setLoserUuid(loser.toString())
                .setWinnerUuid(winner.toString())
                .setWager(wager)
                .build();

        WagerReply response;
        try {
            response = blockingStub.postWager(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException(e);
        }

        return response.getSuccess();
    }
}
