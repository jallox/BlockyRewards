package dev.jayoxdev.blockyrewards.Commands;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import dev.jayoxdev.blockyrewards.Utils.Config;
import dev.jayoxdev.blockyrewards.Utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RedeemCommand implements CommandExecutor {
    private final BlockyRewards plugin;
    private final Message messageUtil;
    private final Config configUtil;

    public RedeemCommand(BlockyRewards plugin) {
        this.plugin = plugin;
        this.messageUtil = plugin.getMessageUtil();
        this.configUtil = plugin.getConfigUtil();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("blockyrewards.commands.claim")) {
            if (args.length == 0) {
                sender.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.code-needed")));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }

            Player player = (Player) sender;
            String claimCode = args[0];

            FileConfiguration rewardsConfig = configUtil.getRewards();
            if (rewardsConfig.contains("command_rewards." + claimCode)) {
                int maxClaims = rewardsConfig.getInt("command_rewards." + claimCode + ".max_claims", 1);
                String[] timeoutParts = rewardsConfig.getString("command_rewards." + claimCode + ".claim_timeout", "0;0;0").split(";");
                int timeoutHours = Integer.parseInt(timeoutParts[0]);
                int timeoutMinutes = Integer.parseInt(timeoutParts[1]);
                int timeoutSeconds = Integer.parseInt(timeoutParts[2]);

                try (Connection connection = plugin.getDatabase().getConnection()) {
                    PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM users WHERE uuid = ?");
                    ps.setString(1, player.getUniqueId().toString());
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next()) {
                        PreparedStatement insertUserPs = connection.prepareStatement("INSERT INTO users (uuid, last_login, rewards_claimed) VALUES (?, ?, 0)");
                        insertUserPs.setString(1, player.getUniqueId().toString());
                        insertUserPs.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        insertUserPs.executeUpdate();
                    }

                    ps = connection.prepareStatement("SELECT claim_count, last_claim FROM commandClaims WHERE uuid = ? AND claim_code = ?");
                    ps.setString(1, player.getUniqueId().toString());
                    ps.setString(2, claimCode);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int claimCount = rs.getInt("claim_count");
                        Timestamp lastClaim = rs.getTimestamp("last_claim");
                        LocalDateTime lastClaimTime = lastClaim.toLocalDateTime();
                        LocalDateTime nextClaimTime = lastClaimTime.plusHours(timeoutHours).plusMinutes(timeoutMinutes).plusSeconds(timeoutSeconds);
                        LocalDateTime now = LocalDateTime.now();

                        if (claimCount >= maxClaims) {
                            player.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.reward-already-claimed-many-times")));
                            return true;
                        }

                        if (now.isBefore(nextClaimTime)) {
                            long secondsUntilNextClaim = ChronoUnit.SECONDS.between(now, nextClaimTime);
                            long hours = secondsUntilNextClaim / 3600;
                            long minutes = (secondsUntilNextClaim % 3600) / 60;
                            long seconds = secondsUntilNextClaim % 60;
                            player.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.reward-timeout").replace("{%0}", String.format("%02d:%02d:%02d", hours, minutes, seconds))));
                            return true;
                        }

                        claimCount++;
                        PreparedStatement updatePs = connection.prepareStatement("UPDATE commandClaims SET claim_count = ?, last_claim = ? WHERE uuid = ? AND claim_code = ?");
                        updatePs.setInt(1, claimCount);
                        updatePs.setTimestamp(2, Timestamp.valueOf(now));
                        updatePs.setString(3, player.getUniqueId().toString());
                        updatePs.setString(4, claimCode);
                        updatePs.executeUpdate();
                    } else {
                        PreparedStatement insertPs = connection.prepareStatement("INSERT INTO commandClaims (uuid, claim_code, claim_count, last_claim) VALUES (?, ?, ?, ?)");
                        insertPs.setString(1, player.getUniqueId().toString());
                        insertPs.setString(2, claimCode);
                        insertPs.setInt(3, 1);
                        insertPs.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                        insertPs.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.database-error")));
                    return true;
                }

                List<String> items = rewardsConfig.getStringList("command_rewards." + claimCode + ".give");
                for (String item : items) {
                    String[] itemData = item.split(";");
                    if (itemData.length == 2) {
                        Material material = Material.getMaterial(itemData[0]);
                        int amount = Integer.parseInt(itemData[1]);

                        if (material != null) {
                            ItemStack itemStack = new ItemStack(material, amount);
                            player.getInventory().addItem(itemStack);
                        }
                    }
                }

                List<String> commands = rewardsConfig.getStringList("command_rewards." + claimCode + ".execute");
                for (String cmd : commands) {
                    cmd = cmd.replace("{%0}", player.getName())
                            .replace("{%1}", claimCode)
                            .replace("{%2}", "Reward name here"); // Replace with actual reward name if available
                    if (cmd.startsWith("console:")) {
                        String consoleCommand = cmd.substring("console:".length());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
                    } else if (cmd.startsWith("player:")) {
                        String playerCommand = cmd.substring("player:".length());
                        player.performCommand(playerCommand);
                    }
                }

                player.sendMessage(messageUtil.parse(plugin.getPrefix() + plugin.getConfigUtil().getMessages().getString("command_rewards.claimed").replace("{%0}", claimCode)));
            } else {
                player.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.invalid-code")));
            }
        } else {
            sender.sendMessage(messageUtil.parse(plugin.getPrefix() + configUtil.getMessages().getString("errors.no-permission")));
        }
        return true;
    }
}
