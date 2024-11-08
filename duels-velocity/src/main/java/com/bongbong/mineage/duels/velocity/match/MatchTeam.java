package com.bongbong.mineage.duels.velocity.match;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class MatchTeam {
    private final MatchPlayer leader;
    private List<MatchPlayer> followers = new ArrayList<>();

    public List<MatchPlayer> getAllPlayers() {
        List<MatchPlayer> list = new ArrayList<>();
        list.add(getLeader());
        list.addAll(getFollowers());
        return list;
    }

    void addFollower(MatchPlayer player) {
        followers.add(player);
    }

    void removeFollower(MatchPlayer player) {
        followers.remove(player);
    }
}
