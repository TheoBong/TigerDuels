package com.bongbong.mineage.duels.velocity.match;

import com.bongbong.mineage.duels.shared.Kit;
import com.bongbong.mineage.duels.velocity.DuelsVelocity;
import com.bongbong.mineage.duels.velocity.utils.Colors;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;
import io.grpc.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

@Getter
@RequiredArgsConstructor
public abstract class Match {

    static final Component
            WARNING = Colors.get("Teleporting away/disconnecting will cancel the duel."),
            WARNING_FOLLOWER = Colors.get("Teleporting away/disconnecting will be the same as leaving the duel."),
            WARNING_FORFEIT = Colors.get("Teleporting away or disconnecting will automatically forfeit the duel."),
            INVITE = Colors.get("Invite your teammates using /di <name>!"),
            YOU_CANCELLED = Colors.get("You cancelled the duel in the waiting stage."),
            CANCELLED = Colors.get("Duel cancelled."),
            LEFT = Colors.get("You left the duel in the waiting stage."),
            DIED = Colors.get("You died."),
            WON = Colors.get("You won."),
            LOST = Colors.get("You lost."),
            ADMIN_CANCEL = Colors.get("Your match was cancelled by an admin."),
            FULL = Colors.get("The duel you are trying to join is full."),
            STARTED = Colors.get("The duel is not in the waiting stage anymore.");

    private final UUID id;
    private final Kit.KitType kit;
    private final int wager, size;
    private final List<MatchTeam> teams = new ArrayList<>();
    private final Map<UUID, MatchTeam> invites = new HashMap<>();
    private final Channel channel, wagerChannel;
    private final RegisteredServer duelServer;
    private @Setter MatchState state;

    public abstract void executeWagers(UUID winner, UUID loser);
    public abstract void onStart();
    public abstract void onEnd();

    public void duelInitiated(Player initiator, RegisteredServer initialServer) {
        MatchPlayer matchPlayer = new MatchPlayer(initiator, initialServer);

        teams.add(new MatchTeam(matchPlayer));

        matchPlayer.getPlayer().createConnectionRequest(duelServer).connect();

        MatchFunctions.postPlayerJoin(initiator.getUniqueId(), null,
                null, initiator.getUniqueId(), channel);

        initiator.sendMessage(WARNING);
        if (size > 0 ) initiator.sendMessage(INVITE);

        setState(MatchState.WAITING);
    }

    public void duelAccepted(Player acceptor, RegisteredServer initialServer) {
        MatchPlayer matchPlayer = new MatchPlayer(acceptor, initialServer);

        for (MatchTeam team : teams)
            if (team.getLeader().equals(matchPlayer)) return;

        teams.add(new MatchTeam(matchPlayer));

        MatchFunctions.postPlayerJoin(acceptor.getUniqueId(), null,
                convertMatchPlayerListToUUIDs(getOpponents(matchPlayer)), acceptor.getUniqueId(), channel);

        sendMessage(Colors.get(acceptor.getUsername() + " has joined."));

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

    public void addPlayer(Player player, UUID inviteId, RegisteredServer initialServer) {
        MatchTeam invitedTeam = invites.get(inviteId);

        if (state != MatchState.WAITING) {
            player.sendMessage(STARTED);
            return;
        }

        if (invitedTeam.getFollowers().size() > size) {
            player.sendMessage(FULL);
            return;
        }

        MatchPlayer matchPlayer = new MatchPlayer(player, initialServer);
        invitedTeam.addFollower(matchPlayer);

        MatchFunctions.postPlayerJoin(player.getUniqueId(), convertMatchPlayerListToUUIDs(getTeammates(matchPlayer)),
                convertMatchPlayerListToUUIDs(getOpponents(matchPlayer)), player.getUniqueId(), channel);

        if (checkReady()) {
            onStart();
            return;
        }

        player.sendMessage(WARNING_FOLLOWER);
        sendMessage(Colors.get(player + " has joined on " + invitedTeam.getLeader().getPlayer().getUsername()
                + "'s team (" + invitedTeam.getFollowers().size() + "/" + size + ")."));
    }

    public void leave(Player player) {
        MatchPlayer matchPlayer = getPlayer(player);

        for (MatchTeam team : teams) {
            if (team.getLeader().getPlayer() == player) {
                cancelDuel();

                player.sendMessage(YOU_CANCELLED);
                return;
            } else {
                if (team.getFollowers().contains(matchPlayer)) team.removeFollower(matchPlayer);

//                showPlayer(matchPlayer);
//                MatchFunctions.revertPlayerInitials(matchPlayer);
                player.sendMessage(LEFT);
                return;
            }
        }
    }

    public void forfeit(Player player) {
        sendMessage(Colors.get(player.getUsername() + " forfeited."));
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
        sendMessage(CANCELLED);
        onEnd();
    }

    void returnWagers() {
        for (MatchTeam team : teams) {}
//            EconomyHook.addMoney(team.getLeader().getPlayer(), wager);
    }

    public void startPlaying() {
        List<UUID> team1 = new ArrayList<>();
        List<UUID> team2 = new ArrayList<>();

        boolean first = true;
        for (MatchTeam team : getTeams()) {
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                Player player = matchPlayer.getPlayer();

                if (first) team1.add(player.getUniqueId());
                else team2.add(player.getUniqueId());
            }
            first = false;
        }

        MatchFunctions.postMatchStart(team1, team2, kit, channel);
        sendMessage(WARNING_FORFEIT);
    }

