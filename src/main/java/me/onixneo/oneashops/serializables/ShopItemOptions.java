package me.onixneo.oneashops.serializables;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemOptions implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> serializer = new HashMap<>();

        serializer.put("base", this.base);
        serializer.put("quantity", this.quantity);

        ArrayList<Map<String, Object>> tempSerializedThreshold = new ArrayList<>();

        for (Threshold threshold : this.thresholds) {
            tempSerializedThreshold.add(threshold.serialize());
        }

        serializer.put("thresholds", tempSerializedThreshold);

        return serializer;
    }

    public ShopItemOptions(Map<String, Object> serializedShopItemOptions, String id) {
        this.base = serializedShopItemOptions.containsKey("base") ? (int) serializedShopItemOptions.get("base") : 0;

        if (serializedShopItemOptions.containsKey("quantity")) {
            this.quantity = (int) serializedShopItemOptions.get("quantity");
        } else {
            ItemStack stack = new ItemStack(Material.matchMaterial(id));
            this.quantity = stack.getMaxStackSize();
        }

        if (!serializedShopItemOptions.containsKey("thresholds")) {
            this.thresholds = new ArrayList<>();
            return;
        }

        ArrayList<Map<String, Object>> mappedThresholds = (ArrayList<Map<String, Object>>) serializedShopItemOptions.get("thresholds");
        ArrayList<Threshold> deserializedThresholds = new ArrayList<>();

        for (Map<String, Object> serializedThreshold : mappedThresholds) {
            deserializedThresholds.add(new Threshold(serializedThreshold));
        }

        this.thresholds = deserializedThresholds;
    }

    public ShopItemOptions() {};

    private int base;

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private List<Threshold> thresholds = new ArrayList<>();

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    public void addThreshold(Threshold threshold) {
        this.thresholds.add(threshold);
    }

    public void removeThreshold(Threshold threshold) {
        this.thresholds.remove(threshold);
    }
}
