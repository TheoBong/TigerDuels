package com.bongbong.mineage.duels.velocity.impl;

import com.bongbong.mineage.duels.velocity.match.Match;
import com.bongbong.mineage.duels.velocity.match.MatchPlayer;
import com.bongbong.mineage.duels.velocity.match.MatchTeam;
import lombok.RequiredArgsConstructor;
import com.velocitypowered.api.proxy.Player;

import java.util.*;


// This is essentially "global state" which is bad
// Not particularly scalable but getting rid of would require deriving commands from matches.
// I don't particularly like this, but it is what it is.

// this state needs to remain protected and contained
@RequiredArgsConstructor
public class State {
    private final Map<UUID, Match> globalMatches;

    void addMatch(Match match) {
        globalMatches.put(match.getId(), match);
    }

    void removeMatch(UUID mapid) {
        globalMatches.remove(mapid);
    }

    Collection<Match> getMatches() {
        return globalMatches.values();
    }

    Match getMatch(UUID mapid) {
        return globalMatches.get(mapid);
    }

    Match getMatch(Player player) {
        for (Match match : getMatches())
            for (MatchTeam team : match.getTeams())
                for (MatchPlayer matchPlayer : team.getAllPlayers())
                    if (matchPlayer.getPlayer() == player) return match;

        return null;
    }

    Match getMatchFromInvite(UUID inviteId) {
        for (Match match : getMatches())
            if (match.getInvites().get(inviteId) != null) return match;

        return null;
    }

    List<MatchPlayer> getPlayersInMatch() {
        List<MatchPlayer> players = new ArrayList<>();

        for (Match match : getMatches())
            for (MatchTeam team : match.getTeams())
                players.addAll(team.getAllPlayers());

        return players;
    }

    public void shutdown() {
        for (Match match : getMatches()) match.endMatch();
    }
}
