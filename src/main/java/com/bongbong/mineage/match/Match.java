package com.bongbong.mineage.match;

import com.bongbong.mineage.Arena;
import com.bongbong.mineage.EconomyHook;
import com.bongbong.mineage.kit.KitType;
import com.bongbong.mineage.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public abstract class Match {

    static final String WARNING = "Teleporting away/disconnecting will cancel the duel.";
    static final String INVITE = "Invite your teammates using /di <name>!";

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

    public abstract void onStart();
    public abstract void onEnd();

    public void duelInitiated(Player initiator) {
        EconomyHook.takeMoney(initiator, wager);

        MatchPlayer matchPlayer = new MatchPlayer(initiator);
        MatchFunctions.savePlayerInitials(matchPlayer);

        teams.add(new MatchTeam(matchPlayer));

        MatchFunctions.hideAllOtherPlayers(initiator);
        MatchFunctions.showPlayersInMatch(this);
        initiator.teleport(Arena.ARENA_MIDDLE);
        MatchFunctions.strip(initiator);

        initiator.sendMessage(WARNING);
        if (size > 0 ) initiator.sendMessage(INVITE);

        setState(MatchState.WAITING);
    }

    public void duelAccepted(Player acceptor) {
        EconomyHook.takeMoney(acceptor, wager);

        MatchPlayer matchPlayer = new MatchPlayer(acceptor);
        MatchFunctions.savePlayerInitials(matchPlayer);

        for (MatchTeam team : teams)
            if (team.getLeader().equals(matchPlayer)) return;

        teams.add(new MatchTeam(matchPlayer));

        MatchFunctions.strip(acceptor);
        MatchFunctions.hideAllOtherPlayers(acceptor);
        MatchFunctions.showPlayersInMatch(this);
        acceptor.teleport(Arena.ARENA_MIDDLE);

        if (checkReady()) onStart();
        else {
            acceptor.sendMessage(WARNING);
            if (size > 0 ) acceptor.sendMessage(INVITE);
        }
    }

    public UUID generateInvite(MatchTeam team) {
        UUID inviteId = UUID.randomUUID();
        addInvite(inviteId, team);

        return inviteId;
    }

    public void addPlayer(Player player, UUID inviteId) {
        MatchTeam invitedTeam = invites.get(inviteId);

        if (state != MatchState.WAITING) {
            player.sendMessage("The duel is not in the waiting stage anymore.");
            return;
        }

        if (invitedTeam.getFollowers().size() > size) {
            player.sendMessage("The duel you are trying to join is full.");
            return;
        }

        MatchPlayer matchPlayer = new MatchPlayer(player);
        MatchFunctions.savePlayerInitials(matchPlayer);

        invitedTeam.addFollower(matchPlayer);

        MatchFunctions.hideAllOtherPlayers(player);
        MatchFunctions.showPlayersInMatch(this);
        player.teleport(Arena.ARENA_MIDDLE);
        MatchFunctions.strip(player);

        if (checkReady()) {
            onStart();
            return;
        }

        player.sendMessage("Teleporting away/disconnecting will be the same as leaving the duel.");
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

                    MatchFunctions.revertPlayerInitials(matchPlayer);
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

    void cancelDuel() {
        returnWagers();
        sendMessage("Duel cancelled.");
        onEnd();
    }

    void returnWagers() {
        for (MatchTeam team : teams)
            EconomyHook.addMoney(team.getLeader().getPlayer(), wager);
    }

    public void startPlaying() {
        boolean first = true;

        for (MatchTeam team : getTeams()) {
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                Player player = matchPlayer.getPlayer();
                MatchFunctions.strip(player);

                // teleport to arena spawn locations
                player.teleport(first ? Arena.ARENA_SPAWN_1 : Arena.ARENA_SPAWN_2);
                MatchFunctions.giveKit(matchPlayer, kit);
            }
            first = false;
        }

        sendMessage("Teleporting away or disconnecting will automatically forfeit the duel.");
    }

    public void cleanup() {
        for (MatchTeam matchTeam : teams)
            for (MatchPlayer matchPlayer : matchTeam.getAllPlayers()) {
                if (matchPlayer.isDead()) continue;

                MatchFunctions.revertPlayerInitials(matchPlayer);
                MatchFunctions.showAllOtherPlayers(matchPlayer.getPlayer());
            }

        teams.clear();
    }

    public void endMatch() {
        returnWagers();
        sendMessage("Your duel is ended by an admin. Wagers have been refunded and this match is void.");

        onEnd();
    }


    public void playerDied(MatchPlayer matchPlayer) {
        matchPlayer.getPlayer().sendMessage("You died.");
        matchPlayer.setDead(true);

        MatchFunctions.revertPlayerInitials(matchPlayer);

        // warp to top of coliseum and (possibly) spectate the match

        // (possibly) /spectate will require you to be in coliseum region and will hide every player other than
        // the match you are spectating until you (a) leave coliseum or (b) match ends

        checkForWinners();
    }

    public void checkForWinners() {
        MatchTeam aliveTeam = null;
        int alive = 0;
        for (MatchTeam team : this.teams) {
            boolean teamAlive = false;

            for (MatchPlayer matchPlayer : team.getAllPlayers())
                if (!matchPlayer.isDead()) {
                    teamAlive = true;
                    break;
                }

            if (teamAlive) {
                alive++;
                aliveTeam = team;
            }
        }

        if (alive == 0) {
            onEnd();
            return;
        }

        if (alive == 1) {
            EconomyHook.addMoney(aliveTeam.getLeader().getPlayer(), (wager * 2));

            for (MatchPlayer matchPlayer : aliveTeam.getAllPlayers())
                matchPlayer.getPlayer().sendMessage("You won!");

            MatchTeam losingTeam = null;

            for (MatchTeam team : teams)
                if (team != aliveTeam) losingTeam = team;

            assert losingTeam != null;

            for (MatchPlayer matchPlayer : losingTeam.getAllPlayers())
                matchPlayer.getPlayer().sendMessage("You lost!");

            onEnd();
        }
    }

    public MatchPlayer getPlayer(Player player) {
        for (MatchTeam team : getTeams())
            for (MatchPlayer mPlayer : team.getAllPlayers())
                if (mPlayer.getPlayer() == player) return mPlayer;

        return null;
    }

    public List<MatchPlayer> getOpponents(Player player) {
        List<MatchPlayer> list = new ArrayList<>();

        for (MatchTeam team : getTeams())
            for (MatchPlayer mPlayer : team.getAllPlayers())
                if (mPlayer.getPlayer() != player) list.add(mPlayer);

        return list;
    }

    public void sendMessage(String msg) {
        for (MatchTeam team : this.teams)
            for (MatchPlayer matchPlayer : team.getAllPlayers())
                if (!matchPlayer.isDead()) matchPlayer.getPlayer().sendMessage(msg);
    }

    public void sendSound(Sound sound, float vol, float pit) {
        for (MatchTeam team : this.teams)
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                Location location = matchPlayer.getPlayer().getLocation();
                matchPlayer.getPlayer().playSound(location, sound, vol, pit);
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
