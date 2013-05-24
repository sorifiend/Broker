package me.ellbristow.broker.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Lang {
    
    private static Map<String, String> strings = new HashMap<String, String>();
    private static FileConfiguration langStore;
    
    static {
        reload();
    }
    
    public static String get(String tag) {
        String found = strings.get(tag);
        if (!found.isEmpty())
            return found;
        return tag;
    }
    
    public static void reload() {
        File langFile = new File(Bukkit.getPluginManager().getPlugin("Broker").getDataFolder(),"lang.yml");
        langStore = YamlConfiguration.loadConfiguration(langFile);
        
        strings.clear();
        
        // General
        loadLangPhrase("By", "by");
        loadLangPhrase("ID", "ID");
        loadLangPhrase("Optional", "Optional");
        loadLangPhrase("Required", "Required");
        loadLangPhrase("Player", "Player");
        
        // Messages
        loadLangPhrase("CommandMessageOpen", "Open the broker window to buy items");
        loadLangPhrase("CommandMessageSellerName", "Seller Name");
        loadLangPhrase("CommandMessageItemName", "Item Name");
        loadLangPhrase("CommandMessageData", "data");
        loadLangPhrase("CommandMessageQuantity", "Quantity");
        loadLangPhrase("CommandMessageMaxPriceEach", "Max Price Each");
        loadLangPhrase("CommandMessagePlaceABuyOrder", "Place A Buy Order");
        loadLangPhrase("CommandMessageCancelABuyOrder", "Cancel A Buy Order");
        loadLangPhrase("CommandMessageBrowseOutstanding", "Browse Outstanding Buy Orders");
        loadLangPhrase("CommandMessagePrice", "Price");
        loadLangPhrase("CommandMessagePerItems", "Per # Items");
        loadLangPhrase("CommandMessageListForSale", "List the item in your hand for sale");
        loadLangPhrase("CommandMessageHowManyItems", "How many items for this price");
        loadLangPhrase("CommandMessageSetPriceOrSell", "Either set a price or sell to the highest current bidder");
        loadLangPhrase("CommandMessageCancelASellOrder", "Cancel A Sell Order");
        loadLangPhrase("CommandMessageCancellingBuyOrder", "Cancelling Buy Orders");
        loadLangPhrase("CommandMessageNotFound", "not found!");
        loadLangPhrase("CommandMessagePlayerName", "Player Name");
        loadLangPhrase("CommandMessageAdminBuyOrder", "List new ADMIN buy order");
        loadLangPhrase("CommandMessageAdminSellOrder", "List new ADMIN sell order");
        
        // Errors
        loadLangPhrase("ErrorNotFromConsole", "You cannot use Broker from the console!");
        loadLangPhrase("ErrorNoPerms", "You do not have permission to ");
        loadLangPhrase("ErrorNoPermsUse", "use");
        loadLangPhrase("ErrorNoPermsBuyCommand", "use the buy command!");
        loadLangPhrase("ErrorNoPermsBuyCancel", "cancel Buy Orders!");
        loadLangPhrase("ErrorNoPermsBuyCancelAdmin", "cancel Buy Orders as an admin!");
        loadLangPhrase("ErrorNotFound", "not found");
        
        try {
            langStore.save(langFile);
        } catch (IOException ex) {
            Bukkit.getLogger().severe("[Broker] Could not save " + langFile);
        }
    }
    
    private static void loadLangPhrase(String key, String defaultString) {
        String value = langStore.getString(key, defaultString);
        langStore.set(key, value);
        strings.put(key, value);
    }
    
}
