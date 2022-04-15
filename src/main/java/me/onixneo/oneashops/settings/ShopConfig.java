package me.onixneo.oneashops.settings;

import me.onixneo.oneashops.OneaShops;
import me.onixneo.oneashops.serializables.Shop;
import me.onixneo.oneashops.serializables.ShopPlayer;
import me.onixneo.oneashops.serializables.ShopPlayerItem;
import me.onixneo.oneashops.serializables.ShopPlayerShop;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ShopConfig extends YamlConfiguration {

    private File defaultFile;
    private File shopDir;
    private File playerDir;
    private List<Shop> shops = new ArrayList<>();
    private HashMap<UUID, ShopPlayer> players = new HashMap<>();
    private static ItemStack backItem;
    private JavaPlugin plugin;

    public ShopConfig(JavaPlugin plugin) {

        this.plugin = plugin;

        // Generating backItem

        backItem = new ItemStack(Material.IRON_DOOR, 1);
        ItemMeta backMeta = backItem.getItemMeta();
        List<String> backLore = new ArrayList<>();
        backLore.add("Clic pour Retour");
        backMeta.setLore(backLore);
        backMeta.setDisplayName("Retour");
        backItem.setItemMeta(backMeta);

        // Reading shop files

        OneaShops.getLocalLogger().info(String.format("[%s] Starting to load config files...", plugin.getDescription().getName()));

        defaultFile = new File(plugin.getDataFolder(), "example.yml");
        shopDir = new File(plugin.getDataFolder(), "shops/");

        if (!defaultFile.exists()) {
            OneaShops.getLocalLogger().info(String.format("[%s] Default config not found. Creating one. (example.yml)", plugin.getDescription().getName()));
            defaultFile.getParentFile().mkdirs();
            plugin.saveResource("example.yml", false);
        }

        if (!shopDir.exists()) {
            OneaShops.getLocalLogger().info(String.format("[%s] Shop directory not found. Creating one. (shops/)", plugin.getDescription().getName()));
            shopDir.mkdirs();
        }

        for (File shopFile : shopDir.listFiles()) {
            OneaShops.getLocalLogger().info(String.format("[%s] Starting to load shop file %s", plugin.getDescription().getName(), shopFile.getName()));
            FileConfiguration shopConfig = new YamlConfiguration();

            try {
                shopConfig.load(shopFile);

                Shop shop = new Shop(shopConfig.getConfigurationSection("shop").getValues(true), this);

                shops.add(shop);
                OneaShops.getLocalLogger().info(String.format("[%s] Shop file %s successfully loaded.", plugin.getDescription().getName(), shopFile.getName()));

            } catch (Exception e) {
                OneaShops.getLocalLogger().warning(String.format("[%s] Error while loading %s", plugin.getDescription().getName(), shopFile.getName()));
            }
        }

        // Reading player files

        playerDir = new File(plugin.getDataFolder(), "players/");

        if (!playerDir.exists()) {
            OneaShops.getLocalLogger().info(String.format("[%s] Player directory not found. Creating one. (players/)", plugin.getDescription().getName()));
            playerDir.mkdirs();
        }

        for (File playerFile : playerDir.listFiles()) {
            OneaShops.getLocalLogger().info(String.format("[%s] Starting to load player file %s", plugin.getDescription().getName(), playerFile.getName()));
            FileConfiguration playerInfo = new YamlConfiguration();

            try {
                playerInfo.load(playerFile);

                ShopPlayer shopPlayer = new ShopPlayer(playerInfo.getValues(true));
                players.put(UUID.fromString(playerFile.getName().split("\\.")[0]), shopPlayer);

                OneaShops.getLocalLogger().info(String.format("[%s] Player file %s successfully loaded.", plugin.getDescription().getName(), playerFile.getName()));

            } catch (Exception e) {
                OneaShops.getLocalLogger().warning(String.format("[%s] Error while loading %s", plugin.getDescription().getName(), playerFile.getName()));
            }
        }
    }

    public HashMap<UUID, ShopPlayer> getPlayers() {
        return players;
    }

    public void savePlayer(UUID uuid, ShopPlayer shopPlayer) {
        File playerFile = new File(playerDir, uuid + ".yml");
        try {
            if (!playerFile.exists()) {
                playerFile.createNewFile();
            }
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerFile);

            ArrayList<Map<String, Object>> tempSerializedShops = new ArrayList<>();

            for (ShopPlayerShop shop : shopPlayer.getShops()) {
                tempSerializedShops.add(shop.serialize());
            }

            cfg.set("shops", tempSerializedShops);
            cfg.save(playerFile);
        } catch (Exception e) {
            OneaShops.getLocalLogger().warning(String.format("[%s] Error while writing player file %s", plugin.getDescription().getName(), playerFile.getName()));
        }
    }

    public void savePlayer(Player player, ShopPlayer shopPlayer) {
        savePlayer(player.getUniqueId(), shopPlayer);
    }

    public void addPlayer(UUID uuid, ShopPlayer shopPlayer) {
        if (players.containsKey(uuid)) {
            return;
        }

        players.put(uuid, shopPlayer);

        savePlayer(uuid, shopPlayer);
    }

    public void addPlayer(Player player, ShopPlayer shopPlayer) {
        addPlayer(player.getUniqueId(), shopPlayer);
    }

    public ShopPlayer getPlayerByUUID(UUID uuid) {
        return players.getOrDefault(uuid, null);
    }

    public List<Shop> getShops() {
        return shops;
    }

    public Shop getShopByName(String name) {
        return shops.stream().filter(shop -> shop.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Shop getShopByFormattedName(String formattedName) {
        return shops.stream().filter(shop -> shop.getFormattedName().equalsIgnoreCase(formattedName)).findFirst().orElse(null);
    }

    public ItemStack getBackItem() {
        return backItem;
    }
}
