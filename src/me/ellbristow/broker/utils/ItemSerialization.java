package me.ellbristow.broker.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSerialization {
    
    public static String saveMeta(ItemMeta stack) {
        YamlConfiguration config = new YamlConfiguration();
        
        // Save every element in the list
        saveMeta(stack, config);
        return config.saveToString();
    }
    
    private static void saveMeta(ItemMeta item, ConfigurationSection destination) {
        // Save every element in the list
        destination.set("meta", item);
    }
    
    public static ItemMeta loadMeta(String data) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            // Load the string
            config.loadFromString(data);
        } catch (InvalidConfigurationException ex) {
            return null;
        }
        return loadMeta(config);
    }
    
    private static ItemMeta loadMeta(ConfigurationSection source) {
        return (ItemMeta) source.get("meta");
    }
    
}