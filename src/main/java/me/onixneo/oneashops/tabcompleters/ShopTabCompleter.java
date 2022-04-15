package me.onixneo.oneashops.tabcompleters;

import me.onixneo.oneashops.OneaShops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            List<String> list = new ArrayList<>();

            OneaShops.getShopConfig().getShops().forEach(shop -> {
                if (OneaShops.getPermission().has(sender, "oneashops." + shop.getFormattedName())) {
                    list.add(shop.getFormattedName());
                }
            });

            Collections.sort(list);

            if (args.length == 0) {
                return list;
            } else if (args.length == 1) {
                List<String> rlist = new ArrayList<>();

                for (String s : list) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                        rlist.add(s);
                    }
                }
                Collections.sort(rlist);
                return rlist;
            } else {
                return new ArrayList<>();
            }
        }
        return null;
    }
}
