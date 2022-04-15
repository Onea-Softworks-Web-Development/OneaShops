package me.onixneo.oneashops.serializables;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class Threshold implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("price", this.price);
        serializer.put("quantity", this.price);

        return serializer;
    }

    public Threshold(Map<String, Object> serializedThreshold) {
        this.price = (int) serializedThreshold.get("price");
        this.quantity = (int) serializedThreshold.get("quantity");
    }

    public Threshold() {}

    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
