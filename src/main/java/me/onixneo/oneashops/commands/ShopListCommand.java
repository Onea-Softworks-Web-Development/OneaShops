package me.onixneo.oneashops.commands;

import me.onixneo.oneashops.OneaShops;
import me.onixneo.oneashops.serializables.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShopListCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            List<String> access = new ArrayList<>();

            for (Shop shop : OneaShops.getShopConfig().getShops()) {
                if (OneaShops.getPermission().has(player, "oneashops." + shop.getFormattedName())) {
                    access.add(shop.getName() + " (" + shop.getFormattedName() + ")");
                }
            }

            if (access.size() == 0) {
                player.sendMessage(ChatColor.GOLD + "Vous n'avez accès à aucune boutique.");
            } else {
                player.sendMessage(ChatColor.GOLD + "Liste des boutiques : " + ChatColor.RESET + String.join(ChatColor.GOLD + ", " + ChatColor.RESET, access));
            }
        } else if (sender instanceof ConsoleCommandSender) {
            List<String> names = new ArrayList<>();
            for (Shop shop : OneaShops.getShopConfig().getShops()) {
                names.add(shop.getName() + " (" + shop.getFormattedName() + ")");
            }
            sender.sendMessage(String.join(", ", names));
        }

        return true;
    }
}
