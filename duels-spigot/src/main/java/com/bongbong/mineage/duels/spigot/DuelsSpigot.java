package com.bongbong.mineage.duels.spigot;

import co.aikar.commands.PaperCommandManager;
import com.bongbong.mineage.duels.spigot.impl.SerializeCommand;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannelBuilder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**

  This project loosely follows Will's interpretation of Procedural
  Programming Principles. Don't fully understand what that means?
  Watch this video: https://www.youtube.com/watch?v=QM1iUe6IofM

  Protected functions are protected for a reason.
  Call-stack is strictly enforced. Inject all state properly.
  All state should be minimized and highly-protected

  Code with pure functions when possible. Write tests.

  Follow best practices for code-aesthetics; favor readability
  and maintainability over all else.
**/

// Make sure server name in server.properties matches with the server name in velocity.toml
// Plugin will not work if not.

public class DuelsSpigot extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "duels.admin";

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new SerializeCommand());

        System.out.println(getConfig().getInt("GRPC_PORT"));

        try {
            new RPCHandler(getConfig().getInt("GRPC_PORT"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        registerListener(new EventListener(getLogger(),
                ManagedChannelBuilder
                        .forAddress(getConfig().getString("PROXY_IP"), getConfig().getInt("PROXY_PORT"))
                        .build()));
    }


    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

}
