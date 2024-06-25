package dev.jayoxdev.blockyrewards.Utils;

import org.bukkit.Bukkit;

@SuppressWarnings({"unused"})
public class SupportCheck {
    public final boolean isGUISupported() {
        String[] bukkitVersion = Bukkit.getServer().getBukkitVersion().split("\\.");
        return bukkitVersion[1].equals("21");
    }
}
