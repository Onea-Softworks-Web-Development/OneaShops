package me.onixneo.oneashops.serializables;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopPlayerShop implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("formattedname", formattedname);

        ArrayList<Map<String, Object>> tempSerializedBought = new ArrayList<>();

        for (ShopPlayerItem item : bought) {
            tempSerializedBought.add(item.serialize());
        }

        serializer.put("bought", tempSerializedBought);

        ArrayList<Map<String, Object>> tempSerializedSold = new ArrayList<>();

        for (ShopPlayerItem item : sold) {
            tempSerializedSold.add(item.serialize());
        }

        serializer.put("sold", tempSerializedSold);

        return serializer;
    }

    public ShopPlayerShop (Map<String, Object> serializedShopPlayerShop) {
        // Formatted Name
        this.formattedname = (String) serializedShopPlayerShop.get("formattedname");

        // Bought items
        if (!serializedShopPlayerShop.containsKey("bought")) {
            this.bought = new ArrayList<>();
        } else {
            ArrayList<Map<String, Object>> mappedItems = (ArrayList<Map<String, Object>>) serializedShopPlayerShop.get("bought");
            ArrayList<ShopPlayerItem> deserializedItems = new ArrayList<>();

            for (Map<String, Object> serializedItem : mappedItems) {
                deserializedItems.add(new ShopPlayerItem(serializedItem));
            }

            this.bought = deserializedItems;
        }

        // Sold items
        if (!serializedShopPlayerShop.containsKey("sold")) {
            this.sold = new ArrayList<>();
        } else {
            ArrayList<Map<String, Object>> mappedItems = (ArrayList<Map<String, Object>>) serializedShopPlayerShop.get("sold");
            ArrayList<ShopPlayerItem> deserializedItems = new ArrayList<>();

            for (Map<String, Object> serializedItem : mappedItems) {
                deserializedItems.add(new ShopPlayerItem(serializedItem));
            }

            this.sold = deserializedItems;
        }
    }

    public ShopPlayerShop (Shop shop) {
        this.formattedname = shop.getFormattedName();
        this.bought = new ArrayList<>();
        this.sold = new ArrayList<>();
    }

    private String formattedname;

    public String getFormattedname() {
        return formattedname;
    }

    public void setFormattedname(String formattedname) {
        this.formattedname = formattedname;
    }

    private List<ShopPlayerItem> bought;

    public List<ShopPlayerItem> getBought() {
        return bought;
    }

    public void setBought(List<ShopPlayerItem> bought) {
        this.bought = bought;
    }

    public void addBought(ShopPlayerItem item) {
        this.bought.add(item);
    }

    private List<ShopPlayerItem> sold;

    public List<ShopPlayerItem> getSold() {
        return sold;
    }

    public void setSold(List<ShopPlayerItem> sold) {
        this.sold = sold;
    }

    public void addSold(ShopPlayerItem item) {
        this.sold.add(item);
    }
}
