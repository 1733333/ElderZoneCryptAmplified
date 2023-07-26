import Listeners.Abilities;
import Pool.WeaponPool;
import UniversalMethod.Tools;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.*;

public class PlayerListener implements Listener {
    WeaponPool weaponPool = WeaponPool.getInstance();
    Tools tools = Tools.getInstance();
    Abilities ability = Abilities.getInstance();
    HashMap<String,HashSet<String>> isCoolingDown=new HashMap<>();
    HashMap<String, ArrayList<BossBar>> playerCoolDownBar = new HashMap<>();
    HashMap<String,Mob>playerMob = new HashMap<>();
    HashMap<String,Integer>recordedProjectile = new HashMap<>();
    HashMap<String,Integer>playerCombo = new HashMap<>();
    HashMap<String,BukkitRunnable>playerComboTask = new HashMap<>();
    HashMap<String,Integer>killCount = new HashMap<>();
    HashMap<String,Integer>bullets = new HashMap<>();
    HashSet<String>bloodThirst = new HashSet<>();
    JavaPlugin plugin;
    Random random = new Random();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public boolean weaponCoolDown(Player p, String weapon){
        if(!(isCoolingDown.containsKey(p.getName()))){
            HashSet<String> playerCoolDown=new HashSet<>();
            isCoolingDown.put(p.getName(),playerCoolDown);
        }
        return !isCoolingDown.get(p.getName()).contains(weapon);
    }
    public void weaponWarmUP(Player p, String weapon, long time, BarColor color){
        BossBar bar = Bukkit.createBossBar(weapon, color, BarStyle.SOLID);
        String name = p.getName();
        ArrayList<BossBar>playerBar = playerCoolDownBar.getOrDefault(p.getName(),new ArrayList<>());
        bar.addPlayer(p);
        playerBar.add(bar);
        playerCoolDownBar.put(p.getName(),playerBar);
        BukkitRunnable coolDown = new BukkitRunnable() {
            final double step = 1.0/(int)time;
            double progress = 1;
            @Override
            public void run() {
                if(progress > 1){
                    this.cancel();
                    return;
                }
                bar.setProgress(progress);
                progress -= step;
                if(progress <= 0 || !isCoolingDown.get(name).contains(weapon)) {
                    bar.removePlayer(p);
                    this.cancel();
                    isCoolingDown.get(name).remove(weapon);
                }
            }
        };
        if(weaponCoolDown(p, weapon)){
            isCoolingDown.get(p.getName()).add(weapon);
        }
        coolDown.runTaskTimer(plugin,0L,1L);
    }
    public void removeAllCoolDown(Player p, String weapon){
        ArrayList<BossBar>playerBar = playerCoolDownBar.getOrDefault(p.getName(),new ArrayList<>());
        if(playerBar.size() == 0)return;
        for(BossBar bar : playerBar){
            bar.setProgress(0);
            bar.removeAll();
        }
        isCoolingDown.get(p.getName()).clear();
        isCoolingDown.get(p.getName()).add(weapon);
        weaponWarmUP(p, weapon, 200L, BarColor.RED);
    }
    public void removeCoolDown(Player p, String weapon){
        ArrayList<BossBar>playerBar = playerCoolDownBar.getOrDefault(p.getName(),new ArrayList<>());
        if(playerBar.size() == 0)return;
        for(BossBar bar : playerBar){
            if(bar.getTitle().equals(weapon)) {
                bar.setProgress(0);
                bar.removeAll();
            }
        }
        isCoolingDown.get(p.getName()).remove(weapon);
    }

