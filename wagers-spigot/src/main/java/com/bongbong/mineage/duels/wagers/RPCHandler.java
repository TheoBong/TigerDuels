package com.bongbong.mineage.duels.wagers;

import com.bongbong.mineage.duels.proto.*;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.UUID;

public class RPCHandler {
    RPCHandler(int port) throws IOException {
        ServerBuilder.forPort(port)
                .addService(new WagerHandler())
                .addService(new BalanceHandler())
                .build().start();
    }

    static class WagerHandler extends MoneyServiceGrpc.MoneyServiceImplBase {
        @Override
        public void postWager(WagerRequest req, StreamObserver<WagerReply> responseObserver) {
            UUID winner = UUID.fromString(req.getWinnerUuid());
            UUID loser = UUID.fromString(req.getLoserUuid());

            EconomyHook.addMoney(Bukkit.getOfflinePlayer(winner), req.getWager());
            EconomyHook.takeMoney(Bukkit.getOfflinePlayer(loser), req.getWager());

            WagerReply reply = WagerReply.newBuilder().setSuccess(true).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    static class BalanceHandler extends MoneyServiceGrpc.MoneyServiceImplBase {
        @Override
        public void getBalance(BalanceRequest req, StreamObserver<BalanceReply> responseObserver) {
            UUID player = UUID.fromString(req.getUuid());

            double balance = EconomyHook.getBalance(Bukkit.getOfflinePlayer(player));
            BalanceReply reply = BalanceReply.newBuilder().setBalance(balance).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

}

