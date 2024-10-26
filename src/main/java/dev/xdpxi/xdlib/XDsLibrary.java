package dev.xdpxi.xdlib;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xdpxi.xdlib.api.custom;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderStage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.command.argument.MessageArgumentType.getMessage;
import static net.minecraft.command.argument.MessageArgumentType.message;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class XDsLibrary implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("xdlib");
    public static final String MOD_ID = "xdlib";
    public static final Identifier DEATH_SOUND_ID = Identifier.of("xdlib:death");
    public static int duration = -1;
    public static List<HostileEntity> list = new ArrayList<>();
    public static SoundEvent DEATH_SOUND_EVENT = SoundEvent.of(DEATH_SOUND_ID);

    public static GameRules.Key<GameRules.IntRule> VAULT_BLOCK_COOLDOWN;
    public static GameRules.Key<GameRules.IntRule> OMINOUS_VAULT_BLOCK_COOLDOWN;

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

        ModNetworkHandler.registerServer();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, registrationEnvironment) -> {
            dispatcher.register(literal("link")
                    .then(argument("message", message())
                            .executes(ctx -> broadcast(ctx.getSource(), getMessage(ctx, "message").getString())))
            );
            dispatcher.register(literal("linkwhisper")
                    .then(argument("player", EntityArgumentType.player())
                            .then(argument("message", message())
                                    .executes(ctx -> whisper(ctx.getSource(), getMessage(ctx, "message").getString(), EntityArgumentType.getPlayer(ctx, "player")))))
            );
        });
        VAULT_BLOCK_COOLDOWN = GameRuleRegistry.register("vaultBlockCooldown", GameRules.Category.MISC, GameRuleFactory.createIntRule(720_000, 1));
        OMINOUS_VAULT_BLOCK_COOLDOWN = GameRuleRegistry.register("ominousVaultBlockCooldown", GameRules.Category.MISC, GameRuleFactory.createIntRule(864_000, 1));

        Item xdlib_item = custom.Item("xdlib_item", MOD_ID);
        List<Item> items = List.of(
                xdlib_item
        );
        custom.ItemGroup("xdlib_group", MOD_ID, xdlib_item, items);

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, parameters) -> {
            String stringMessage = message.getContent().getString();
            String namePrefix = "<" + sender.getName().getString() + "> ";

            switch (stringMessage) {
                case "{pos}" -> {
                    return broadcastToPlayers(sender, namePrefix + getPos(sender));
                }
                case "{biome}" -> {
                    return broadcastToPlayers(sender, namePrefix + getBiome(sender));
                }
                case "{dim}" -> {
                    return broadcastToPlayers(sender, namePrefix + getDim(sender));
                }
                case "{loc}" -> {
                    String all = namePrefix + getPos(sender) + " (" + getBiome(sender) + ") " + "(" + getDim(sender) + ")";
                    return broadcastToPlayers(sender, all);
                }
            }
            return true;
        });

        ServerTickEvents.END_WORLD_TICK.register(this::postWorldTick);

        LOGGER.info("[XDLib] - Loaded!");
    }

    private boolean broadcastToPlayers(ServerPlayerEntity sender, String message) {
        List<ServerPlayerEntity> players = sender.getServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players)
            player.sendMessage(Text.literal(message));
        return false;
    }

    private String getPos(ServerPlayerEntity sender) {
        return (int) sender.getPos().getX() + ", " + (int) sender.getPos().getY() + ", " + (int) sender.getPos().getZ();
    }

    private String getBiome(ServerPlayerEntity sender) {
        String rawBiome = Identifier.of(sender.getEntityWorld().getBiome(sender.getBlockPos()).getIdAsString()).getPath();
        return fixName(rawBiome);
    }

    private String getDim(ServerPlayerEntity sender) {
        String dim = sender.getEntityWorld().getRegistryKey().getValue().getPath();
        return fixName(dim);
    }

    private String fixName(String rawName) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < rawName.length(); i++) {
            if (i == 0 || rawName.charAt(i - 1) == '_') {
                name.append(Character.toUpperCase(rawName.charAt(i)));
                continue;
            }

            if (rawName.charAt(i) == '_') {
                name.append(" ");
                continue;
            }

            name.append(rawName.charAt(i));
        }
        return name.toString();
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