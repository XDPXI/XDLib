package dev.xdpxi.xdlib;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xdpxi.xdlib.api.mod.custom;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderStage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class XDsLibrary implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    public static final String MOD_ID = "xdlib";
    public static int duration = -1;
    public static List<HostileEntity> list = new ArrayList<>();

    @Getter
    public static boolean eulaAccepted = false;

    public static void acceptEula() {
        eulaAccepted = true;
    }

    @Contract("_ -> new")
    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static int broadcast(ServerCommandSource source, String message) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = source.getPlayer();

            if (message.contains(" ") || !(message.contains("https://") || message.contains("http://"))) {
                final Text error = Text.literal("Links cannot have spaces and must have http://").formatted(Formatting.RED, Formatting.ITALIC);
                assert player != null;
                player.sendMessage(error, false);
                return -1;
            }

            assert player != null;
            AbstractTeam abstractTeam = player.getScoreboardTeam();
            Formatting playerColor = abstractTeam != null && abstractTeam.getColor() != null ? abstractTeam.getColor() : Formatting.WHITE;

            final Text announceText = Text.literal("")
                    .append(Text.literal(source.getName()).formatted(playerColor).formatted())
                    .append(Text.literal(" has a link to share!").formatted());
            final Text text = Text.literal(message).styled(s ->
                    s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to Open Link!")))
                            .withColor(Formatting.BLUE).withUnderline(true));

            source.getServer().getPlayerManager().broadcast(announceText, false);
            source.getServer().getPlayerManager().broadcast(text, false);
            return Command.SINGLE_SUCCESS; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int whisper(ServerCommandSource source, String message, ServerPlayerEntity target) throws CommandSyntaxException {
        try {
            ServerPlayerEntity player = source.getPlayer();

            if (message.contains(" ") || !(message.contains("https://") || message.contains("http://"))) {
                final Text error = Text.literal("Links cannot have spaces and must have http://").formatted(Formatting.RED, Formatting.ITALIC);
                assert player != null;
                player.sendMessage(error, false);
                return -1;
            }

            assert player != null;
            AbstractTeam abstractTeam = player.getScoreboardTeam();
            Formatting playerColor = abstractTeam != null && abstractTeam.getColor() != null ? abstractTeam.getColor() : Formatting.WHITE;

            if (!player.equals(target)) {
                final Text senderText = Text.literal("")
                        .append(Text.literal("You whisper a link to ").formatted(Formatting.GRAY, Formatting.ITALIC))
                        .append(Text.literal(source.getName()).formatted(playerColor).formatted(Formatting.ITALIC));

                final Text senderLink = Text.literal(message).styled(s ->
                        s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to Open Link!")))
                                .withColor(Formatting.BLUE).withItalic(true));
                player.sendMessage(senderText);
                player.sendMessage(senderLink);
            }

            final Text announceText = Text.literal("")
                    .append(Text.literal(source.getName()).formatted(playerColor).formatted(Formatting.ITALIC))
                    .append(Text.literal(" whispers a link to you!").formatted(Formatting.GRAY, Formatting.ITALIC));
            final Text text = Text.literal(message).styled(s ->
                    s.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to Open Link!")))
                            .withColor(Formatting.BLUE).withItalic(true).withUnderline(true));

            target.sendMessage(announceText);
            target.sendMessage(text);
            return Command.SINGLE_SUCCESS; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void registerItems() {
        try {
            Item xdlibItem = custom.Item("xdlib_item", MOD_ID);
            if (xdlibItem == null) {
                LOGGER.error("[XDLib] Failed to create xdlib_item - Item creation returned null");
            }

            List<Item> items = List.of(xdlibItem);
            custom.ItemGroup("xdlib_group", MOD_ID, xdlibItem, items);
            LOGGER.info("[XDLib] Successfully registered items and item group");
        } catch (Exception e) {
            LOGGER.error("[XDLib] Failed to register items: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[XDLib] - Loading...");
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            LOGGER.debug("[XDLib] - Running on MacOS");
        } else if (osName.contains("win")) {
            LOGGER.debug("[XDLib] - Running on Windows");
        } else {
            LOGGER.debug("[XDLib] - Running on an unsupported OS: {}", osName);
        }

        updateChecker.checkForUpdate();
        registerItems();

        ServerTickEvents.END_WORLD_TICK.register(this::postWorldTick);

        LOGGER.info("[XDLib] - Loaded!");
    }

    private void postWorldTick(ServerWorld world) {
        if (world.getTime() % 10 != 0)
            return;

        WorldBorder border = world.getServer().getOverworld().getWorldBorder();
        if (border.getStage() == WorldBorderStage.GROWING)
            return;

        for (Entity entity : world.iterateEntities()) {
            if (!(entity instanceof ItemEntity item)
                    || !item.getStack().isOf(Items.DIAMOND)
                    || !border.canCollide(entity, entity.getBoundingBox()))
                continue;

            int diamonds = item.getStack().getCount();
            item.getStack().decrement(diamonds);

            double size = border.getSize();
            double newSize = size + diamonds;
            long timePerDiamond = (long) (10 * 1e3);
            border.interpolateSize(size, newSize, diamonds * timePerDiamond);
        }
    }
}