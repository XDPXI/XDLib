package dev.xdpxi.xdlib;

import dev.xdpxi.xdlib.util.Log;
import com.google.inject.Inject;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("xdlib")
public class Main {
    private final PluginContainer container;
    private final Logger logger;
    public static final String version = "4.0.0-beta.8";

    @Inject
    Main(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        CommonClass.init();

        Thread updateThread = new Thread(new UpdateCheckerSponge(), "Update thread");
        updateThread.setDaemon(true);
        updateThread.start();

        Log.info("[XDLib/Main] - Loaded!");
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {

    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        Log.info("[XDLib/Main] - Disabling...");
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        final Parameter.Value<String> nameParam = Parameter.string().key("name").build();
        event.register(this.container, Command.builder().addParameter(nameParam).permission("spong.command.greet").executor(ctx -> {
            final String name = ctx.requireOne(nameParam);
            ctx.sendMessage(Identity.nil(), LinearComponents.linear(NamedTextColor.AQUA, Component.text("Hello "), Component.text(name, Style.style(TextDecoration.BOLD)), Component.text("!")));

            return CommandResult.success();
        }).build(), "greet", "wave");
    }
}
