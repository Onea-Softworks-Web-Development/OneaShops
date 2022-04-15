package me.onixneo.oneashops.serializables;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopPlayer implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        ArrayList<Map<String, Object>> tempSerializedShops = new ArrayList<>();

        for (ShopPlayerShop shop : shops) {
            tempSerializedShops.add(shop.serialize());
        }

        serializer.put("shops", tempSerializedShops);

        return serializer;
    }

    public ShopPlayer (Map<String, Object> serializedShopPlayer) {
        // Shops
        if (!serializedShopPlayer.containsKey("shops")) {
            this.shops = new ArrayList<>();
        } else {
            ArrayList<Map<String, Object>> mappedShops = (ArrayList<Map<String, Object>>) serializedShopPlayer.get("shops");
            ArrayList<ShopPlayerShop> deserializedShop = new ArrayList<>();

            for (Map<String, Object> serializedShop : mappedShops) {
                deserializedShop.add(new ShopPlayerShop(serializedShop));
            }

            this.shops = deserializedShop;
        }
    }

    public ShopPlayer () {
        this.shops = new ArrayList<>();
    }

    private List<ShopPlayerShop> shops;

    public List<ShopPlayerShop> getShops() {
        return shops;
    }

    public void setShops(List<ShopPlayerShop> shops) {
        this.shops = shops;
    }

    public void addShop(ShopPlayerShop shop) {
        this.shops.add(shop);
    }
}
