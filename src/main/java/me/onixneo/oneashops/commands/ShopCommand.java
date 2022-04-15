package me.onixneo.oneashops.commands;

import me.onixneo.oneashops.OneaShops;
import me.onixneo.oneashops.serializables.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopCommand implements CommandExecutor {

    private JavaPlugin plugin;

    public ShopCommand (JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                plugin.getCommand("shopslist").getExecutor().onCommand(sender, command, label, args);
            } else {
                Shop shop = OneaShops.getShopConfig().getShopByFormattedName(args[0]);

                if (shop != null) {
                    if (OneaShops.getPermission().has(player, "oneashops." + shop.getFormattedName())) {
                        player.openInventory(shop.getInventory());
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Vous ne pouvez pas accéder à la boutique " + args[0] + ".\nFaites /shopslist pour obtenir une liste des shops.");
                    }
                } else {
                    player.sendMessage(ChatColor.GOLD + "La boutique " + args[0] + " n'existe pas.\nFaites /shopslist pour obtenir une liste des shops.");
                }
            }
        } else {
            sender.sendMessage("Seul un joueur peut accéder aux boutiques.");
        }

        return true;
    }
}
