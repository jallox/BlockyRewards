package dev.jayoxdev.blockyrewards.Utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

@SuppressWarnings({"unused "})
public class Message {

    private final String parser;

    public Message(String parser) {
        this.parser = parser;
    }

    public String parse(String text) {
        if (parser.equals("Legacy")) {
            TextComponent legacyParse = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
            return LegacyComponentSerializer.legacySection().serialize(legacyParse);   // Parses using LegacyComponetnSerializer
        } else if (parser.equals("MiniMessage")) {
            MiniMessage miniMessage = MiniMessage.miniMessage();
            TextComponent mmt = (TextComponent) miniMessage.deserialize(text);
            return LegacyComponentSerializer.legacySection().serialize(mmt);    // Parses using MiniMessage
        } else {
            return String.valueOf(LegacyComponentSerializer.legacyAmpersand().deserialize("&cError while parsing text. Unknown parser &n" + parser));   // If unknown parser given
        }
    }

    public void sendConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(parse(text));
    }

    public String getParser() {
        return parser;
    }


}
