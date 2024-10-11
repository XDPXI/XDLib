package dev.xdpxi.xdlib.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("xdlib")) {
            if (args.length == 1) {
                return Arrays.asList("reload", "help");
            }
        }
        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length == 1) {
                return Arrays.asList("accept", "decline", "ask");
            }
        }
        return new ArrayList<>();
    }
}