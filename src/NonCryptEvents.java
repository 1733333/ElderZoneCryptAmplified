import Pool.MaterialPool;
import UniversalMethod.Tools;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

public class NonCryptEvents implements Listener {
    JavaPlugin plugin;
    Tools tools = Tools.getInstance();
    MaterialPool materialPool = MaterialPool.getInstance();
    Random random = new Random();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent deathEvent){
        Entity e = deathEvent.getEntity();
        if(e.getName().contains("滨州")){
            deathEvent.getDrops().clear();
        }
    }
    @EventHandler
    public void playerJoin(PlayerJoinEvent joinEvent){
        Player p = joinEvent.getPlayer();
        Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + ChatColor.AQUA + "加入了游戏");
        for(Player op : Bukkit.getOnlinePlayers()){
            op.playSound(op.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1,2);
        }
    }
    @EventHandler
    public void playerQuit(PlayerQuitEvent quitEvent){
        Player p = quitEvent.getPlayer();
        Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + ChatColor.RED + "退出了游戏");
        for(Player op : Bukkit.getOnlinePlayers()){
            op.playSound(op.getLocation(),Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,1,1);
            op.playSound(op.getLocation(),Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,1,1);
        }
    }

    @EventHandler
    public void itemSpawn(ItemSpawnEvent spawnEvent) {
        Item item = spawnEvent.getEntity();
        ItemStack itemStack = item.getItemStack();
        if(itemStack.getItemMeta() == null)return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        item.setCustomName(itemMeta.getDisplayName());
        item.setCustomNameVisible(true);
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent chatEvent){
        Player sender = chatEvent.getPlayer();
        Server server = sender.getServer();
        String message = chatEvent.getMessage();
        for (Player onlinePlayer : server.getOnlinePlayers())
            onlinePlayer.playSound(onlinePlayer.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        if(message.contains("@")){
            int atIndex = message.indexOf("@");
            String name;
            if(message.contains(" ")){
                int endIndex = message.indexOf(" ");
                name = message.substring(atIndex + 1,endIndex);
            }else {
                name = message.substring(atIndex + 1);
            }
            for (Player onlinePlayer : server.getOnlinePlayers()){
                String onlinePlayerName = onlinePlayer.getName();
                if(message.contains("@所有") || message.contains("@全体")) {
                    chatEvent.setMessage(ChatColor.AQUA + message);
                    if (onlinePlayer.getName().equals(sender.getName())) continue;
                    onlinePlayer.sendTitle(ChatColor.AQUA + sender.getName(), ChatColor.YELLOW + "提到了你", 10, 50, 10);
                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    return;
                }
                if(name.contains(onlinePlayerName)){
                    String newMessage = message.replace("@" + onlinePlayerName,ChatColor.AQUA + "@" + onlinePlayerName + ChatColor.RESET);
                    onlinePlayer.sendTitle(ChatColor.AQUA + sender.getName(), ChatColor.YELLOW + "提到了你", 10, 50, 10);
                    onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    chatEvent.setMessage(newMessage);
                    return;
                }
            }
            for (OfflinePlayer offlinePlayer : server.getOfflinePlayers()){
                if(offlinePlayer.getName() == null)return;
                String offlineName = offlinePlayer.getName();
                if(name.contains(offlineName)){
                    String newMessage = message.replace("@" + offlineName,ChatColor.GRAY + "@" + offlineName + ChatColor.RESET);
                    chatEvent.setMessage(newMessage);
                    return;
                }
            }
        }
    }
    @EventHandler
    public void playerClick(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) return;
        String tag = tools.getLore(hand);
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            interactEvent.setCancelled(true);
            switch (tag) {
                case "§7SweepTicket":
                    sweepTicket(p, hand);
                    break;
                case "§7SniperRifle":
                    p.setWalkSpeed(-0.25f);
                default:
                    interactEvent.setCancelled(false);
            }
        }
    }
    @EventHandler
    public void playerShootBow(EntityShootBowEvent shootBowEvent){
        Entity e = shootBowEvent.getEntity();
        float force = shootBowEvent.getForce();
        if(e instanceof Player){
            Player p = (Player)e;
            World w = p.getWorld();
            ItemStack hand = p.getInventory().getItemInMainHand();
            if (hand.getType() == Material.AIR) return;
            String tag = tools.getLore(hand);
            switch (tag){
                case "§7SniperRifle":
                    p.setWalkSpeed(0.2f);
                    Location shootLoc = p.getEyeLocation();
                    Location pLoc = p.getLocation();
                    Vector shootVec = shootLoc.getDirection();
                    Arrow bullet = w.spawnArrow(shootLoc,shootVec,10 * force,0);
                    bullet.setDamage(2 * force);
                    bullet.setShooter(p);
                    bullet.setColor(Color.WHITE);
                    bullet.setTicksLived(1200);
                    shootBowEvent.setProjectile(bullet);
                    w.playSound(shootLoc,Sound.ENTITY_LIGHTNING_BOLT_IMPACT,1,1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE,shootLoc.add(shootVec),1);
                    float pitch = pLoc.getPitch();
                    pLoc.setPitch(Math.max(-90,pitch - 30));
                    p.teleport(pLoc);
            }
        }
    }
    @EventHandler
    public void interactAtEntity(PlayerInteractAtEntityEvent interact){
        Entity e = interact.getRightClicked();
        if(e.getName().contains("滨州")){
            interact.setCancelled(true);
        }
    }
    @EventHandler
    public void playerConsume(PlayerItemConsumeEvent consumeEvent) {
        Player p = consumeEvent.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) return;
        String tag = tools.getLore(hand);
        consumeEvent.setCancelled(true);
        switch (tag) {
            case "§7RawCola":
                BinZhou(p);
                p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                break;
            default:
                consumeEvent.setCancelled(false);
        }
    }

    public void sweepTicket(Player p,ItemStack hand){
        if(p.getGameMode() != GameMode.CREATIVE) {
            hand.setAmount(hand.getAmount() - 1);
        }
        ItemStack[]betterItems = materialPool.betterMaterials(10);
        ItemStack[]items = materialPool.randomMaterial(60);
        tools.sendGifts(p,betterItems);
        tools.sendGifts(p,items);
        p.playSound(p.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
        p.sendTitle(" ",ChatColor.YELLOW + "奖励已经发送到你的礼物箱",10,40,10);
        ComponentBuilder baseText = (new ComponentBuilder(ChatColor.YELLOW + "金胡萝卜神给你赠送了礼物").color(net.md_5.bungee.api.ChatColor.YELLOW).append(",使用").color(net.md_5.bungee.api.ChatColor.GRAY));
        TextComponent clickableText = new TextComponent("/giftlist");
        clickableText.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        clickableText.setUnderlined(Boolean.TRUE);
        clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("点击即可快速输入指令")).color(net.md_5.bungee.api.ChatColor.GRAY).create()));
        clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/giftlist"));
        baseText.append(clickableText).append("来打开你的礼物箱").color(net.md_5.bungee.api.ChatColor.GRAY).underlined(false);
        p.spigot().sendMessage(baseText.create());
    }

    public void BinZhou(Player p){
        World w = p.getWorld();
        Location eyeLoc = p.getEyeLocation();
        Vector eyeVec = eyeLoc.getDirection();
        ArmorStand binZhou = theD(p,eyeLoc.add(eyeVec));
        BukkitRunnable shoot = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                count += 1;
                if(count > 5 || binZhou.isDead()){
                    this.cancel();
                    binZhou.remove();
                    return;
                }
                Location loc = binZhou.getEyeLocation();
                Vector vec = loc.getDirection();
                if(random.nextInt(4) == 0){
                    for(int i = 0;i < 20;i ++){
                        Arrow cum = w.spawnArrow(loc,vec,2,50);
                        cum.setShooter(binZhou);
                        cum.setTicksLived(1200);
                        cum.setDamage(20);
                        cum.setColor(Color.WHITE);
                        w.playSound(loc,Sound.BLOCK_SLIME_BLOCK_BREAK,1,1);
                    }
                }else {
                    for(int i = 0;i < 10;i ++){
                        Arrow water = w.spawnArrow(loc,vec,3,30);
                        water.setShooter(binZhou);
                        water.setTicksLived(1200);
                        water.setDamage(10);
                        water.setColor(Color.YELLOW);
                        w.playSound(loc,Sound.ENTITY_GENERIC_SWIM,1,2);
                    }
                }
            }
        };
        shoot.runTaskTimer(plugin,20L,20L);
    }
    public ArmorStand theD(Player p,Location location){
        World w = location.getWorld();
        ArmorStand stand = (ArmorStand) w.spawnEntity(location, EntityType.ARMOR_STAND);
        EntityEquipment equipment = stand.getEquipment();
        ItemStack head = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta headMeta = (LeatherArmorMeta) head.getItemMeta();
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        headMeta.setColor(Color.FUCHSIA);
        chestMeta.setColor(Color.FUCHSIA);
        legMeta.setColor(Color.FUCHSIA);
        bootsMeta.setColor(Color.FUCHSIA);
        head.setItemMeta(headMeta);
        chest.setItemMeta(chestMeta);
        leg.setItemMeta(legMeta);
        boots.setItemMeta(bootsMeta);
        equipment.setHelmet(head);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boots);
        stand.setCustomName(ChatColor.LIGHT_PURPLE + p.getName() +" 的滨州");
        stand.setCustomNameVisible(true);
        return stand;
    }
}
