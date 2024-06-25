package dev.jayoxdev.blockyrewards.Commands;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import dev.jayoxdev.blockyrewards.GUIs.AdminGUI;
import dev.jayoxdev.blockyrewards.Utils.SupportCheck;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


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
                    if(new SupportCheck().isGUISupported()) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br gui &7Opens the admin GUI"));
                    }
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br setup &7Sets up the plugin. &cRun only once"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br list &7Lists the current rewards"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/br user &#0089BD<player> &#2C91B8<action> &7Executes an action on the selected player"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/rewards &#0089BD<action> &7Player rewards command"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/reward &#0089BD<action> &7Player rewards command"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/claim &7Command to claim a available reward"));
                    commandSender.sendMessage(plugin.getMessageUtil().parse("&#00B9FF/redeem &#0089BD<reward> &7Claims a reward"));

                }
                case "reload" -> {
                    if(commandSender.hasPermission("blockyrewards.admin.reload")) {
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

                    }else{
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                    }
                }
                case "gui" -> {
                    if(commandSender.hasPermission("blockyrewards.admin.gui")) {
                        if(new SupportCheck().isGUISupported()) {
                            new AdminGUI(plugin, commandSender);
                        }else{
                            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&4&ERROR: &cGUI is not supported on this bukkit version. Check for plugin updates if you want support"));
                        }
                    }else{
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                    }
                }
                case "purge" -> {
                    if(args.length == 1) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6This command deletes the entire DB. This process is irreversible and may affect to user's rewards"));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6To continue with this action, execute &n/br purge confirm"));

                    }
                    if (args[1].equalsIgnoreCase("confirm")) {
                        if (commandSender.hasPermission("blockyrewards.admin.purge")) {
                            try (Connection connection = plugin.getDatabase().getConnection()) {
                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: Purging users..."));
                                PreparedStatement purgeStatement1 = connection.prepareStatement("DELETE * FROM users");
                                purgeStatement1.execute();
                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: &aUsers purged!"));

                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: Purging user_rewards..."));
                                PreparedStatement purgeStatement2 = connection.prepareStatement("DELETE * FROM user_rewards");
                                purgeStatement2.execute();
                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: &aUser_rewards purged!"));

                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: Purging commandClaims..."));
                                PreparedStatement purgeStatement3 = connection.prepareStatement("DELETE * FROM commandClaims");
                                purgeStatement3.execute();
                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: &aCommandClaims purged!"));
                                commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&cDatabase Purge: &aDONE! All database has been purged!"));
                                plugin.getMessageUtil().sendConsole(prefix + "&c&l&nWARNING: DATABASE HAS BEEN PURGED BY &4&l&n" + commandSender.getName());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                            commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                        }
                    } else {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6This command deletes the entire DB. This process is irreversible and may affect to user's rewards"));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6To continue with this action, execute &n/br purge confirm"));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &c" + args[1] + " is not a valid argument"));
                    }
                }
                case "setup" -> {
                    if(commandSender.hasPermission("blockyrewards.admin.setup")) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6Setup unavailable"));
                    }else{
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                    }
                }
                case "list" -> {
                    if(commandSender.hasPermission("blockyrewards.admin.list")) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6List unavailable"));
                    }else{
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                    }
                }
                case "user" -> {
                    if(commandSender.hasPermission("blockyrewards.admin.user")) {
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + "&6&lWARNING: &6Users unavailable"));
                    }else{
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-admin")));
                        commandSender.sendMessage(plugin.getMessageUtil().parse(prefix + plugin.getConfigUtil().getMessages().getString("errors.no-permission-command")));
                    }
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