    public void cleanup() {
        for (MatchTeam matchTeam : teams) for (MatchPlayer matchPlayer : matchTeam.getAllPlayers()) {
                assert matchPlayer.getPlayer().getCurrentServer().isPresent();
                if (Objects.equals(matchPlayer.getPlayer().getCurrentServer().get().getServer(), duelServer))
                    matchPlayer.getPlayer().createConnectionRequest(matchPlayer.getOriginalServer()).connect();
            }

        teams.clear();
    }

    public void endMatch() {
        returnWagers();
        sendMessage(ADMIN_CANCEL);

        onEnd();
    }


    public void playerDied(MatchPlayer matchPlayer) {
        matchPlayer.getPlayer().sendMessage(DIED);
        matchPlayer.setDead(true);

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
//            EconomyHook.addMoney(aliveTeam.getLeader().getPlayer(), (wager * 2));

            for (MatchPlayer matchPlayer : aliveTeam.getAllPlayers())
                matchPlayer.getPlayer().sendMessage(WON);

            MatchTeam losingTeam = null;

            for (MatchTeam team : teams)
                if (team != aliveTeam) losingTeam = team;

            assert losingTeam != null;

            for (MatchPlayer matchPlayer : losingTeam.getAllPlayers())
                matchPlayer.getPlayer().sendMessage(LOST);

            onEnd();
        }
    }

    public MatchPlayer getPlayer(Player player) {
        for (MatchTeam team : getTeams())
            for (MatchPlayer mPlayer : team.getAllPlayers())
                if (mPlayer.getPlayer() == player) return mPlayer;

        return null;
    }

    public List<MatchPlayer> getOpponents(MatchPlayer player) {
        List<MatchPlayer> opponents = new ArrayList<>();
        for (MatchTeam team : teams) {
            if (team.getLeader() == player || team.getAllPlayers().contains(player)) continue;
            opponents.addAll(team.getAllPlayers());
        }

        return opponents;
    }

    public List<MatchPlayer> getTeammates(MatchPlayer player) {
        List<MatchPlayer> teammates = new ArrayList<>();
        for (MatchTeam team : teams) {
            if (team.getLeader() != player && !team.getAllPlayers().contains(player)) continue;
            teammates.addAll(team.getAllPlayers());
        }

        return teammates;
    }

    public List<UUID> convertMatchPlayerListToUUIDs(List<MatchPlayer> list) {
        List<UUID> uuids = new ArrayList<>();
        for (MatchPlayer player : list) uuids.add(player.getPlayer().getUniqueId());
        return uuids;
    }

    public void sendMessage(Component msg) {
        for (MatchTeam team : this.teams)
            for (MatchPlayer matchPlayer : team.getAllPlayers())
                if (!matchPlayer.isDead()) matchPlayer.getPlayer().sendMessage(msg);
    }

    public void sendSound(Sound sound, float vol, float pit) {
        for (MatchTeam team : this.teams)
            for (MatchPlayer matchPlayer : team.getAllPlayers()) {
                ProtocolizePlayer player = Protocolize.playerProvider().player(matchPlayer.getPlayer().getUniqueId());
                player.playSound(sound, SoundCategory.MASTER, vol, pit);
            }
    }

    void addInvite(UUID inviteId, MatchTeam team) {
        invites.put(inviteId, team);
    }

    // TODO: Code feature to cancel invites
}
