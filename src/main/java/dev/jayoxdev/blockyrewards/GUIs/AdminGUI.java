package dev.jayoxdev.blockyrewards.GUIs;

import dev.jayoxdev.blockyrewards.BlockyRewards;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AdminGUI {
    private BlockyRewards plugin;
    private CommandSender sender;
    private Player player;

    public AdminGUI(BlockyRewards plugin, CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
        if (sender instanceof Player) {
            this.player = (Player) sender;
            showGUI(player);
        } else {
            sender.sendMessage(plugin.getMessageUtil().parse("&cYou are not a player. What are you??"));
        }
    }

    private void showGUI(Player player) {

        Gui main = Gui.gui()
                .title(Component.translatable(plugin.getMessageUtil().parse("&#00B9FF&lAdmin GUI")))
                .rows(3)
                .create();

        GuiItem chest = ItemBuilder.from(Material.CHEST).asGuiItem(event -> {
            event.setCancelled(true);
            main.close(player);
        });
        // Create ItemStack and set its metadata
        ItemStack chestItem = new ItemStack(Material.CHEST);
        ItemMeta guiChest = chestItem.getItemMeta();
        if (guiChest != null) {
            guiChest.setDisplayName(plugin.getMessageUtil().parse("&aList rewards"));

            List<String> chestLore = new ArrayList<>();
            chestLore.add(plugin.getMessageUtil().parse("&aList current available rewards in plugin"));
            chestLore.add(plugin.getMessageUtil().parse("&e"));
            chestLore.add(plugin.getMessageUtil().parse("&fFor creating new rewards, edit the file"));
            chestLore.add(plugin.getMessageUtil().parse("&e&nrewards.yml &fin plugin config"));
            chestLore.add(plugin.getMessageUtil().parse("&f"));
            guiChest.setLore(chestLore);

            chestItem.setItemMeta(guiChest);
        }

        // Set the ItemStack to the GuiItem
        chest.setItemStack(chestItem);
        main.setItem(2, 2, chest);


        // Sunflower to restart config
        GuiItem sunflower = ItemBuilder.from(Material.SUNFLOWER).asGuiItem(event -> {
            event.setCancelled(true);
            main.close(player);
            player.performCommand("br reload");
        });
        // Create ItemStack and set its metadata
        ItemStack sunflowerItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta sunflowerGUI = sunflowerItem.getItemMeta();
        if (sunflowerGUI != null) {
            sunflowerGUI.setDisplayName(plugin.getMessageUtil().parse("&dReload plugin"));

            List<String> sunflowerLore = new ArrayList<>();
            sunflowerLore.add(plugin.getMessageUtil().parse("&aReloads plugin configuration"));
            sunflowerLore.add(plugin.getMessageUtil().parse("&8Alias: /br reload"));
            sunflowerGUI.setLore(sunflowerLore);

            sunflowerItem.setItemMeta(sunflowerGUI);
        }

        // Set the ItemStack to the GuiItem
        sunflower.setItemStack(sunflowerItem);
        main.setItem(2, 5, sunflower);

        main.open(player);

        // Arrow to close menu
        GuiItem arrow = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
            event.setCancelled(true);
            main.close(player);
        });
        // Create ItemStack and set its metadata
        ItemStack arrowItem = new ItemStack(Material.ARROW);
        ItemMeta arrowGUI = arrowItem.getItemMeta();
        if (arrowGUI != null) {
            arrowGUI.setDisplayName(plugin.getMessageUtil().parse("&cClose GUI"));

            List<String> arrowLore = new ArrayList<>();
            arrowLore.add(plugin.getMessageUtil().parse("&aClose this gui"));
            arrowGUI.setLore(arrowLore);

            arrowItem.setItemMeta(arrowGUI);
        }

        // Set the ItemStack to the GuiItem
        arrow.setItemStack(arrowItem);
        main.setItem(2, 8, arrow);

        main.open(player);
    }

}
