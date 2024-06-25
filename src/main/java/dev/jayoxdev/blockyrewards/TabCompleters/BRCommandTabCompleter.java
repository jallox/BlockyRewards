package dev.jayoxdev.blockyrewards.TabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BRCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        List<String> subCommands = new ArrayList<>();
        subCommands.add("help");
        subCommands.add("reload");
        subCommands.add("gui");
        subCommands.add("purge");
        subCommands.add("setup");
        subCommands.add("list");
        subCommands.add("user");
        return subCommands;

    }
}
