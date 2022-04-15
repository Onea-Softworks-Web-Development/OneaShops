package me.onixneo.oneashops.serializables;

import me.onixneo.oneashops.settings.ShopConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("name", this.name);
        serializer.put("formattedname", this.formattedName);

        ArrayList<Map<String, Object>> tempSerializedItems = new ArrayList<>();

        for (ShopItem item : this.items) {
            tempSerializedItems.add(item.serialize());
        }

        serializer.put("items", tempSerializedItems);

        return serializer;
    }

    public Shop(Map<String, Object> serializedShop, ShopConfig cfg) {
        this.name = (String) serializedShop.get("name");

        this.formattedName = serializedShop.containsKey("formattedname") ? (String) serializedShop.get("formattedname") : this.name.toLowerCase().replaceAll(" ", "_");

        Inventory shopInterface = Bukkit.createInventory(null, 54, this.name);

        if (!serializedShop.containsKey("items")) {
            this.items = new ArrayList<>();
        } else {
            ArrayList<Map<String, Object>> mappedItems = (ArrayList<Map<String, Object>>) serializedShop.get("items");
            ArrayList<ShopItem> deserializedItems = new ArrayList<>();

            for (Map<String, Object> serializedItem : mappedItems) {
                deserializedItems.add(new ShopItem(serializedItem, this, cfg));
            }

            this.items = deserializedItems;

            for (ShopItem item : this.items) {
                shopInterface.setItem(shopInterface.firstEmpty(), item.getInventoryItem());
            }
        }

        this.inventory = shopInterface;
    }

    public Shop() {};

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.formattedName = name.toLowerCase().replaceAll(" ", "_");
    }

    private String formattedName;

    public String getFormattedName() {
        return formattedName;
    }

    private List<ShopItem> items = new ArrayList<>();

    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public void addItem(ShopItem item) {
        this.items.add(item);
    }

    public void removeItem(ShopItem item) {
        this.items.remove(item);
    }

    private Inventory inventory;

    public Inventory getInventory() {
        return inventory;
    }
}
