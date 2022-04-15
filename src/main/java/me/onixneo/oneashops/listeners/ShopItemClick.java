package me.onixneo.oneashops.listeners;

import me.onixneo.oneashops.OneaShops;
import me.onixneo.oneashops.serializables.Shop;
import me.onixneo.oneashops.serializables.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopItemClick implements Listener {

    @EventHandler
    public void shopItemClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            for (Shop shop : OneaShops.getShopConfig().getShops()) {
                if (e.getInventory().equals(shop.getInventory())) {
                    e.setCancelled(true);

                    ShopItem shopItem = shop.getItems().get(e.getSlot());

                    if (e.getClick().isLeftClick() && shopItem.getBuy() != null) {
                        e.getWhoClicked().openInventory(shopItem.getBuyInventory((Player) e.getWhoClicked()));
                    }

                    if (e.getClick().isRightClick() && shopItem.getSell() != null) {
                        e.getWhoClicked().openInventory(shopItem.getSellInventory((Player) e.getWhoClicked()));
                    }

                    return;
                }

                for (ShopItem item : shop.getItems()) {

                    // Click on item buying menu
                    if (e.getInventory() == item.getBuyInventory((Player) e.getWhoClicked())) {
                        e.setCancelled(true);

                        // Click on the return item
                        if (e.getCurrentItem().equals(OneaShops.getShopConfig().getBackItem())) {
                            e.getWhoClicked().openInventory(shop.getInventory());
                        }

                        // Click on the buy item
                        if (e.getSlot() == 14) {
                            item.buy((Player) e.getWhoClicked());
                        }

                        return;
                    }

                    // Click on item selling menu
                    if (e.getInventory().equals(item.getSellInventory((Player) e.getWhoClicked()))) {
                        e.setCancelled(true);

                        // Click on the return item
                        if (e.getCurrentItem().equals(OneaShops.getShopConfig().getBackItem())) {
                            e.getWhoClicked().openInventory(shop.getInventory());
                        }

                        // Click on the sell stack item
                        if (e.getSlot() == 14) {
                            item.sell((Player) e.getWhoClicked());
                        }

                        return;
                    }
                }
            }
        }
    }
}
