package dev.xdpxi.xdlib;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import dev.xdpxi.xdlib.api.mod.loader;
import dev.xdpxi.xdlib.config.configHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordRPCHandler {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    private static Thread callbackThread;
    private static boolean initialized = false;

    public static void init() {
        try {
            if (initialized) {
                return;
            }

            DiscordRPC lib = DiscordRPC.INSTANCE;
            String applicationId = "1268895788558319626";
            String steamId = "";
            DiscordEventHandlers handlers = new DiscordEventHandlers();
            handlers.ready = (user) -> LOGGER.info("[XDLib] - Started Discord RPC!");
            lib.Discord_Initialize(applicationId, handlers, true, steamId);

            DiscordRichPresence presence = new DiscordRichPresence();
            presence.startTimestamp = System.currentTimeMillis() / 1000;
            presence.details = "Playing Minecraft";
            lib.Discord_UpdatePresence(presence);

            callbackThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    lib.Discord_RunCallbacks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "RPC-Handler");
            callbackThread.start();

            initialized = true;
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Failed to initialize Discord RPC.", e);
        }

        if (loader.isModLoaded("cloth-config")) {
            if (!configHelper.isDiscordRPCEnabled()) {
                shutdown();
            }
        }
    }

    public static void shutdown() {
        try {
            if (callbackThread != null && callbackThread.isAlive()) {
                callbackThread.interrupt();
                callbackThread.join();
            }
            DiscordRPC.INSTANCE.Discord_Shutdown();
        } catch (Exception e) {
            LOGGER.error("[XDLib] - Failed to shutdown Discord RPC: ", e);
        } finally {
            initialized = false;
        }
    }
}