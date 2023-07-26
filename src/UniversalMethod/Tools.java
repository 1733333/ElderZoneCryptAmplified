package UniversalMethod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class Tools {
    private static Tools instance = new Tools();
    private Tools(){}
    public static Tools getInstance() {
        return instance;
    }
    Random random = new Random();

    public String getLore(ItemStack item){
        if(!item.hasItemMeta())return "";
        ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore())return "";
        return meta.getLore().get(0);
    }

    public void addLore(ItemStack item,String[] itemLore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore;
        if (!meta.hasLore()) {
            lore = Arrays.asList(itemLore);
        } else {
            lore = meta.getLore();
            lore.addAll(Arrays.asList(itemLore));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public double distance(Location from,Location to){
        Location subLoc = to.clone().subtract(from.clone());
        return subLoc.length();
    }
    public void removeName(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();
        if(name.contains("：")){
            int index = name.indexOf("：");
            String newName = name.substring(index + 1);
            meta.setDisplayName(newName);
            item.setItemMeta(meta);
        }
    }

    public void shuffleInt(Integer[] numbers) {
        for (int i = numbers.length - 1; i > 0; i--) {
            int randomNum = random.nextInt(i + 1);
            int num = numbers[randomNum];
            numbers[randomNum] = numbers[i];
            numbers[i] = num;
        }
    }

    public void sendGifts(Player p, ItemStack[] items) {
        for(ItemStack item: items){
            if(item == null)continue;
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (itemMeta.hasLore()) {
                lore = itemMeta.getLore();
            }
            if(!lore.contains("§7Received from§e 金胡萝卜神")) {
                lore.add("§7Received from§e 金胡萝卜神");
            }
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin("DelayedGive");
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> gifts = new ArrayList<>();
        if (config.getList("delayedgifts." + p.getName(), null) != null)
            gifts = (List<ItemStack>) config.getList("delayedgifts." + p.getName(), null);
        for(ItemStack itemStack:items){
            if(itemStack == null)continue;
            gifts.add(itemStack);
        }
        config.set("delayedgifts." + p.getName(), gifts);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

}
