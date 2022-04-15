package me.onixneo.oneashops;

import java.util.logging.Logger;

import me.onixneo.oneashops.commands.ShopListCommand;
import me.onixneo.oneashops.listeners.ShopItemClick;
import me.onixneo.oneashops.serializables.*;
import me.onixneo.oneashops.settings.ShopConfig;
import me.onixneo.oneashops.tabcompleters.ShopTabCompleter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.onixneo.oneashops.commands.ShopCommand;

public final class OneaShops extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    private static Permission perms = null;
    private static ShopConfig shopConfig;

    @Override
    public void onDisable() {
        // Log : Shutdown process complete !
        log.info(String.format("[%s] v%s successfully disabled", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ShopItemClick(), this);

        // Register for serialization
        // Shops
        ConfigurationSerialization.registerClass(Threshold.class);
        ConfigurationSerialization.registerClass(ShopItemOptions.class);
        ConfigurationSerialization.registerClass(ShopItem.class);
        ConfigurationSerialization.registerClass(Shop.class);
        // Storage
        ConfigurationSerialization.registerClass(ShopPlayer.class);
        ConfigurationSerialization.registerClass(ShopPlayerShop.class);
        ConfigurationSerialization.registerClass(ShopPlayerItem.class);

        // Setup providers and config

        if (!setupEconomy()) {
            log.severe(String.format("[%s] No Vault found, disabling plugin...", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        shopConfig = new ShopConfig(this);

        setupPermissions();

        // Commands setup

        PluginCommand shop = getCommand("shop");
        shop.setExecutor(new ShopCommand(this));
        shop.setTabCompleter(new ShopTabCompleter());

        getCommand("shopslist").setExecutor(new ShopListCommand());

        // Log : Startup process complete !

        log.info(String.format("[%s] v%s successfully enabled", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermission() {
        return perms;
    }

    public static Logger getLocalLogger() {
        return log;
    }

    public static ShopConfig getShopConfig() {
        return shopConfig;
    }
}
