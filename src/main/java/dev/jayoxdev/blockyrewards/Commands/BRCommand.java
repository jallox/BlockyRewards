package dev.jayoxdev.blockyrewards.Commands;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import dev.jayoxdev.blockyrewards.GUIs.AdminGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public class BRCommand implements CommandExecutor {
    private BlockyRewards plugin;
    private String prefix;

    public BRCommand(BlockyRewards plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getPrefix();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFRunning version &l" + plugin.getVersion()));
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFFound any bug? Report it at my discord: &ahttps://discord.gg/tfvqwBbyRG"));
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FF&lRun /br help for help"));
            return true;
        }
        if (!commandSender.hasPermission("blockyrewards.admin")) {
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFRunning version &l" + plugin.getVersion()));
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFFound any bug? Report it at my discord: &ahttps://discord.gg/tfvqwBbyRG"));
            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&4You don't have permission to execute any sub-command"));
            return true;
        } else {
            switch (args[0].toLowerCase()) {

                case "help" -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse("You are running version: &a" + plugin.getVersion()));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br reload &7Reloads configuration"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br gui &7Opens the admin GUI"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br purge &#0089BD<string> &7Purges a reward"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br setup &7Sets up the plugin. &cRun only once"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br list &7Lists the current rewards"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br user &#0089BD<player> &#2C91B8<action> &7Executes an action on the selected player"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/rewards &#0089BD<action> &7Player rewards command"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/reward &#0089BD<action> &7Player rewards command"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/claim &7Command to claim a available reward"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/redeem &#0089BD<reward> &7Claims a reward"));

                }
                case "reload" -> {
                    long startTime = System.currentTimeMillis();

                    String oldDB = plugin.getConfig().getString("database.type");
                    int oldCVersion = plugin.getConfig().getInt("config");
                    plugin.getConfigUtil().reloadConfig();
                    plugin.getConfigUtil().reloadMessages();
                    plugin.getConfigUtil().reloadRewards();
                    if (!(oldDB.equalsIgnoreCase(plugin.getConfig().getString("database.type")))) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &eThe database type has changed. This change requires server restart to start working."));
                    }
                    if (!(oldCVersion == (plugin.getConfig().getInt("config")))) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&4&lDANGER: &cThe config version has changed. &7(" + oldCVersion + "  -> " + plugin.getConfig().getInt("config") + "). &cThis may cause errors."));
                    }
                    plugin.getConfigUtil().saveAll();
                    long endTime = System.currentTimeMillis();
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FF&lDone! &#00B9FFConfig reloaded in &b" + (endTime - startTime) + "ms"));

                }
                case "gui" -> {
                    new AdminGUI(plugin, commandSender);
                }
                case "purge" -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6Command error. Dump code: 1a1b1c1d1"));
                }
                case "setup" -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6Setup unavailable"));
                }
                case "list" -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6List unavailable"));
                }
                case "user" -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6Users unavailable"));
                }
                default -> {
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFRunning version &l" + plugin.getVersion()));
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FFFound any bug? Report it at my discord: &ahttps://discord.gg/tfvqwBbyRG"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&#00B9FF&lRun /br help for help"));
                }
            }
            return true;
        }


    }
}
