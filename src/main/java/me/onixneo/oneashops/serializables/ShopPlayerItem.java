package me.onixneo.oneashops.serializables;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class ShopPlayerItem implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("id", this.id);
        serializer.put("quantity", this.quantity);

        return serializer;
    }

    public ShopPlayerItem (Map<String, Object> serializedShopPlayerItem) {
        this.id = (String) serializedShopPlayerItem.get("id");
        this.quantity = (int) serializedShopPlayerItem.get("quantity");
    }

    public ShopPlayerItem(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
