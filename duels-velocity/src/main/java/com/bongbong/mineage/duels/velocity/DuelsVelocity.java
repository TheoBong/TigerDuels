package com.bongbong.mineage.duels.velocity;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.mineage.duels.velocity.impl.*;
import com.bongbong.mineage.duels.velocity.utils.Config;
import com.bongbong.mineage.duels.velocity.utils.IPPortPair;
import com.bongbong.mineage.duels.velocity.utils.ServerObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.grpc.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;


@Plugin(
        id = "duels",
        name = "Duels",
        version = "1.0-SNAPSHOT",
        description = "Proxy-based duels plugin",
        url = "mineage.net",
        authors = "tigerbong"
)
public class DuelsVelocity {
    private final ProxyServer server;
    private final Path dataDirectory;
    private State state;

    @Inject
    public DuelsVelocity(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        state = new State(new HashMap<>());
        TaskScheduler scheduler = new TaskScheduler(this, server.getScheduler());


        VelocityCommandManager commandManager = new VelocityCommandManager(server, this);

        Config config = loadConfig();
        Map<String, Channel> channels = getChannels(config);
        Channel duelsChannel = ManagedChannelBuilder
                .forAddress(config.getDUELS_GRPC_IP(), config.getDUELS_GRPC_PORT()).build();

        Optional<RegisteredServer> duelServer = server.getServer(config.getDUELS_SERVER_NAME());
        assert duelServer.isPresent();

        new RPCServer(state, config.getGRPC_PORT());

        commandManager.registerCommand(new DuelCommand(state, scheduler, channels, duelsChannel,
                duelServer.get()));
        commandManager.registerCommand(new AcceptInviteCommand(scheduler, state));
        commandManager.registerCommand(new CancelDuelCommand(state));
        commandManager.registerCommand(new AcceptDuelCommand(state));
        commandManager.registerCommand(new EndDuelCommand(state));
        commandManager.registerCommand(new InviteCommand(state));
    }

    private Config loadConfig() throws IOException {
        Path path = Path.of(dataDirectory + "/config.yml");

        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.write(path, getClass().getResourceAsStream("/config.yml").readAllBytes(),
                    StandardOpenOption.CREATE_NEW);
        }

        File file = new File(path.toString());

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, Config.class);
    }

    private Map<String, Channel> getChannels(Config config) {
        Map<String, IPPortPair> servers = new HashMap<>();
        Map<String, Channel> channels = new HashMap<>();

        for (ServerObject server : config.getSERVERS())
            servers.put(server.getNAME(), new IPPortPair(server.getIP(), server.getPORT()));

        for (Map.Entry<String, IPPortPair> server : servers.entrySet())
            channels.put(server.getKey(),
                    ManagedChannelBuilder.forAddress(server.getValue().IP(), server.getValue().port()).build());

        return channels;
    }

    public void onProxyShutdown(ProxyShutdownEvent event) {
        state.shutdown();
    }
}
