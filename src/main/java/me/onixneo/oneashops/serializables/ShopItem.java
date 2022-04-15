package me.onixneo.oneashops.serializables;

import me.onixneo.oneashops.OneaShops;
import me.onixneo.oneashops.settings.ShopConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItem implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("id", this.id);
        serializer.put("buy", this.buy.serialize());
        serializer.put("sell", this.sell.serialize());

        return serializer;
    }

    public ShopItem(Map<String, Object> serializedShopItem, Shop origin, ShopConfig cfg) {

        this.origin = origin;
        this.cfg = cfg;

        // Deserialization
        this.id = (String) serializedShopItem.get("id");
        this.buy = serializedShopItem.containsKey("buy") ? new ShopItemOptions((Map<String, Object>) serializedShopItem.get("buy"), this.id) : null;
        this.sell = serializedShopItem.containsKey("sell") ? new ShopItemOptions((Map<String, Object>) serializedShopItem.get("sell"), this.id) : null;

        // Shop Interface ItemStack Generation
        ItemStack stack = new ItemStack(Material.matchMaterial(this.id), 1);
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (this.buy != null) {
            lore.add("Clic gauche pour Achats");
        }

        if (this.sell != null) {
            lore.add("Clic droit pour Ventes");
        }

        meta.setLore(lore);
        stack.setItemMeta(meta);

        this.inventoryItem = stack;

        // Buy Interfaces Generation

        if (this.buy != null) {
            Inventory inv;

            ItemStack buyItem = new ItemStack(inventoryItem);
            ItemMeta buyMeta = buyItem.getItemMeta();
            List<String> buyLore;

            if (this.buy.getThresholds().size() != 0) {
                for (Threshold th : this.buy.getThresholds()) {
                    buyLore = new ArrayList<>();
                    buyLore.add(th.getPrice() + "$ / " + this.buy.getQuantity());
                    buyMeta.setLore(buyLore);
                    buyItem.setItemMeta(buyMeta);
                    buyItem.setAmount(this.buy.getQuantity());

                    inv = Bukkit.createInventory(null, 27, "Achats");
                    inv.setItem(12, cfg.getBackItem());
                    inv.setItem(14, buyItem);

                    buyInventories.add(inv);
                }
            }

            buyLore = new ArrayList<>();
            buyLore.add(this.buy.getBase() + "$ / " + this.buy.getQuantity());
            buyMeta.setLore(buyLore);
            buyItem.setItemMeta(buyMeta);
            buyItem.setAmount(this.buy.getQuantity());

            inv = Bukkit.createInventory(null, 27, "Achats");
            inv.setItem(12, cfg.getBackItem());
            inv.setItem(14, buyItem);

            buyInventories.add(inv);
        }

        // Sell Interfaces Generation

        if (this.sell != null) {
            Inventory inv;

            ItemStack sellItem = new ItemStack(inventoryItem);
            ItemMeta sellMeta = sellItem.getItemMeta();
            List<String> sellLore;

            if (this.sell.getThresholds().size() != 0) {
                for (Threshold th : this.sell.getThresholds()) {
                    sellLore = new ArrayList<>();
                    sellLore.add(th.getPrice() + "$ / " + this.sell.getQuantity());
                    sellMeta.setLore(sellLore);
                    sellItem.setItemMeta(sellMeta);
                    sellItem.setAmount(this.sell.getQuantity());

                    inv = Bukkit.createInventory(null, 27, "Ventes");
                    inv.setItem(12, cfg.getBackItem());
                    inv.setItem(14, sellItem);

                    sellInventories.add(inv);
                }
            }

            sellLore = new ArrayList<>();
            sellLore.add(this.sell.getBase() + "$ / " + this.sell.getQuantity());
            sellMeta.setLore(sellLore);
            sellItem.setItemMeta(sellMeta);
            sellItem.setAmount(this.sell.getQuantity());

            inv = Bukkit.createInventory(null, 27, "Ventes");
            inv.setItem(12, cfg.getBackItem());
            inv.setItem(14, sellItem);

            sellInventories.add(inv);
        }
    }

    public ShopItem() {};

    private Shop origin;
    private ShopConfig cfg;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private ShopItemOptions buy;

    public ShopItemOptions getBuy() {
        return buy;
    }

    public void setBuy(ShopItemOptions buy) {
        this.buy = buy;
    }

    private ShopItemOptions sell;

    public ShopItemOptions getSell() {
        return sell;
    }

    public void setSell(ShopItemOptions sell) {
        this.sell = sell;
    }

    private ItemStack inventoryItem;

    public ItemStack getInventoryItem() {
        return inventoryItem;
    }

    private List<Inventory> buyInventories = new ArrayList<>();

    public Inventory getBuyInventory(Player player) {
        if (this.buy == null) {
            return null;
        }

        ShopPlayer shopPlayer = cfg.getPlayerByUUID(player.getUniqueId());

        if (shopPlayer == null) {
            shopPlayer = new ShopPlayer();
            cfg.addPlayer(player, shopPlayer);
        }

        ShopPlayerShop playerShop = shopPlayer.getShops().stream().filter(shopPlayerShop -> shopPlayerShop.getFormattedname().equals(origin.getFormattedName())).findFirst().orElse(new ShopPlayerShop(origin));

        ShopPlayerItem playerItem = playerShop.getBought().stream().filter(shopPlayerItem -> shopPlayerItem.getId().equals(this.id)).findFirst().orElse(new ShopPlayerItem(this.id, 0));

        int i = 0;
        int c = 0;
        while (playerItem.getQuantity() >= c) {
            if (this.buy.getThresholds().size() == i) {
                return buyInventories.get(buyInventories.size() - 1);
            }
            c += this.buy.getThresholds().get(i).getQuantity();
            i++;
        }
        return buyInventories.get(i-1);
    }

    public void buy(Player player) {
        // Check if item has buy option

        if (this.buy == null) {
            return;
        }

        // Create item instance

        ItemStack item = new ItemStack(Material.matchMaterial(this.id), this.buy.getQuantity());

        // Check player inventory

        boolean fit = false;

        for (ItemStack invi : player.getInventory().getStorageContents()) {
            if (invi == null || invi.isSimilar(item) && invi.getMaxStackSize() >= invi.getAmount() + this.buy.getQuantity()) {
                fit = true;
            }
        }

        if (!fit) {
            player.sendMessage(ChatColor.GOLD + "OneaShops - " + ChatColor.RESET + "Vous n'avez aucun slot de libre dans votre inventaire.");
            return;
        }

        // Get player buy data

        ShopPlayer shopPlayer = cfg.getPlayerByUUID(player.getUniqueId());

        if (shopPlayer == null) {
            shopPlayer = new ShopPlayer();
            cfg.addPlayer(player, shopPlayer);
        }

        ShopPlayerShop playerShop = shopPlayer.getShops().stream().filter(shopPlayerShop -> shopPlayerShop.getFormattedname().equals(origin.getFormattedName())).findFirst().orElse(null);

        if (playerShop == null) {
            playerShop = new ShopPlayerShop(origin);
            shopPlayer.addShop(playerShop);
        }

        ShopPlayerItem playerItem = playerShop.getBought().stream().filter(shopPlayerItem -> shopPlayerItem.getId().equals(this.id)).findFirst().orElse(null);

        if (playerItem == null) {
            playerItem = new ShopPlayerItem(this.id, 0);
            playerShop.getBought().add(playerItem);
        }

        // Economy

        int amount = 0;

        int i = 0;
        int c = 0;
        while (playerItem.getQuantity() >= c) {
            if (this.buy.getThresholds().size() == i) {
                amount = this.buy.getBase();
                break;
            }
            c += this.buy.getThresholds().get(i).getQuantity();
            amount = this.buy.getThresholds().get(i).getPrice();
            i++;
        }

        double bal = OneaShops.getEconomy().getBalance(player);

        if (amount > bal) {
            player.sendMessage(ChatColor.GOLD + "OneaShops - " + ChatColor.RESET + "Vous n'avez pas assez d'argent.");
            return;
        } else {
            OneaShops.getEconomy().withdrawPlayer(player, amount);
        }

        // Save buying data

        playerItem.setQuantity(playerItem.getQuantity() + this.buy.getQuantity());

        cfg.savePlayer(player, shopPlayer);

        // Give item

        player.getInventory().addItem(item);

        // Send confirmation message

        player.sendMessage(ChatColor.GOLD + "OneaShops - " + ChatColor.RESET + "Vous avez acheté " + ChatColor.GOLD + this.buy.getQuantity() + " " + this.inventoryItem.getType().name() + ChatColor.RESET + " pour " + ChatColor.GOLD + amount + "$");

        // Update inventory

        player.openInventory(getBuyInventory(player));
    }

    private List<Inventory> sellInventories = new ArrayList<>();

    public Inventory getSellInventory(Player player) {
        if (this.sell == null) {
            return null;
        }

        ShopPlayer shopPlayer = cfg.getPlayerByUUID(player.getUniqueId());

        if (shopPlayer == null) {
            shopPlayer = new ShopPlayer();
            cfg.addPlayer(player, shopPlayer);
        }

        ShopPlayerShop playerShop = shopPlayer.getShops().stream().filter(shopPlayerShop -> shopPlayerShop.getFormattedname().equals(origin.getFormattedName())).findFirst().orElse(new ShopPlayerShop(origin));

        ShopPlayerItem playerItem = playerShop.getSold().stream().filter(shopPlayerItem -> shopPlayerItem.getId().equals(this.id)).findFirst().orElse(new ShopPlayerItem(this.id, 0));

        int i = 0;
        int c = 0;
        while (playerItem.getQuantity() >= c) {
            if (this.sell.getThresholds().size() == i) {
                return sellInventories.get(sellInventories.size() - 1);
            }
            c += this.sell.getThresholds().get(i).getQuantity();
            i++;
        }
        return sellInventories.get(i-1);
    }

    public void sell(Player player) {
        // Check if item has buy option

        if (this.sell == null) {
            return;
        }

        // Create item instance

        ItemStack item = new ItemStack(Material.matchMaterial(this.id), this.sell.getQuantity());

        // Check player inventory by trying to remove the item

        boolean ri = false;

        for (ItemStack ci : player.getInventory().getStorageContents()) {
            if (ci == null) continue;
            if (ci.getType() == item.getType() && ci.getAmount() >= item.getAmount()) {
                if (ci.getAmount() > item.getAmount()) {
                    ci.setAmount(ci.getAmount() - item.getAmount());
                } else if (ci.getAmount() == item.getAmount()) {
                    player.getInventory().removeItem(ci);
                }
                ri = true;
                break;
            }
        }

        if (!ri) {
            player.sendMessage(ChatColor.GOLD + "OneaShops - " + ChatColor.RESET + "Vous n'avez pas l'item à vendre.");
            return;
        }

        // Get player sell data

        ShopPlayer shopPlayer = cfg.getPlayerByUUID(player.getUniqueId());

        if (shopPlayer == null) {
            shopPlayer = new ShopPlayer();
            cfg.addPlayer(player, shopPlayer);
        }

        ShopPlayerShop playerShop = shopPlayer.getShops().stream().filter(shopPlayerShop -> shopPlayerShop.getFormattedname().equals(origin.getFormattedName())).findFirst().orElse(null);

        if (playerShop == null) {
            playerShop = new ShopPlayerShop(origin);
            shopPlayer.addShop(playerShop);
        }

        ShopPlayerItem playerItem = playerShop.getSold().stream().filter(shopPlayerItem -> shopPlayerItem.getId().equals(this.id)).findFirst().orElse(null);

        if (playerItem == null) {
            playerItem = new ShopPlayerItem(this.id, 0);
            playerShop.getSold().add(playerItem);
        }

        // Economy

        int amount = 0;

        int i = 0;
        int c = 0;
        while (playerItem.getQuantity() >= c) {
            if (this.sell.getThresholds().size() == i) {
                amount = this.sell.getBase();
                break;
            }
            c += this.sell.getThresholds().get(i).getQuantity();
            amount = this.sell.getThresholds().get(i).getPrice();
            i++;
        }

        OneaShops.getEconomy().depositPlayer(player, amount);

        // Save selling data

        playerItem.setQuantity(playerItem.getQuantity() + this.sell.getQuantity());

        cfg.savePlayer(player, shopPlayer);

        // Send confirmation message

        player.sendMessage(ChatColor.GOLD + "OneaShops - " + ChatColor.RESET + "Vous avez vendu " + ChatColor.GOLD + this.sell.getQuantity() + " " + this.inventoryItem.getType().name() + ChatColor.RESET + " pour " + ChatColor.GOLD + amount + "$");

        // Update inventory

        player.openInventory(getSellInventory(player));
    }
}
