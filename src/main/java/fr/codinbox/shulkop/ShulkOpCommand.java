package fr.codinbox.shulkop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ShulkOpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("shulkop.manage"))
            return true;
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /shulkop <enable|disable>");
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "enable" -> {
                ShulkOpPlugin.enabled = true;
                sender.sendMessage("§aShulkOp enabled.");
            }
            case "disable" -> {
                ShulkOpPlugin.enabled = false;
                sender.sendMessage("§cShulkOp disabled.");
            }
            default -> sender.sendMessage("§cUsage: /shulkop <enable|disable>");
        }
        return true;
    }

}
