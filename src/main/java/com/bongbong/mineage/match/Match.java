package com.bongbong.mineage.match;

import com.bongbong.mineage.Arena;
import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Match {
    private final UUID id;
    private final KitType kit;
    private final int wager, size;
    private final List<MatchTeam> teams = new ArrayList<>();
    private final Map<UUID, MatchTeam> invites = new HashMap<>();
    private long duration;
    private @Setter MatchState state;

    public Match(KitType kit, int wager, int size) {
        this.id = UUID.randomUUID();
        this.kit = kit;
        this.wager = wager;
        this.size = size;
    }

    public void duelInitiated(Player initiator) {
        MatchPlayer matchPlayer = new MatchPlayer(initiator);

        teams.add(new MatchTeam(matchPlayer));
        initiator.teleport(Arena.ARENA_MIDDLE);
        MatchFunctions.setupWaiter(matchPlayer);

        initiator.sendMessage("Teleporting away/disconnecting will cancel the duel.");

        setState(MatchState.WAITING);
    }

    public boolean duelAccepted(Player acceptor) {
        MatchPlayer matchPlayer = new MatchPlayer(acceptor);

        teams.add(new MatchTeam(matchPlayer));

        if (size < 0) {
            acceptor.teleport(Arena.ARENA_SPAWN_2);
            return true;
        } else {
            acceptor.teleport(Arena.ARENA_MIDDLE);
            MatchFunctions.setupWaiter(matchPlayer);
            acceptor.sendMessage("Teleporting away/disconnecting will cancel the duel.");
            return false;
        }
    }

    public UUID generateInvite(MatchTeam team) {
        UUID inviteId = UUID.randomUUID();
        addInvite(inviteId, team);

        return inviteId;
    }

    public boolean addPlayer(Player player, UUID inviteId) {
        MatchTeam invitedTeam = invites.get(inviteId);

        if (invitedTeam.getFollowers().size() > size) {
            player.sendMessage("The duel you are trying to join is full.");
            return false;
        }

        MatchPlayer matchPlayer = new MatchPlayer(player);
        invitedTeam.addFollower(matchPlayer);

        if (checkReady()) {
            return true;
        } else {
            player.teleport(Arena.ARENA_MIDDLE);
            MatchFunctions.setupWaiter(matchPlayer);
            player.sendMessage("Teleporting away/disconnecting will be the same as leaving the duel.");
            return false;
        }
    }

    public void leave(Player player) {
        for (MatchTeam team : teams) {
            if (team.getLeader().getPlayer() == player) {
                cancelDuel();

                player.sendMessage("You cancelled the duel in the waiting stage.");
                return;
            } else {
                for (MatchPlayer matchPlayer : team.getFollowers()) {
                    if (matchPlayer.getPlayer() == player) team.removeFollower(matchPlayer);
                    // hopefully no concurrent modification exception

                    MatchFunctions.resetPlayer(matchPlayer);
                    player.sendMessage("You left the duel in the waiting stage.");
                    return;
                }
            }
        }
    }

    public void forfeit(Player player) {
        sendMessage(player.getName() + " forfeited.");
        playerDied(getPlayer(player));
    }

    public MatchTeam getTeam(Player player) {
        for (MatchTeam team : teams) {
            for (MatchPlayer player1 : team.getAllPlayers())
                if (player1.getPlayer() == player) return team;
        }

        return null;
    }

    boolean checkReady() {
        boolean ready = true;

        for (MatchTeam team : teams) {
            if (team.getFollowers().size() != size) {
                ready = false;
                break;
            }
        }

        return ready;
    }

    public void cancelDuel() {
        sendMessage("Duel cancelled.");
        cleanup();
    }

    public void startPlaying() {
        boolean first = true;

        for (MatchTeam team : teams) {
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                Player player = matchPlayer.getPlayer();

                // teleport to arena spawn locations
                player.teleport(first ? Arena.ARENA_SPAWN_1 : Arena.ARENA_SPAWN_2);
                MatchFunctions.giveKit(matchPlayer, kit);
            }
            first = false;
        }

        sendMessage("Teleporting away or disconnecting will automatically forfeit the duel.");
    }

    public void cleanup() {
        for (MatchTeam matchTeam : teams) {
            for (MatchPlayer matchPlayer : matchTeam.getAllPlayers()) {
                if (matchPlayer.isDead()) continue;

                MatchFunctions.resetPlayer(matchPlayer);
                MatchFunctions.showAllOtherPlayers(matchPlayer.getPlayer());
            }
        }

        teams.clear();
    }

    public void endMatch() {
        sendMessage("Your duel is ended by an admin. Wagers have been refunded and this match is void.");

        for (MatchTeam team : teams) {
            // give back both team leader wagers
        }

        cleanup();
    }


    public void playerDied(MatchPlayer matchPlayer) {
        matchPlayer.getPlayer().sendMessage("You died.");
        matchPlayer.setDead(true);

        MatchFunctions.resetPlayer(matchPlayer);
        // warp to top of coliseum and (possibly) spectate the match

        // (possibly) /spectate will require you to be in coliseum region and will hide every player other than
        // the match you are spectating until you (a) leave coliseum or (b) match ends

        this.checkForWinners();
    }

    public void checkForWinners() {

        MatchTeam aliveTeam = null;
        int alive = 0;
        for (MatchTeam team : this.teams) {
            boolean teamAlive = false;
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                if (!matchPlayer.isDead()) teamAlive = true;
            }

            if (teamAlive) {
                alive++;
                aliveTeam = team;
            }
        }

        if (alive == 1) {
            for (MatchPlayer matchPlayer : aliveTeam.getAllPlayers()) {

                if (wager != 0) {
                }
                // logic for giving money to leader

                matchPlayer.getPlayer().sendMessage("You won!");
            }

            MatchTeam losingTeam = null;

            for (MatchTeam team : teams)
                if (team != aliveTeam) losingTeam = team;

            assert losingTeam != null;

            for (MatchPlayer matchPlayer : losingTeam.getAllPlayers()) {

                if (wager != 0) {
                }
                // logic for giving money to leader

                matchPlayer.getPlayer().sendMessage("You lost!");
            }

            cleanup();
        }
    }

    public MatchPlayer getPlayer(Player player) {
        for (MatchTeam team : getTeams()) {
            for (MatchPlayer mPlayer : team.getAllPlayers())
                if (mPlayer.getPlayer() == player) return mPlayer;
        }
        return null;
    }

    public List<MatchPlayer> getOpponents(Player player) {
        List<MatchPlayer> list = new ArrayList<>();

        for (MatchTeam team : getTeams()) {
            for (MatchPlayer mPlayer : team.getAllPlayers()) {
                if (mPlayer.getPlayer() != player) list.add(mPlayer);
            }
        }

        return list;
    }

    public void sendMessage(String msg) {
        for (MatchTeam team : this.teams) {
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                if (!matchPlayer.isDead()) matchPlayer.getPlayer().sendMessage(msg);
            }
        }
    }

    public void sendSound(Sound sound, float vol, float pit) {
        for (MatchTeam team : this.teams) {
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                Location location = matchPlayer.getPlayer().getLocation();
                matchPlayer.getPlayer().playSound(location, sound, vol, pit);
            }
        }
    }

    public String getDuration() {
        switch (this.state) {
            case STARTING:
                return "00:00";
            case ENDING:
                return "Ending";
            case PLAYING:
                return TimeUtil.formatTimeMillisToClock(System.currentTimeMillis() - this.duration);
            default:
                return null;
        }
    }

    void addInvite(UUID inviteId, MatchTeam team) {
        invites.put(inviteId, team);
    }

    // TODO: Code feature to cancel invites
}