    @EventHandler
    public void entityDeathEvent(EntityDeathEvent deathEvent){
        LivingEntity dead = deathEvent.getEntity();
        Player killer = dead.getKiller();
        if(killer != null){
            World w = killer.getWorld();
            if(!(w.getName().equals("world")))return;
            ItemStack hand = killer.getInventory().getItemInMainHand();
            String name = killer.getName();
            if (!hand.hasItemMeta()) return;
            if (hand.getType() == Material.AIR) return;
            String tag = tools.getLore(hand);
            if(tag.equals(ChatColor.GRAY + "ChopAxe")){
                int count = killCount.getOrDefault(name,0);
                if(!bloodThirst.contains(name) && count < 9){
                    count += 1;
                    killCount.put(name,count);
                    killer.sendTitle(" ",ChatColor.RED + "击杀计数: " + count,0,20,10);
                }else if(count >= 9){
                    killCount.remove(name);
                    bloodThirst.add(name);
                    w.playSound(killer.getLocation(),Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED,1,1);
                    killer.sendTitle(" ",ChatColor.RED + "你进入了嗜血状态",0,20,10);
                    BukkitRunnable cancel = new BukkitRunnable() {
                        @Override
                        public void run() {
                            bloodThirst.remove(name);
                            w.playSound(killer.getLocation(),Sound.BLOCK_FIRE_EXTINGUISH,1,1);
                            killer.sendTitle(" ",ChatColor.RED + "嗜血状态结束了",0,20,10);
                        }
                    };
                    cancel.runTaskLater(plugin,300L);
                }
            }
        }
    }
    @EventHandler
    public void clickAtEntity(PlayerInteractAtEntityEvent interact){
        Player p = interact.getPlayer();
        World w = p.getWorld();
        if(!(w.getName().equals("world")))return;
        Entity entity = interact.getRightClicked();
        ItemStack hand = p.getInventory().getItemInMainHand();
        String name = p.getName();
        if (!hand.hasItemMeta()) return;
        if (hand.getType() == Material.AIR) return;
        String tag = tools.getLore(hand);
        if(entity instanceof Mob){
            Mob mob = (Mob) entity;
            if(tag.equals(ChatColor.GRAY + "MonsterStaff")){
                if(weaponCoolDown(p,tag)) {
                    weaponWarmUP(p,tag,2L,BarColor.WHITE);
                    if (playerMob.getOrDefault(name, null) == null) {
                        playerMob.put(name, mob);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    } else {
                        Mob saved = playerMob.get(name);
                        if (saved != entity) {
                            saved.setTarget(mob);
                            mob.setTarget(saved);
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                            playerMob.remove(name);
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void testGuy(EntityDamageEvent damageEvent){
        Entity damaged = damageEvent.getEntity();
        String name = damaged.getName();
        double damage =  damageEvent.getFinalDamage();
        if(Objects.equals(name, "不死半兵卫")){
            if(!damageEvent.isCancelled()) {
                List<Entity>nearby = damaged.getNearbyEntities(20,20,20);
                for(Entity e : nearby){
                    if(e instanceof Player){
                        e.sendMessage(name + ":这一下，对我造成了" + String.format("%.2f",damage) + "点伤害，伤害类型为" + damageEvent.getCause());
                    }
                }
            }
        }
    }
    @EventHandler
    public void playerAttack(EntityDamageByEntityEvent damageByEntityEvent){
        Entity damaged = damageByEntityEvent.getEntity();
        Entity attacker = damageByEntityEvent.getDamager();
        double damage = damageByEntityEvent.getDamage();
        World w = attacker.getWorld();
        if(!(w.getName().equals("world")))return;
        if(attacker instanceof Player){
            Player p = (Player) attacker;
            double attackDamage = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
            double attackSpeed = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getValue();
            if(damageByEntityEvent.getDamage() >= attackDamage * 0.8){
                long delay = (long) ((5 - attackSpeed) * 15L);
                combo(p,delay);
            }else {
                String message = ChatColor.RED + "你攻击的太快了！没有积攒到连击";
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
            }
            ItemStack hand = p.getInventory().getItemInMainHand();
            if(!hand.hasItemMeta())return;
            String name = hand.getItemMeta().getDisplayName();
            if(hand.getType() !=Material.AIR) {
                String tag = tools.getLore(hand);
                if (hand.getType().name().contains("AXE")) {
                    chopLimb(p, (LivingEntity) damaged, tag.contains("ChopAxe"));
                }
                switch (tag) {
                    case "§7Sword":
                    case "§7SharpSword":
                    case "§7TamaKiri":
                    case "§7OpSword":
                        if (weaponCoolDown(p, ChatColor.WHITE + "无敌时间")) {
                            weaponWarmUP(p, ChatColor.WHITE + "无敌时间", 18L, BarColor.WHITE);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 5));
                        }
                        break;
                    case "§7ShieldAxe":
                        if (weaponCoolDown(p, ChatColor.WHITE + "无敌时间")) {
                            weaponWarmUP(p, ChatColor.WHITE + "无敌时间", 28L, BarColor.WHITE);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 25, 5));
                        }
                        break;
                    case "§7ReflexAxe":
                    case "§7OpSwordKiwami":
                        int recordedDamage = recordedProjectile.getOrDefault(p.getName(), 0);
                        damageByEntityEvent.setDamage(damage + (recordedDamage * 2));
                        recordedProjectile.put(p.getName(), 0);
                        String message = ChatColor.GREEN + "你造成了" + String.format("%.2f", damageByEntityEvent.getFinalDamage()) + "点伤害";
                        if (recordedDamage != 0) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                        }
                        break;
                    case "§7BoomerAxe":
                        removeCoolDown(p, name);
                        break;
                    case "§7Stick":
                        if (random.nextInt(100) == 0) {
                            LivingEntity mob = (LivingEntity) damaged;
                            mob.setHealth(0);
                            w.playSound(mob.getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 2, 1);
                        }
                        break;
                    case "§7Hammer":
                        if (random.nextInt(3) == 0) {
                            LivingEntity mob = (LivingEntity) damaged;
                            mob.damage(20);
                            mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 10));
                            Particle.DustOptions dust = new Particle.DustOptions(Color.YELLOW, 0.5f);
                            w.spawnParticle(Particle.REDSTONE, mob.getEyeLocation(), 20, 0.5, 0.5, 0.5, dust);
                            w.playSound(mob.getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1);
                        }
                        break;
                    case "§7GrenadeStick":
                        if (random.nextInt(10) == 1) {
                            w.spawnParticle(Particle.EXPLOSION_HUGE, p.getEyeLocation(), 1);
                            ability.explosion(p, p.getLocation(), 50, 5, 5, false, null);
                        }
                        break;
                    case "§7DiceHammer":
                        double finalDamage = random.nextInt(25) + 6;
                        damageByEntityEvent.setDamage(finalDamage);
                        String message1 = ChatColor.WHITE + "你造成了" + finalDamage + "点伤害";
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message1));
                        break;
                    case "§7Shield":
                        ability.shieldAttack(p, (LivingEntity) damaged,1,10);
                        break;
                    case "§7FireShield":
                        ability.shieldAttack(p, (LivingEntity) damaged,1,15);
                        break;
                    case "§7LavaShield":
                        ability.shieldAttack(p, (LivingEntity) damaged,1,20);
                        break;
                    case "§7WaterShield":
                        ability.shieldAttack(p, (LivingEntity) damaged,2,18);
                        break;
                    case "§7IceShield":
                        ability.shieldAttack(p, (LivingEntity) damaged,2,25);
                        break;
                    case "§7AShield":
                        ability.shieldAttack(p, (LivingEntity) damaged,4,30);
                        break;
                }
            }
        }
        if(damaged instanceof Player) {
            Player p = (Player) damaged;
            int elite = CryptEvent.playerElite.getOrDefault(p.getName(),-1);
            EntityDamageEvent.DamageCause cause = damageByEntityEvent.getCause();
            if(elite == 0){
                if(cause == EntityDamageEvent.DamageCause.PROJECTILE || cause == EntityDamageEvent.DamageCause.CUSTOM){
                    damageByEntityEvent.setDamage(damage * 2);
                }
            }
            ItemStack hand = p.getInventory().getItemInMainHand();
            if(!hand.hasItemMeta())return;
            if(hand.getType() !=Material.AIR) {
                String tag = tools.getLore(hand);
                switch (tag) {
                    case "§7LavaShield":
                        if(p.isBlocking()){
                            if(weaponCoolDown(p,ChatColor.GOLD + "熔岩溅射")){
                                weaponWarmUP(p,ChatColor.GOLD +"熔岩溅射",100L,BarColor.YELLOW);
                                Location shootLoc = p.getEyeLocation();
                                Vector shootVec = shootLoc.getDirection();
                                w.playSound(shootLoc, Sound.ITEM_FIRECHARGE_USE, 2, 1);
                                for (int i = 0; i < 10; i++) {
                                    Arrow fire = w.spawnArrow(shootLoc, shootVec, 1, 50);
                                    fire.setFireTicks(1200);
                                    fire.setTicksLived(1200);
                                    fire.setShooter(p);
                                    fire.setDamage(10);
                                }
                                w.spawnParticle(Particle.EXPLOSION_LARGE, shootLoc.add(shootVec), 1);
                            }
                        }
                        break;
                    case "§7IceShield":
                        if(p.isBlocking()){
                            if(weaponCoolDown(p,ChatColor.AQUA + "冰刺")){
                                weaponWarmUP(p,ChatColor.AQUA +"冰刺",100L,BarColor.BLUE);
                                w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                ability.shootFreezeMagic(p,10,0,3,5,1,Color.AQUA);
                            }
                        }
                        break;
                    case "§7AShield":
                        if (p.isBlocking()){
                            if (random.nextBoolean()){
                                if(weaponCoolDown(p,ChatColor.AQUA + "冰刺")){
                                    weaponWarmUP(p,ChatColor.AQUA +"冰刺",60L,BarColor.BLUE);
                                    w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                    w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                    w.playSound(p.getLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
                                    ability.shootFreezeMagic(p,10,0,3,5,1,Color.AQUA);
                                }
                            }else {
                                if(weaponCoolDown(p,ChatColor.GOLD + "熔岩溅射")){
                                    weaponWarmUP(p,ChatColor.GOLD +"熔岩溅射",60L,BarColor.YELLOW);
                                    Location shootLoc = p.getEyeLocation();
                                    Vector shootVec = shootLoc.getDirection();
                                    w.playSound(shootLoc, Sound.ITEM_FIRECHARGE_USE, 2, 1);
                                    for (int i = 0; i < 10; i++) {
                                        Arrow fire = w.spawnArrow(shootLoc, shootVec, 1, 50);
                                        fire.setFireTicks(1200);
                                        fire.setTicksLived(1200);
                                        fire.setShooter(p);
                                        fire.setDamage(10);
                                    }
                                    w.spawnParticle(Particle.EXPLOSION_LARGE, shootLoc.add(shootVec), 1);
                                }
                            }
                        }
                }
            }
            if (!weaponCoolDown(p, ChatColor.WHITE + "格挡") && weaponCoolDown(p, ChatColor.WHITE + "格挡冷却")) {
                w.playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                ability.suck(p,p.getLocation(),2,-0.7);
                purify(p);
                removeCoolDown(p, ChatColor.WHITE + "格挡");
                weaponWarmUP(p, ChatColor.WHITE + "格挡冷却", 60L, BarColor.WHITE);
                damageByEntityEvent.setCancelled(true);
            }
            if (!weaponCoolDown(p, ChatColor.AQUA + "格挡") && weaponCoolDown(p, ChatColor.AQUA + "格挡冷却")) {
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                ability.suck(p,p.getLocation(),2,-0.7);
                purify(p);
                BukkitRunnable slash = new BukkitRunnable() {
                    @Override
                    public void run() {
                        areaSlash(p,3,10,Color.WHITE);
                        ability.suck(p,p.getLocation(),3,-1);
                    }
                };
                slash.runTaskLater(plugin,10L);
                removeCoolDown(p, ChatColor.AQUA + "格挡");
                weaponWarmUP(p, ChatColor.AQUA + "格挡冷却", 60L, BarColor.WHITE);
                damageByEntityEvent.setCancelled(true);
            }
            if (!weaponCoolDown(p, ChatColor.GRAY + "格挡") && weaponCoolDown(p, ChatColor.GRAY + "格挡冷却")) {
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                ability.suck(p,p.getLocation(),2,-0.7);
                boostedWarCry(p);
                removeCoolDown(p, ChatColor.GRAY + "格挡");
                weaponWarmUP(p, ChatColor.GRAY + "格挡冷却", 80L, BarColor.WHITE);
                damageByEntityEvent.setCancelled(true);
            }
            if (!weaponCoolDown(p, ChatColor.YELLOW + "格挡") && weaponCoolDown(p, ChatColor.YELLOW + "格挡冷却")) {
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                w.playSound(damaged.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
                ability.suck(p,p.getLocation(),2,-0.7);
                tamaKiri(p);
                removeCoolDown(p, ChatColor.YELLOW + "格挡");
                weaponWarmUP(p, ChatColor.YELLOW + "格挡冷却", 70L, BarColor.WHITE);
                damageByEntityEvent.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void playerThrow(PlayerDropItemEvent dropItemEvent) {
        Player p = dropItemEvent.getPlayer();
        World w = p.getWorld();
        if(!(w.getName().equals("world")))return;
        Item item = dropItemEvent.getItemDrop();
        ItemStack itemStack = item.getItemStack();
        String lore = tools.getLore(itemStack);
        switch (lore) {
            case "§7MonarchSwordIII":
                reArm(p);
                dropItemEvent.setCancelled(true);
                break;
            case "§7Meteor":
                ability.throwMeteor(p,itemStack);
                break;
        }
    }
    @EventHandler
    public void itemDamage(PlayerItemDamageEvent damageEvent){
        World w = damageEvent.getPlayer().getWorld();
        if(!(w.getName().equals("world")))return;
        damageEvent.setCancelled(true);
    }
    @EventHandler
    public void playerShootBow(EntityShootBowEvent shootBowEvent) {
        Entity entity = shootBowEvent.getEntity();
        World w = entity.getWorld();
        if(!(w.getName().equals("world")))return;
        Entity projectile = shootBowEvent.getProjectile();
        float force = shootBowEvent.getForce();
        projectile.setTicksLived(1200);
        if (entity instanceof Player) {
            Player shooter = (Player) entity;
            Inventory inv = shooter.getInventory();
            ItemStack bow = shootBowEvent.getBow();
            String lore = tools.getLore(bow);
            inv.remove(Material.ARROW);
            inv.addItem(new ItemStack(Material.ARROW));
            shootBowEvent.setCancelled(true);
            Location shootLoc = shooter.getEyeLocation();
            Vector shootVec = shootLoc.getDirection();
            switch (lore) {
                case "§7Bow":
                    shootBowEvent.setCancelled(false);
                    if (force == 1) {
                        w.playSound(shooter.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                        w.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                        w.playSound(shooter.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 2);
                        ability.shootMagic(shooter, 1, 1, 5, 3, 1, Color.WHITE);
                        shootBowEvent.setCancelled(true);
                    }
                    break;
                case "§7RainBow":
                    shootBowEvent.setCancelled(false);
                    if (force == 1) {
                        w.playSound(shooter.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                        w.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                        ability.rainArrow(shooter);
                        shootBowEvent.setCancelled(true);
                    }
                    break;
                case "§7FastBow":
                    shootBowEvent.setCancelled(false);
                    if(force > 0.25){
                        Arrow fastArrow = w.spawnArrow(shootLoc,shootVec,1.8f,6);
                        fastArrow.setDamage(7);
                        fastArrow.setShooter(shooter);
                        fastArrow.setTicksLived(1200);
                        w.playSound(shooter.getLocation(),Sound.BLOCK_CHAIN_PLACE,1,1);
                        w.playSound(shooter.getLocation(),Sound.BLOCK_CHAIN_PLACE,1,1);
                        w.playSound(shooter.getLocation(),Sound.ENTITY_ARROW_SHOOT,1,1.2f);
                        shootBowEvent.setCancelled(true);
                    }
                    break;
                case "§7IceBow":
                    shootBowEvent.setCancelled(false);
                    if(force > 0.2){
                        Arrow frostArrow = w.spawnArrow(shootLoc,shootVec,2f,5);
                        frostArrow.setDamage(8);
                        frostArrow.setShooter(shooter);
                        frostArrow.setTicksLived(1200);
                        frostArrow.setFreezeTicks(200);
                        frostArrow.setColor(Color.AQUA);
                        w.playSound(shooter.getLocation(),Sound.BLOCK_CHAIN_PLACE,1,1);
                        w.playSound(shooter.getLocation(),Sound.BLOCK_CHAIN_PLACE,1,1);
                        w.playSound(shooter.getLocation(),Sound.ENTITY_ARROW_SHOOT,1,1.2f);
                        shootBowEvent.setCancelled(true);
                    }
                    break;
                case "§7WildFireLauncher":
                    wildFireLauncher(shooter);
                    break;
                case "§7TripleCrossbow":
                    tripleCrossBow(shooter);
                    break;
                case "§7MachineCrossbow":
                    machineCrossbow(shooter);
                    break;
                case "§7MachineShotBow":
                    machineShotBow(shooter);
                    break;
                case "§7ShotCrossbow":
                    shotBow(shooter);
                    break;
                case "§7SmokeOni":
                    smokeBow(shooter,force);
                    break;
                case "§7DoubleBow":
                    doubleBow(shooter, 5 * force,force);
                    break;
                default:
                    shootBowEvent.setCancelled(false);
            }
        }
    }
    @EventHandler
    public void playerClick(PlayerInteractEvent interactEvent) {
        Action action = interactEvent.getAction();
        Player p = interactEvent.getPlayer();
        World w = p.getWorld();
        if(!(w.getName().equals("world")))return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (!hand.hasItemMeta()) return;
        ItemMeta handMeta = hand.getItemMeta();
        String name = handMeta.getDisplayName();
        if (hand.getType() == Material.AIR) return;
        String tag = tools.getLore(hand);
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            interactEvent.setCancelled(true);
            switch (tag) {
                case "§7VillarPerosa":
                    machineGun(p);
                    break;
                case "§7AShield":
                    if(weaponCoolDown(p,ChatColor.LIGHT_PURPLE + "Bending")){
                        weaponWarmUP(p, ChatColor.LIGHT_PURPLE + "Bending", 50L, BarColor.PINK);
                        ability.aShieldAbility(p);
                    }
                    break;
                case "§7BoomerAxe":
                    if (weaponCoolDown(p, name)) {
                        weaponWarmUP(p, name, 80L, BarColor.WHITE);
                        boomerAxe(p, hand);
                    }
                    break;
                case "§7EnchantedAxe":
                    if (weaponCoolDown(p, name)) {
                        weaponWarmUP(p, name, 60L, BarColor.BLUE);
                        enchantedAxe(p, hand);
                    }
                    break;
                case "§7AssassinAxe":
                    if (weaponCoolDown(p, name)) {
                        weaponWarmUP(p, name, 40L, BarColor.PINK);
                        assassinAxe(p, hand);
                    }
                    break;
                case "§7OpSword":
                    opSword(p, name);
                    break;
                case "§7OpSwordKiwami":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 20L, BarColor.GREEN);
                        opKiwami(p);
                    }
                    break;
                case "§7DeathStaff":
                    deathStaff(p);
                    break;
                case "§7MagicBook":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 50L, BarColor.BLUE);
                        ability.shootDelayedMagic(p, 3, 1, 2, 3,1, Color.AQUA);
                    }
                    break;
                case "§7ThickBook":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 40L, BarColor.BLUE);
                        ability.shootDelayedMagic(p, 5, 1, 2, 5,1, Color.AQUA);
                    }
                    break;
                case "§7BoostedBook":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 40L, BarColor.PURPLE);
                        ability.shootDelayedMagic(p, 7, 1, 3, 5,1, Color.PURPLE);
                    }
                    break;
                case "§7EnchantedSword":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 40L, BarColor.PURPLE);
                        ability.shootDelayedMagic(p, 9, 1, 3, 6, 1,Color.PURPLE);
                    }
                    break;
                case "§7BoostedEnchantedSword":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 35L, BarColor.PURPLE);
                        ability.shootDelayedMagic(p, 11, 1, 4, 5, 1,Color.PURPLE);
                    }
                    break;
                case "§7MasterSword":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 30L, BarColor.BLUE);
                        ability.masterSword(p);
                    }
                    break;
                case "§7Meteor":
                    ability.throwMeteor(p,hand);
                    break;
                case "§7SmokeSword":
                    if(weaponCoolDown(p,ChatColor.WHITE + "突进")) {
                        weaponWarmUP(p, ChatColor.WHITE + "突进", 100L, BarColor.WHITE);
                        if(((Entity)p).isOnGround()){
                            ability.dash(p,3);
                        }else {
                            ability.dash(p,1.5);
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,40,5));
                    }
                    break;
                case "§7TamaKiri":
                    if (weaponCoolDown(p, ChatColor.YELLOW + "格挡") && weaponCoolDown(p,ChatColor.YELLOW + "格挡冷却")) {
                        tamaParry(p);
                    }
                    break;
                case "§7Axe":
                    if (weaponCoolDown(p, ChatColor.WHITE + "战吼")) {
                        weaponWarmUP(p, ChatColor.WHITE + "战吼", 50L, BarColor.WHITE);
                        warCry(p);
                    }
                    break;
                case "§7ShieldAxe":
                    if (weaponCoolDown(p, ChatColor.GRAY + "格挡") && weaponCoolDown(p,ChatColor.GRAY + "格挡冷却")) {
                        shieldParry(p);
                    }
                    break;
                case "§7Sword":
                    if (weaponCoolDown(p, ChatColor.WHITE + "格挡") && weaponCoolDown(p,ChatColor.WHITE + "格挡冷却")) {
                        parry(p);
                    }
                    break;
                case "§7SharpSword":
                    if (weaponCoolDown(p, ChatColor.AQUA + "格挡") && weaponCoolDown(p,ChatColor.AQUA + "格挡冷却")) {
                        sharpParry(p);
                    }
                    break;
                case "§7MonarchSwordI":
                    shootMissile(p,false);
                    break;
                case "§7MonarchSwordII":
                    if(p.isSneaking()){
                        healthDrain(p);
                    }else {
                        shootMissile(p,false);
                    }
                    break;
                case "§7MonarchSwordIII":
                    if(p.isSneaking()){
                        healthDrain(p);
                    }else {
                        shootMissile(p,true);
                    }
                    break;
                case "§7TrashBin":
                    trashBin(p);
                    break;
                case "§7GuitarAxe":
                    guitarAxe(p);
                    break;
                case "§7ReflexAxe":
                    if (weaponCoolDown(p, name)) {
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        weaponWarmUP(p, name, 20L, BarColor.GREEN);
                        reflexAxe(p);
                    }
                    break;
                case "§7RandomBook":
                    if (weaponCoolDown(p, name)) {
                        weaponWarmUP(p, name, 50L, BarColor.PINK);
                        randomMagic(p);
                    }
                    break;
                case "§7Flail":
                    interactEvent.setCancelled(true);
                    if (weaponCoolDown(p, name)) {
                        weaponWarmUP(p, name, 40L, BarColor.WHITE);
                        ability.areaDamage(p.getLocation(), 4, 25);
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                        w.playSound(p.getEyeLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    }
                    break;
                case"§eGoldenCarrotZweiHander":
                    if (weaponCoolDown(p, ChatColor.YELLOW + "随机能力")) {
                        weaponWarmUP(p, ChatColor.YELLOW + "随机能力", 30L, BarColor.YELLOW);
                        randomAbility(p);
                    }
                    break;
                default:
                    interactEvent.setCancelled(false);
            }
        }
        if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
            switch (tag) {
                case "§eGoldenCarrotZweiHander":
                    if (weaponCoolDown(p, ChatColor.YELLOW + "剑气")) {
                        weaponWarmUP(p, ChatColor.YELLOW + "剑气", 40L, BarColor.YELLOW);
                        areaSlash(p,5,30,Color.YELLOW);
                    }
                    break;
                case "§7BoostedEnchantedSword":
                    if (weaponCoolDown(p, ChatColor.LIGHT_PURPLE + "剑气")) {
                        weaponWarmUP(p, ChatColor.LIGHT_PURPLE + "剑气", 30L, BarColor.PURPLE);
                        areaSlash(p,3,20,Color.FUCHSIA);
                    }
                    break;
                case "§7ChopAxe":
                    if(bloodThirst.contains(p.getName())) {
                        if (weaponCoolDown(p, ChatColor.RED + "血刃")) {
                            weaponWarmUP(p,ChatColor.RED + "血刃", 50L, BarColor.RED);
                            areaSlash(p, 5, 40, Color.RED);
                        }
                    }
                    break;
            }
        }
    }
    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent sneakEvent){
        Player p = sneakEvent.getPlayer();
        World w = p.getWorld();
        if(!(w.getName().equals("world")))return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        if(!hand.hasItemMeta())return;
        if (hand.getType() == Material.AIR) return;
        String tag = tools.getLore(hand);
        switch (tag){
            case"§7FireShield":
                if(weaponCoolDown(p,ChatColor.GOLD + "盾牌冲撞")) {
                    weaponWarmUP(p, ChatColor.GOLD + "盾牌冲撞", 80L, BarColor.YELLOW);
                    ability.shieldRush(p,3,20);
                }
                break;
            case "§7LavaShield":
                if(weaponCoolDown(p,ChatColor.GOLD + "盾牌冲撞")) {
                    weaponWarmUP(p, ChatColor.GOLD + "盾牌冲撞", 60L, BarColor.YELLOW);
                    ability.shieldRush(p,4,25);
                }
                break;
            case "§7AShield":
                if(weaponCoolDown(p,ChatColor.LIGHT_PURPLE + "盾牌冲撞")) {
                    weaponWarmUP(p, ChatColor.LIGHT_PURPLE + "盾牌冲撞", 60L, BarColor.PINK);
                    ability.shieldRush(p,5,30);
                }
                break;
            case "§7Bow":
            case "§7DoubleBow":
            case "§7CrossBow":
                if(weaponCoolDown(p,ChatColor.WHITE + "突进")) {
                    weaponWarmUP(p, ChatColor.WHITE + "突进", 100L, BarColor.WHITE);
                    if(((Entity)p).isOnGround()){
                        ability.dash(p,3);
                    }else {
                        ability.dash(p,1.5);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,40,5));
                }
                break;
        }
    }
    @EventHandler
    public void playerEat(PlayerItemConsumeEvent consumeEvent){
        Player p = consumeEvent.getPlayer();
        World w = p.getWorld();
        if(!(w.getName().equals("world")))return;
        ItemStack itemStack = consumeEvent.getItem();
        String tag = tools.getLore(itemStack);
        consumeEvent.setCancelled(true);
        switch (tag){
            case "§7Poison":
                p.setHealth(0);
                w.playSound(p.getEyeLocation(),Sound.ENTITY_PILLAGER_DEATH,1,1);
                Bukkit.broadcastMessage(ChatColor.YELLOW + p.getName() + ChatColor.RED +"吃下了昏睡红茶果实,被先辈撅回了主城");
                break;
            default:
                consumeEvent.setCancelled(false);
        }
    }

    public void trashBin(Player p){
        Inventory inv = Bukkit.createInventory(p,27,ChatColor.RED + "在里面放入没用的东西");
        p.openInventory(inv);
    }
    public void wildFireLauncher(Player p){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        Arrow grenade = w.spawnArrow(shootLoc,shootVec,1.2f,0);
        grenade.setFireTicks(1200);
        grenade.setDamage(20);
        w.playSound(shootLoc,Sound.ITEM_FIRECHARGE_USE,1,1);
        ability.thermiteGrenade(p,grenade,Particle.FLAME);
    }
    public void opSword(Player p,String name) {
        if (weaponCoolDown(p, name)) {
            weaponWarmUP(p,name,15L,BarColor.GREEN);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
            Vector vec = p.getEyeLocation().getDirection();
            Location fieldLoc = p.getEyeLocation().clone().add(vec);
            ability.reflectProjectile(p, fieldLoc, Color.LIME);
        }
    }
    public void boomerAxe(Player p,ItemStack item){
        ItemStack air = new ItemStack(Material.AIR);
        EntityEquipment equipment = p.getEquipment();
        equipment.setItemInMainHand(air);
        ability.throwBoomerang(p,item,15);
    }
    public void enchantedAxe(Player p,ItemStack item){
        ItemStack air = new ItemStack(Material.AIR);
        EntityEquipment equipment = p.getEquipment();
        equipment.setItemInMainHand(air);
        ability.throwMagicBoomerang(p,item,18);
    }
    public void assassinAxe(Player p,ItemStack item){
        ItemStack air = new ItemStack(Material.AIR);
        EntityEquipment equipment = p.getEquipment();
        equipment.setItemInMainHand(air);
        ability.throwBombAxe(p,item,20);
    }

    public void doubleBow(Player p,double damage,float force){
        World w = p.getWorld();
        w.playSound(p.getEyeLocation(),Sound.ENTITY_ARROW_SHOOT,1,1);
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection().clone();
        Vector forwardVec = p.getEyeLocation().getDirection().normalize();
        Vector downVec = new Vector(0, -1, 0).normalize();
        Vector rightVec = forwardVec.clone().crossProduct(downVec);
        Vector finalShootVec;
        Location finalShootLoc;
        for (int i = -1; i <= 1; i = i + 2) {
            finalShootLoc = shootLoc.clone().add(shootVec.clone());
            finalShootVec = shootVec.clone().add(rightVec.clone().multiply(0.2 * i));
            Arrow arrow = w.spawnArrow(finalShootLoc.clone(),finalShootVec.clone(), 3 * force, 0);
            arrow.setDamage(damage);
            arrow.setShooter(p);
            arrow.setTicksLived(1200);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
    }
    public void randomMagic(Player p){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        String message = "";
        switch (random.nextInt(4)){
            case 0:
                ability.fireWall(p);
                message = "你释放出了" + ChatColor.GOLD + "火墙";
                break;
            case 1:
                ability.arcWave(p);
                message = "你释放出了" + ChatColor.AQUA + "电弧波";
                break;
            case 2:
                message = "你释放出了" + ChatColor.BLUE + "爆散结晶";
                for(int i = 0 ;i < 30;i ++){
                    Arrow arrow = w.spawnArrow(shootLoc,shootVec,1,30);
                    arrow.setColor(Color.BLUE);
                    arrow.setShooter(p);
                    arrow.setDamage(20);
                    arrow.setTicksLived(1200);
                }
                break;
            case 3:
                message = "你释放出了" + ChatColor.RED + "法术";
                ability.shootMagic(p,13,0,3,7,1,Color.RED);
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
    }
    public void tripleCrossBow(Player p) {
        World w = p.getWorld();
        BukkitRunnable arrow1 = new BukkitRunnable() {
            int shot = 0;
            @Override
            public void run() {
                if(shot > 2){
                    this.cancel();
                    return;
                }
                shot += 1;
                Location shootLoc = p.getEyeLocation();
                Vector shootVec = p.getEyeLocation().getDirection();
                w.playSound(shootLoc, Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                Arrow arrow = w.spawnArrow(shootLoc, shootVec, 3, 2);
                arrow.setDamage(3);
                arrow.setShooter(p);
                arrow.setTicksLived(1200);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
        };
        arrow1.runTaskTimer(plugin, 0L,5L);
    }
    public void warCry(Player p){
        World w = p.getWorld();
        Location cryLoc = p.getEyeLocation();
        Vector cryVec = cryLoc.getDirection();
        w.playSound(cryLoc,Sound.ENTITY_ENDER_DRAGON_GROWL,0.3f,2);
        ability.explosion(p,cryLoc.add(cryVec),4,4,2,false,null);
    }
    public void boostedWarCry(Player p){
        World w = p.getWorld();
        Location cryLoc = p.getEyeLocation();
        Vector cryVec = cryLoc.getDirection();
        w.playSound(cryLoc,Sound.ENTITY_ENDER_DRAGON_GROWL,0.3f,2);
        ability.explosion(p,cryLoc.add(cryVec),8,5,2,false,null);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,80,0));
    }
    public void purify(Player p){
        World w = p.getWorld();
        w.playSound(p.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1,1);
        Collection<PotionEffect>effects = p.getActivePotionEffects();
        for(PotionEffect effect : effects){
            if(effect.getType().equals(PotionEffectType.NIGHT_VISION))continue;
            if(effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE))continue;
            p.removePotionEffect(effect.getType());

        }
        p.setFreezeTicks(0);
        p.setFireTicks(0);
    }
    public void tamaKiri(Player p){
        World w = p.getWorld();
        Collection<Entity>entities = p.getNearbyEntities(10,10,10);
        Particle.DustOptions dust = new Particle.DustOptions(Color.YELLOW, 1f);
        w.spawnParticle(Particle.REDSTONE, p.getEyeLocation(), 50, 1, 1, 1, dust);
        ArrayList<Location>locations = new ArrayList<>();
        for(Entity e:entities){
            if(e instanceof Projectile){
                locations.add(e.getLocation());
                e.remove();
            }
        }
        if(locations.size() == 0)return;
        int duration = locations.size() + 1;
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,duration * 40,1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,duration * 40,1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,duration * 40,1));
        BukkitRunnable kiri = new BukkitRunnable() {
            @Override
            public void run() {
                if(locations.size() == 0){
                    this.cancel();
                    return;
                }
                w.spawnParticle(Particle.SWEEP_ATTACK,locations.get(0),1);
                w.playSound(locations.get(0),Sound.ENTITY_PLAYER_ATTACK_SWEEP,5,1);
                w.playSound(locations.get(0),Sound.ENTITY_PLAYER_ATTACK_SWEEP,5,1);
                w.playSound(locations.get(0),Sound.ENTITY_PLAYER_ATTACK_SWEEP,5,1);
                locations.remove(0);
            }
        };
        kiri.runTaskTimer(plugin,0L,1L);
    }
    public void shootMissile(Player p,boolean boost){
        if (weaponCoolDown(p, ChatColor.GOLD + "帝王飞弹")) {
            weaponWarmUP(p, ChatColor.GOLD + "帝王飞弹", 80L, BarColor.YELLOW);
            ability.missiles(p,boost);
        }
    }
    public void healthDrain(Player p){
        if (weaponCoolDown(p, ChatColor.AQUA + "生命汲取")) {
            weaponWarmUP(p, ChatColor.AQUA + "生命汲取", 120L, BarColor.BLUE);
            ability.healthDrain(p);
        }
    }
    public void reArm(Player p){
        World w = p.getWorld();
        if (weaponCoolDown(p, ChatColor.RED + "再武装")) {
            weaponWarmUP(p, ChatColor.RED + "生命汲取", 200L, BarColor.RED);
            removeAllCoolDown(p,ChatColor.RED + "再武装");
            w.playSound(p.getLocation(),Sound.BLOCK_ANVIL_PLACE,1,2);
        }
    }
    public void shotBow(Player p){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation();
        Vector shootVec = shootLoc.getDirection();
        w.playSound(shootLoc,Sound.ITEM_CROSSBOW_SHOOT,1,1);
        for(int i = 0;i < 5;i++){
            Arrow arrow = w.spawnArrow(shootLoc,shootVec,2,10);
            arrow.setTicksLived(1200);
            arrow.setPierceLevel(10);
            arrow.setDamage(5);
            arrow.setShooter(p);
        }
    }

    public void machineCrossbow(Player p){
        World w = p.getWorld();
        BukkitRunnable shoot = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 7) {
                    this.cancel();
                    return;
                }
                count += 1;
                Location shootLoc = p.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                w.playSound(shootLoc, Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                Arrow arrow = w.spawnArrow(shootLoc, shootVec, 3, 5);
                arrow.setTicksLived(1200);
                arrow.setPierceLevel(10);
                arrow.setDamage(5);
                arrow.setShooter(p);
                arrow.setBounce(false);
            }
        };
        shoot.runTaskTimer(plugin,0L,4L);
    }
    public void machineShotBow(Player p){
        World w = p.getWorld();
        BukkitRunnable shoot = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= 3) {
                    this.cancel();
                    return;
                }
                count += 1;
                Location shootLoc = p.getEyeLocation().clone();
                Vector shootVec = shootLoc.getDirection().normalize().clone();
                w.playSound(shootLoc, Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                for(int i = 0;i < 10;i ++){
                    Arrow arrow = w.spawnArrow(shootLoc, shootVec, 4, 15);
                    arrow.setTicksLived(1200);
                    arrow.setDamage(4);
                    arrow.setShooter(p);
                    arrow.setBounce(false);
                }
            }
        };
        shoot.runTaskTimer(plugin,0L,8L);
    }
    public void guitarAxe(Player p){
        World w = p.getWorld();
        BukkitRunnable music = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count > 8){
                    this.cancel();
                    return;
                }
                if(count < 2 || count == 3 || count == 5){
                    w.playSound(p.getEyeLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1,0.7f);
                }else if(count > 5 && count != 7){
                    w.playSound(p.getEyeLocation(),Sound.BLOCK_NOTE_BLOCK_PLING,1,0.93f);
                }
                count += 1;
            }
        };
        music.runTaskTimer(plugin,0L,3L);
    }
    public void reflexAxe(Player p){
        World w = p.getWorld();
        Location fieldLoc = p.getEyeLocation();
        Vector shootVec = fieldLoc.getDirection();
        Particle.DustOptions dust = new Particle.DustOptions(Color.LIME, 1f);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
        w.spawnParticle(Particle.REDSTONE, fieldLoc.add(shootVec), 50, 1, 1, 1, dust);
        List<Entity>nearby = p.getNearbyEntities(10,10,10);
        int count = 0;
        for (Entity e : nearby) {
            if (e instanceof Projectile) {
                if(((Projectile) e).getShooter() != p){
                    count += 1;
                    e.remove();
                    w.playSound(e.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE,e.getLocation(),1);
                }
            }
        }
        int recorded = recordedProjectile.getOrDefault(p.getName(),0);
        recorded += count;
        recordedProjectile.put(p.getName(),recorded);
        String message =ChatColor.GREEN + "一共吸收了" + recorded + "个远程攻击";
        if(recorded != 0 && count != 0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
        }
    }
    public void opKiwami(Player p){
        World w = p.getWorld();
        Location fieldLoc = p.getEyeLocation();
        Vector shootVec = fieldLoc.getDirection();
        Particle.DustOptions dust = new Particle.DustOptions(Color.LIME, 1f);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
        w.spawnParticle(Particle.REDSTONE, fieldLoc.add(shootVec), 50, 1, 1, 1, dust);
        List<Entity>nearby = p.getNearbyEntities(10,10,10);
        int count = 0;
        for (Entity e : nearby) {
            if (e instanceof Projectile) {
                if(((Projectile) e).getShooter() != p){
                    count += 1;
                    e.remove();
                    w.playSound(e.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,1,1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE,e.getLocation(),1);
                }
            }
        }
        int amount = count;
        BukkitRunnable shoot = new BukkitRunnable() {
            @Override
            public void run() {
                if(amount == 0)return;
                w.playSound(p.getEyeLocation(),Sound.BLOCK_ANVIL_PLACE,1,2);
                ability.shootMagic(p, amount + 5,0,3,6,0.5,Color.LIME);
            }
        };
        shoot.runTaskLater(plugin,10L);
        int recorded = recordedProjectile.getOrDefault(p.getName(),0);
        recorded += count;
        recordedProjectile.put(p.getName(),recorded);
        String message =ChatColor.GREEN + "一共吸收了" + recorded + "个远程攻击";
        if(recorded != 0 && count != 0) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
        }
    }
    public void deathStaff(Player p){
        World w = p.getWorld();
        List<Entity>nearby = p.getNearbyEntities(20,20,20);
        ArrayList<Location>mobsLocation = new ArrayList<>();
        for (Entity e : nearby){
            mobsLocation.add(e.getLocation());
            if(!(e instanceof Player)){
                if(e instanceof LivingEntity){
                    ((LivingEntity) e).setHealth(0);
                    e.remove();
                }
                e.remove();
            }
        }
        BukkitRunnable kill = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= mobsLocation.size()){
                    this.cancel();
                    return;
                }
                Location mobLoc = mobsLocation.get(count).add(0,1,0);
                w.spawnParticle(Particle.EXPLOSION_LARGE,mobLoc,0);
                w.spawnParticle(Particle.FLAME,mobLoc, 10, 0, 0, 0, 0.04);
                w.playSound(mobLoc,Sound.ITEM_FIRECHARGE_USE,1,1);
                count += 1;
            }
        };
        kill.runTaskTimer(plugin,0L,1L);
    }
    public void randomAbility(Player p){
        String message = "";
        switch (random.nextInt(8) ) {
            case 0:
                ability.playerBall(p);
                message = "你使用了: " + ChatColor.GREEN + "史莱姆粘液球";
                break;
            case 1:
                ability.playerArrow(p);
                message = "你使用了: 箭雨";
                break;
            case 2:
                ability.playerDash(p);
                message = "你使用了: " + ChatColor.DARK_GREEN + "牛头人冲撞";
                break;
            case 3:
                ability.playerDragonShout(p);
                message = "你使用了: " + ChatColor.RED + "龙息";
                break;
            case 4:
                ability.playerAnvil(p);
                message = "你使用了: " + ChatColor.GRAY + "投掷铁砧";
                break;
            case 5:
                ability.playerMagic(p);
                message = "你使用了: " + ChatColor.LIGHT_PURPLE + "巫妖王魔法";
                break;
            case 6:
                ability.playerBoost(p);
                message = "你使用了: " + ChatColor.DARK_PURPLE + "黑檀木恶魔的怨念";
                break;
            case 7:
                ability.playerEarthShake(p);
                message = "你使用了: " + ChatColor.RED + "类" + ChatColor.GOLD + "星体" + ChatColor.WHITE + "撼地";
                break;
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
    }
    public void parry(Player p){
        World w = p.getWorld();
        if(weaponCoolDown(p,ChatColor.WHITE + "格挡")){
            weaponWarmUP(p,ChatColor.WHITE + "格挡",20L,BarColor.WHITE);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        }
    }
    public void tamaParry(Player p){
        World w = p.getWorld();
        if(weaponCoolDown(p,ChatColor.YELLOW + "格挡")){
            weaponWarmUP(p,ChatColor.YELLOW+ "格挡",15L,BarColor.WHITE);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        }
    }
    public void sharpParry(Player p){
        World w = p.getWorld();
        if(weaponCoolDown(p,ChatColor.AQUA + "格挡")){
            weaponWarmUP(p,ChatColor.AQUA + "格挡",20L,BarColor.WHITE);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        }
    }
    public void shieldParry(Player p){
        World w = p.getWorld();
        if(weaponCoolDown(p,ChatColor.GRAY + "格挡")){
            weaponWarmUP(p,ChatColor.GRAY + "格挡",30L,BarColor.WHITE);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
            w.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,1,1);
        }
    }
    public void areaSlash(Player p,double area,double damage,Color color){
        World w = p.getWorld();
        Location shootLoc = p.getEyeLocation().add(0, -1, 0);
        double angle1 = random.nextDouble() - random.nextDouble();
        double angle2 = random.nextDouble() - random.nextDouble();
        ability.universalSlash(shootLoc, (int) area * 2, angle1, angle2, color);
        ability.areaDamage(shootLoc, area, damage);
        w.playSound(shootLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        w.playSound(shootLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        w.playSound(shootLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
    }
    public void combo(Player p,long delay){
        String name = p.getName();
        BukkitRunnable task = playerComboTask.getOrDefault(name,null);
        if(task != null){
            task.cancel();
        }
        int combo = playerCombo.getOrDefault(name,0);
        combo += 1;
        String message =ChatColor.RED + "" + ChatColor.BOLD + combo + " 连击";
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
        if(combo > 0 && combo % 5 == 0){
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,100,0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL,1,1));
            p.playSound(p.getEyeLocation(),Sound.BLOCK_GLASS_BREAK,1,1);
        }
        playerCombo.put(name,combo);
        BukkitRunnable resetCombo = new BukkitRunnable() {
            @Override
            public void run() {
                playerCombo.remove(name);
                String message =ChatColor.RED + "" + ChatColor.BOLD +"连击重置";
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
                p.playSound(p.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
            }
        };
        resetCombo.runTaskLater(plugin,delay);
        playerComboTask.put(name,resetCombo);
    }
    public void chopLimb(Player attacker,LivingEntity chopped,boolean boosted){
        String name = chopped.getCustomName();
        World w = attacker.getWorld();
        if(name != null){
            switch (name){
                case "§e金胡萝卜神的化身":
                case "§d巫妖王":
                case "§6蜂巢僵尸母体":
                case "§bDJ纯一郎":
                case "§c“膜术师”高资":
                case "§b“膜术师”高资 但是分身":
                case "§c类§6星体":
                    return;
            }
        }
        int bound = 10;
        if(boosted){
            bound = 5;
        }
        switch (random.nextInt(bound)){
            case 0:
                chopped.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,86400,0));
                chopped.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                attacker.sendTitle(" ",ChatColor.GRAY + "你砍掉了怪物的手,怪物的攻击力降低了",10,30,10);
                w.playSound(attacker.getLocation(),Sound.ENTITY_WITHER_BREAK_BLOCK,1,1);
                break;
            case 1:
                chopped.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,86400,2));
                attacker.sendTitle(" ",ChatColor.BLUE + "你砍掉了怪物的腿,怪物的移动速度大幅降低了",10,30,10);
                w.playSound(attacker.getLocation(),Sound.ENTITY_WITHER_BREAK_BLOCK,1,1);
                break;
            case 2:
                chopped.getEquipment().setHelmet(new ItemStack(Material.NETHER_WART_BLOCK));
                attacker.sendTitle(" ",ChatColor.RED + "你砍掉了怪物的头,造成了额外伤害",10,30,10);
                w.playSound(attacker.getLocation(),Sound.ENTITY_WITHER_BREAK_BLOCK,1,1);
                chopped.damage(chopped.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2);
                break;
        }
    }
    public void smokeBow(Player p,float force){
        World w = p.getWorld();
        ability.smokeArrow(p,force);
        Item item = w.dropItem(p.getLocation(),weaponPool.smokeSword());
        item.setPickupDelay(0);
        BukkitRunnable change = new BukkitRunnable() {
            @Override
            public void run() {
                Item item = w.dropItem(p.getLocation(),weaponPool.smokeOni());
                item.setPickupDelay(0);
            }
        };
        change.runTaskLater(plugin,200L);
    }
    public void machineGun(Player p){
        if(weaponCoolDown(p,ChatColor.WHITE + "重新装填")) {
            World w = p.getWorld();
            int ammo = bullets.getOrDefault(p.getName(), 0);
            ammo += 1;
            if (ammo >= 5) {
                w.playSound(p.getLocation(),Sound.BLOCK_FIRE_EXTINGUISH,1,1);
                weaponWarmUP(p, ChatColor.WHITE + "重新装填", 80L, BarColor.WHITE);
                bullets.put(p.getName(), 0);
            } else {
                bullets.put(p.getName(), ammo);
            }
            BukkitRunnable shoot = new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    Location eyeLoc = p.getEyeLocation();
                    Vector eyeVec = eyeLoc.getDirection();
                    count += 1;
                    if (count > 5) {
                        this.cancel();
                        return;
                    }
                    w.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1, 1);
                    Arrow arrow = w.spawnArrow(eyeLoc, eyeVec, 3, 2);
                    arrow.setColor(Color.GRAY);
                    arrow.setShooter(p);
                    arrow.setDamage(6);
                    arrow.setPierceLevel(2);
                    arrow.setTicksLived(1200);
                }
            };
            shoot.runTaskTimer(plugin,0L,1L);
        }
    }
}
