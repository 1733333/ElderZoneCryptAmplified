package Pool;

import Listeners.Abilities;
import UniversalMethod.Tools;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.Area;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

public class MobPool {
    private static MobPool instance = new MobPool();

    private MobPool() {
    }

    public static MobPool getInstance() {
        return instance;
    }

    Tools t = Tools.getInstance();
    WeaponPool wp = WeaponPool.getInstance();
    JavaPlugin plugin;
    Random random = new Random();
    Abilities abilities = Abilities.getInstance();

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void spawnMobs(Location location, int amount, double amplifier, Player target) {
        World w = target.getWorld();
        Location targetLoc = target.getLocation();
        int rangeAmount = random.nextInt(Math.max(amount / 2,1));
        for (int i = 0; i < amount - rangeAmount; i++) {
            int xSpread = random.nextInt(14) - random.nextInt(14);
            int zSpread = random.nextInt(14) - random.nextInt(14);
            Location spawnLoc = location.clone().add(xSpread, 0, zSpread);
            double distance = t.distance(spawnLoc,targetLoc);
            while (distance < 7){
                xSpread = random.nextInt(14) - random.nextInt(14);
                zSpread = random.nextInt(14) - random.nextInt(14);
                spawnLoc = location.clone().add(xSpread, 0, zSpread);
                distance = t.distance(spawnLoc,targetLoc);
            }
            LivingEntity entity;
            int type = random.nextInt(6);
            switch (type) {
                case 0:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.ZOMBIE);
                    break;
                case 1:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.VINDICATOR);
                    entity.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                    entity.getEquipment().setHelmet(new ItemStack(Material.END_ROD));
                    if(random.nextInt(50) == 0){
                        ((Vindicator)entity).setJohnny(true);
                        entity.setCustomName("Johnny");
                        entity.setCustomNameVisible(true);
                    }
                    break;
                case 2:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.CREEPER);
                    break;
                case 3:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.ZOMBIFIED_PIGLIN);
                    break;
                case 4:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.HUSK);
                    break;
                case 5:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.WITHER_SKELETON);
                    break;
                default:
                    return;
            }
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double health = maxHealth * amplifier;
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            entity.setHealth(health);
            entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
            if (entity instanceof Mob) {
                ((Mob) entity).setTarget(target);
                if(amplifier > 3.5){
                    entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                }
            }
        }
        if(amount == 0)return;
        for (int i = 0; i < rangeAmount; i++) {
            int xSpread = random.nextInt(14) - random.nextInt(14);
            int zSpread = random.nextInt(14) - random.nextInt(14);
            Location spawnLoc = location.clone().add(xSpread, 0, zSpread);
            double distance = t.distance(spawnLoc,targetLoc);
            while (distance < 7){
                xSpread = random.nextInt(14) - random.nextInt(14);
                zSpread = random.nextInt(14) - random.nextInt(14);
                spawnLoc = location.clone().add(xSpread, 0, zSpread);
                distance = t.distance(spawnLoc,targetLoc);
            }
            LivingEntity entity;
            int type = random.nextInt(3);
            switch (type) {
                case 0:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.SKELETON);
                    break;
                case 1:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc, EntityType.STRAY);
                    break;
                case 2:
                    entity = (LivingEntity) w.spawnEntity(spawnLoc,EntityType.DROWNED);
                    break;
                default:
                    return;
            }
            double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double health = maxHealth * amplifier;
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            entity.setHealth(health);
            entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
            if (entity instanceof Mob) {
                ((Mob) entity).setTarget(target);
                if(amplifier > 3.5){
                    entity.getEquipment().setItemInMainHand(wp.monsterBow());
                }
            }
        }
    }

    public void spawnMiniBossType(Location location, int type, double amplifier, Player target) {
        switch (type) {
            case 0:
                beeZombie(location, amplifier);
                break;
            case 1:
                ghostSkeleton(location, amplifier, target);
                break;
            case 2:
                woodenDevil(location, amplifier, target);
                break;
            case 3:
                rareSeren(location, amplifier, target);
                break;
            case 4:
                arrowZombie(location, amplifier, target);
                break;
            case 5:
                crasher(location, amplifier, target);
                break;
            case 6:
                teleSee(location, amplifier, target);
                break;
            case 7:
                slimeZombie(location, amplifier, target);
                break;
            case 8:
                wormZombie(location, amplifier, target);
                break;
            case 9:
                mimic(location, amplifier, target);
                break;
            case 10:
                thiefZombie(location, amplifier);
                break;
            case 11:
                cowZombie(location, amplifier);
                break;
            case 12:
                bardZombie(location, amplifier);
                break;
        }
    }
    public void spawnBossType(Location location, int type, double amplifier, Player target) {
        World w = target.getWorld();
        w.strikeLightningEffect(location);
        switch (type) {
            case -2:
                goldenCarrotForm(location,amplifier,target);
                break;
            case -1:
                starBody(location, amplifier, target);
                break;
            case 0:
                wizardKing(location, amplifier, target);
                break;
            case 1:
                beeKing(location, amplifier, target);
                break;
            case 2:
                DJIchiRo(location, amplifier, target);
                break;
            case 3:
                magician(location, amplifier, target);
                break;
        }
    }


    public void spawnMiniBoss(Location location, int amount, double amplifier,Player target) {
        Location targetLoc = target.getLocation();
        for(int i = 0;i < amount;i ++) {
            int type = random.nextInt(13);
            int xSpread = random.nextInt(14) - random.nextInt(14);
            int zSpread = random.nextInt(14) - random.nextInt(14);
            Location spawnLoc = location.clone().add(xSpread, 0, zSpread);
            double distance = t.distance(spawnLoc,targetLoc);
            while (distance < 5){
                xSpread = random.nextInt(14) - random.nextInt(14);
                zSpread = random.nextInt(14) - random.nextInt(14);
                spawnLoc = location.clone().add(xSpread, 0, zSpread);
                distance = t.distance(spawnLoc,targetLoc);
            }
            switch (type) {
                case 0:
                    beeZombie(spawnLoc, amplifier);
                    break;
                case 1:
                    ghostSkeleton(spawnLoc, amplifier, target);
                    break;
                case 2:
                    woodenDevil(spawnLoc, amplifier, target);
                    break;
                case 3:
                    rareSeren(spawnLoc, amplifier, target);
                    break;
                case 4:
                    arrowZombie(spawnLoc, amplifier, target);
                    break;
                case 5:
                    crasher(spawnLoc, amplifier, target);
                    break;
                case 6:
                    teleSee(spawnLoc, amplifier, target);
                    break;
                case 7:
                    slimeZombie(spawnLoc, amplifier, target);
                    break;
                case 8:
                    wormZombie(spawnLoc, amplifier, target);
                    break;
                case 9:
                    mimic(spawnLoc, amplifier, target);
                    break;
                case 10:
                    thiefZombie(spawnLoc, amplifier);
                    break;
                case 11:
                    cowZombie(spawnLoc, amplifier);
                    break;
                case 12:
                    bardZombie(spawnLoc, amplifier);
                    break;
            }
        }
    }
    public void spawnBoss(Location location, double amplifier,Player target) {
        World w = target.getWorld();
        int type = random.nextInt(4);
        w.strikeLightningEffect(location);
        switch (type) {
            case 0:
                wizardKing(location, amplifier, target);
                break;
            case 1:
                beeKing(location, amplifier, target);
                break;
            case 2:
                DJIchiRo(location, amplifier, target);
                break;
            case 3:
                magician(location, amplifier, target);
                break;
        }
    }

    public void slimeZombie(Location location, double amplifier,Player target) {
        World w = target.getWorld();
        LivingEntity zombie = (LivingEntity) w.spawnEntity(location, EntityType.ZOMBIE);
        ((Mob)zombie).setTarget(target);
        double maxHealth = zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.setCustomName(ChatColor.GREEN + "史莱姆僵尸");
        zombie.setCustomNameVisible(true);
        zombie.getEquipment().setHelmet(new ItemStack(Material.SLIME_BLOCK));
    }
    public void beeZombie(Location location, double amplifier) {
        World w = location.getWorld();
        LivingEntity zombie = (LivingEntity) w.spawnEntity(location, EntityType.ZOMBIE);
        double maxHealth = 30;
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.setCustomName(ChatColor.YELLOW + "蜂巢僵尸");
        zombie.setCustomNameVisible(true);
        zombie.getEquipment().setHelmet(new ItemStack(Material.BEE_NEST));
    }
    public void ghostSkeleton(Location location, double amplifier,Player target) {
        World w = target.getWorld();
        LivingEntity ghost = (LivingEntity) w.spawnEntity(location, EntityType.OCELOT);
        LivingEntity passenger = (LivingEntity) w.spawnEntity(location, EntityType.SKELETON);
        ((Mob)passenger).setTarget(target);
        ghost.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 86400, 0));
        ghost.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 86400, 5));
        ghost.setSilent(true);
        passenger.setCustomName(ChatColor.WHITE + "幽灵骷髅");
        passenger.setCustomNameVisible(true);
        double maxHealth = passenger.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        passenger.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        passenger.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        passenger.setHealth(health);
        ghost.addPassenger(passenger);
    }
    public void arrowZombie(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity zombie = (LivingEntity) w.spawnEntity(location, EntityType.ZOMBIE);
        ((Mob)zombie).setTarget(target);
        zombie.setCustomName(ChatColor.WHITE + "被箭射成刺猬的僵尸");
        zombie.setCustomNameVisible(true);
        double maxHealth = zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.getEquipment().setHelmet(new ItemStack(Material.ARROW));
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.ARROW));
        zombie.getEquipment().setItemInOffHand(new ItemStack(Material.ARROW));
    }
    public void crasher(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity creeper = (LivingEntity) w.spawnEntity(location, EntityType.CREEPER);
        ((Mob)creeper).setTarget(target);
        creeper.setCustomName(ChatColor.GOLD + "火爆浪子");
        creeper.setCustomNameVisible(true);
        double maxHealth = creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        creeper.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        creeper.setHealth(health);
    }
    public void woodenDevil(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity skeleton = (LivingEntity) w.spawnEntity(location, EntityType.SKELETON);
        ((Mob)skeleton).setTarget(target);
        double maxHealth = 40;
        double health = maxHealth * amplifier;
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        skeleton.setHealth(health);
        skeleton.setCustomName(ChatColor.DARK_PURPLE + "黑檀木恶魔");
        skeleton.setCustomNameVisible(true);
        EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        equipment.setItemInMainHandDropChance(0);
        ItemStack blade = new ItemStack(Material.IRON_SWORD);
        ItemMeta bladeMeta = blade.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 1, AttributeModifier.Operation.ADD_NUMBER);
        bladeMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
        bladeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bladeMeta.setDisplayName(ChatColor.WHITE + "剃刀");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "一把很普通的剃刀，上面沾了血");
        lore.add(ChatColor.GRAY + "可能是刚刚割了某种东西");
        bladeMeta.setLore(lore);
        blade.setItemMeta(bladeMeta);
        equipment.setItemInMainHand(blade);
    }
    public void teleSee(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity skeleton = (LivingEntity) w.spawnEntity(location, EntityType.WITHER_SKELETON);
        ((Mob)skeleton).setTarget(target);
        double maxHealth = skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        skeleton.setHealth(health);
        skeleton.setCustomName(ChatColor.GRAY + "要塞喷火兵");
        skeleton.setCustomNameVisible(true);
        EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(new ItemStack(Material.IRON_HELMET));
        equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
        ItemStack blade = new ItemStack(Material.STICK);
        ItemMeta bladeMeta = blade.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 1, AttributeModifier.Operation.ADD_NUMBER);
        bladeMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
        bladeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bladeMeta.setDisplayName(ChatColor.WHITE + "WEX");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "传统的远程武器");
        lore.add(ChatColor.GRAY + "能够喷出火焰");
        bladeMeta.setLore(lore);
        blade.setItemMeta(bladeMeta);
        equipment.setItemInMainHand(blade);
        shootFire(skeleton);
    }
    public void rareSeren(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity skeleton = (LivingEntity) w.spawnEntity(location, EntityType.SKELETON);
        ((Mob)skeleton).setTarget(target);
        skeleton.setCustomName(ChatColor.AQUA + "“寒剑”塞壬");
        skeleton.setCustomNameVisible(true);
        double maxHealth = skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        skeleton.setHealth(health);
        EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        ItemStack blade = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta bladeMeta = blade.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 1, AttributeModifier.Operation.ADD_NUMBER);
        bladeMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
        bladeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bladeMeta.setDisplayName(ChatColor.AQUA + "寒剑");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "用上古寒铁打造的具有冰冷气息的剑");
        lore.add(ChatColor.GRAY + "只是碰到就会让人严重冻伤");
        bladeMeta.setLore(lore);
        blade.setItemMeta(bladeMeta);
        equipment.setItemInMainHand(blade);
    }
    public void wormZombie(Location location, double amplifier,Player target) {
        World w = target.getWorld();
        Zombie zombie = (Zombie) w.spawnEntity(location, EntityType.ZOMBIE);
        zombie.setTarget(target);
        zombie.setAdult();
        zombie.setCustomName(ChatColor.GRAY + "寄生僵尸");
        zombie.setCustomNameVisible(true);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,3));
        Chicken kun = (Chicken) w.spawnEntity(location,EntityType.CHICKEN);
        kun.setSilent(true);
        kun.setInvisible(true);
        kun.setAdult();
        kun.setCustomName(ChatColor.GRAY + "隐身坤");
        zombie.addPassenger(kun);
        Silverfish worm = (Silverfish) w.spawnEntity(location,EntityType.SILVERFISH);
        kun.addPassenger(worm);
        double maxHealth = worm.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = maxHealth * amplifier;
        worm.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        worm.setHealth(health);
        worm.setCustomName(ChatColor.GRAY + "寄生僵尸本体");
        worm.setCustomNameVisible(true);
    }
    public void mimic(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity skeleton = (LivingEntity) w.spawnEntity(location, EntityType.SKELETON);
        ((Mob)skeleton).setTarget(target);
        double maxHealth = 10;
        double health = maxHealth * amplifier;
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        skeleton.setHealth(health);
        skeleton.setCustomName(ChatColor.BOLD + "模仿者");
        skeleton.setCustomNameVisible(true);
        EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        BukkitRunnable handItem = new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack hand = target.getInventory().getItemInMainHand();
                if(hand.getType() == Material.AIR)return;
                if(hand.getType() == Material.ENCHANTED_BOOK){
                    equipment.setItemInMainHand(wp.monsterBow());
                }else {
                    equipment.setItemInMainHand(new ItemStack(hand.getType()));
                }
                w.playSound(skeleton.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,5,1);
                w.playSound(skeleton.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,5,1);
                w.playSound(skeleton.getLocation(),Sound.ITEM_ARMOR_EQUIP_GENERIC,5,1);
            }
        };
        handItem.runTaskLater(plugin,20L);
    }
    public void thiefZombie(Location location, double amplifier) {
        World w = location.getWorld();
        Zombie zombie = (Zombie) w.spawnEntity(location, EntityType.ZOMBIE);
        double maxHealth = 10;
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.setCustomName(ChatColor.RED + "盗贼僵尸");
        zombie.setCustomNameVisible(true);
        zombie.setBaby();
        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
    }
    public void cowZombie(Location location, double amplifier) {
        World w = location.getWorld();
        Cow cow = (Cow) w.spawnEntity(location,EntityType.COW);
        cow.setBaby();
        cow.setAI(false);
        cow.setSilent(true);
        cow.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,5));
        cow.setCustomName(ChatColor.GREEN + "牛头人");
        cow.setCustomNameVisible(true);
        Zombie zombie = (Zombie) w.spawnEntity(location, EntityType.ZOMBIE);
        zombie.addPassenger(cow);
        double maxHealth = 30;
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.setCustomName(ChatColor.GREEN + "牛头人");
        zombie.setCustomNameVisible(true);
        zombie.setAdult();
        dash(zombie);
    }
    public void bardZombie(Location location, double amplifier) {
        World w = location.getWorld();
        Zombie zombie = (Zombie) w.spawnEntity(location, EntityType.ZOMBIE);
        double maxHealth = 10;
        double health = maxHealth * amplifier;
        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        zombie.setHealth(health);
        zombie.setCustomName(ChatColor.LIGHT_PURPLE + "吟游尸人");
        zombie.setCustomNameVisible(true);
        EntityEquipment equipment = zombie.getEquipment();
        equipment.setItemInMainHand(new ItemStack(Material.MUSIC_DISC_13));
        bardAbility(zombie);
    }

    public void wizardKing(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity entity = (LivingEntity) w.spawnEntity(location, EntityType.SKELETON);
        entity.setCustomName(ChatColor.LIGHT_PURPLE + "巫妖王");
        double maxHealth = 50;
        double health = maxHealth * amplifier;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        entity.setHealth(health);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 86400, 0));
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(new ItemStack(Material.GOLDEN_HELMET));
        equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.WHITE + "巫妖王权杖");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "一般人只能看到一把弓");
        bowMeta.setLore(lore);
        bow.setItemMeta(bowMeta);
        equipment.setItemInMainHand(bow);
        equipment.setItemInMainHandDropChance(0);
        wizardKingAbility(entity,target);
        bossBar(entity, BarColor.PINK,target);
    }
    public void wizardMinion(Location location,Player target) {
        World w = location.getWorld();
        Entity entity = w.spawnEntity(location, EntityType.SKELETON);
        ((Mob)entity).setTarget(target);
        entity.setCustomName(ChatColor.LIGHT_PURPLE + "巫妖王的随从");
        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        equipment.setItemInMainHand(new ItemStack(Material.WOODEN_HOE));
    }
    public void beeKing(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity entity = (LivingEntity) w.spawnEntity(location, EntityType.ZOMBIE);
        entity.setCustomName(ChatColor.GOLD + "蜂巢僵尸母体");
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 86400, 2));
        double maxHealth = 50;
        double health = maxHealth * amplifier;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        entity.setHealth(health);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(new ItemStack(Material.BEE_NEST));
        equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName(ChatColor.YELLOW + "养蜂人");
        swordItemMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "感觉有点眼熟");
        swordItemMeta.setLore(lore);
        sword.setItemMeta(swordItemMeta);
        equipment.setItemInMainHand(sword);
        equipment.setItemInMainHandDropChance(0);
        Bee entity1 = (Bee) w.spawnEntity(location, EntityType.BEE);
        entity1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40D);
        entity1.setHealth(40);
        entity1.addPassenger(entity);
        beeKingAbility(entity, amplifier);
        bossBar(entity, BarColor.YELLOW,target);
    }
    public void DJIchiRo(Location location, double amplifier,Player target) {
        World w = location.getWorld();
        LivingEntity skeleton = (LivingEntity) w.spawnEntity(location, EntityType.STRAY);
        skeleton.setCustomName(ChatColor.AQUA + "DJ纯一郎");
        double maxHealth = 50;
        double health = maxHealth * amplifier;
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        skeleton.setHealth(health);
        skeleton.setCustomNameVisible(true);
        EntityEquipment equipment = skeleton.getEquipment();
        equipment.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        equipment.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        ItemStack blade = new ItemStack(Material.BOW);
        ItemMeta bladeMeta = blade.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "GENERIC_KNOCKBACK_RESISTANCE", 1, AttributeModifier.Operation.ADD_NUMBER);
        bladeMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
        bladeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        bladeMeta.setDisplayName(ChatColor.AQUA + "锐刻MKV");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "具有寒气的远程攻击手段");
        lore.add(ChatColor.GRAY + "能够放出寒冷烟雾弹");
        bladeMeta.setLore(lore);
        blade.setItemMeta(bladeMeta);
        equipment.setItemInMainHand(blade);
        bossBar(skeleton, BarColor.BLUE,target);
        DJAbility(skeleton);
    }
    public void magician(Location location,double amplifier,Player target){
        World w = location.getWorld();
        WitherSkeleton entity = (WitherSkeleton) w.spawnEntity(location, EntityType.WITHER_SKELETON);
        entity.setCustomName(ChatColor.RED + "“膜术师”高资");
        entity.setCustomNameVisible(true);
        double maxHealth = 50;
        double health = maxHealth * amplifier;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.setHealth(health);
        EntityEquipment equipment = entity.getEquipment();
        ItemStack head = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack hand = new ItemStack(Material.BOW);
        LeatherArmorMeta headMeta = (LeatherArmorMeta) head.getItemMeta();
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        ItemMeta handMeta = hand.getItemMeta();
        headMeta.setColor(Color.RED);
        chestMeta.setColor(Color.RED);
        legMeta.setColor(Color.RED);
        bootsMeta.setColor(Color.RED);
        handMeta.setDisplayName(ChatColor.RED + "膜法棒");
        head.setItemMeta(headMeta);
        chest.setItemMeta(chestMeta);
        leg.setItemMeta(legMeta);
        boots.setItemMeta(bootsMeta);
        hand.setItemMeta(handMeta);
        equipment.setHelmet(head);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boots);
        equipment.setItemInMainHand(hand);
        entity.setTarget(target);
        bossBar(entity,BarColor.RED,target);
        magicianAbility(entity,amplifier,target);
    }
    public void magicianMinion(Location location,double amplifier,Player target){
        World w = location.getWorld();
        WitherSkeleton entity = (WitherSkeleton) w.spawnEntity(location, EntityType.WITHER_SKELETON);
        entity.setCustomName(ChatColor.AQUA + "“膜术师”高资 但是分身");
        entity.setCustomNameVisible(true);
        double maxHealth = 30;
        double health = maxHealth * amplifier;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(amplifier * 1.5);
        entity.setHealth(health);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,5));
        EntityEquipment equipment = entity.getEquipment();
        ItemStack head = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack hand = new ItemStack(Material.STICK);
        LeatherArmorMeta headMeta = (LeatherArmorMeta) head.getItemMeta();
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        ItemMeta handMeta = hand.getItemMeta();
        headMeta.setColor(Color.AQUA);
        chestMeta.setColor(Color.AQUA);
        legMeta.setColor(Color.AQUA);
        bootsMeta.setColor(Color.AQUA);
        handMeta.setDisplayName(ChatColor.AQUA + "挥石法杖");
        head.setItemMeta(headMeta);
        chest.setItemMeta(chestMeta);
        leg.setItemMeta(legMeta);
        boots.setItemMeta(bootsMeta);
        hand.setItemMeta(handMeta);
        equipment.setHelmet(head);
        equipment.setChestplate(chest);
        equipment.setLeggings(leg);
        equipment.setBoots(boots);
        equipment.setItemInMainHand(hand);
        entity.setTarget(target);
    }
    public void starBody(Location location,double amplifier,Player target) {
        World w = location.getWorld();
        MagmaCube cube = (MagmaCube) w.spawnEntity(location.clone().add(0,2,0), EntityType.MAGMA_CUBE);
        cube.setCustomName(ChatColor.RED + "类" + ChatColor.GOLD + "星体");
        cube.setCustomNameVisible(true);
        cube.setSize(10);
        cube.setGlowing(true);
        double maxHealth = 100;
        double health = maxHealth * amplifier;
        cube.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        cube.setHealth(health);
        cube.setAI(false);
        cube.setGravity(false);
        cube.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,86400,5));
        starBodyAbility(cube,target);
        bossBar(cube,BarColor.RED,target);
    }
    public void goldenCarrotForm(Location location,double amplifier,Player target){
        World w = location.getWorld();
        Skeleton entity = (Skeleton) w.spawnEntity(location, EntityType.SKELETON);
        entity.setCustomName(ChatColor.YELLOW + "金胡萝卜神的化身");
        entity.setCustomNameVisible(true);
        double maxHealth = 1000;
        double health = maxHealth * amplifier;
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        entity.setHealth(maxHealth);
        entity.setSilent(true);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,86400,0));
        EntityEquipment equipment = entity.getEquipment();
        equipment.setHelmet(new ItemStack(Material.GOLDEN_CARROT));
        equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        equipment.setItemInMainHand(wp.bossGoldenZweiHander());
        entity.setTarget(target);
        bossBar(entity,BarColor.YELLOW,target);
        carrotAbility(entity,target);
    }

    public void shootFire(LivingEntity entity) {
        World w = entity.getWorld();
        BukkitRunnable fire = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (entity.isDead()) {
                    this.cancel();
                    return;
                }
                Location eyeLoc = entity.getEyeLocation().clone();
                Vector eyeVec = eyeLoc.getDirection().clone();
                time += 1;
                if (time < 10) {
                    w.spawnParticle(Particle.SMOKE_LARGE, eyeLoc.add(eyeVec.multiply(0.5)), 5, 0, 0, 0, 0.03);
                } else if (time > 10 && time < 13) {
                    w.playSound(eyeLoc, Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                    w.spawnParticle(Particle.FLAME, eyeLoc.add(eyeVec.multiply(0.5)), 10, 0, 0, 0, 0.04);
                    if(time == 12){
                        entity.setAI(false);
                    }
                } else if (time >= 13) {
                    if (entity.isDead()) this.cancel();
                    Location shootLoc = entity.getEyeLocation();
                    Vector shootVec = shootLoc.getDirection();
                    w.playSound(shootLoc, Sound.ITEM_FIRECHARGE_USE, 2, 1);
                    for (int i = 0; i < 5; i++) {
                        Arrow fire = w.spawnArrow(shootLoc, shootVec, 1, 30);
                        fire.setFireTicks(1200);
                        fire.setTicksLived(1200);
                        fire.setShooter(entity);
                        fire.setDamage(6);
                    }
                    entity.setAI(true);
                    w.spawnParticle(Particle.EXPLOSION_LARGE, eyeLoc.add(eyeVec), 1);
                    time = 0;
                }
            }
        };
        fire.runTaskTimer(plugin, 10L, 10L);
    }
    public void dash(LivingEntity zombie){
        World w = zombie.getWorld();
        BukkitRunnable count = new BukkitRunnable() {
            int num = 0;
            @Override
            public void run() {
                if(num > 10){
                    num = 0;
                }
                if(zombie.isDead()){
                    this.cancel();
                    return;
                }
                if(num > 6 && num < 9){
                    zombie.setGlowing(true);
                    zombie.setAI(false);
                    w.playSound(zombie.getLocation(),Sound.BLOCK_GRASS_BREAK,2,1);
                    if(num == 7) {
                        Location dashLoc = zombie.getEyeLocation();
                        Vector dashVec = zombie.getEyeLocation().getDirection().clone();
                        Vector finalVec = new Vector(dashVec.getX(), 0, dashVec.getZ());
                        for (int i = 0; i < 8; i++) {
                            AreaEffectCloud cloud = (AreaEffectCloud) w.spawnEntity(dashLoc, EntityType.AREA_EFFECT_CLOUD);
                            cloud.setDuration(15);
                            cloud.setRadius(2);
                            cloud.setColor(Color.LIME);
                            dashLoc.add(finalVec.clone().multiply(2));
                        }
                    }
                }
                if(num == 9){
                    w.playSound(zombie.getLocation(),Sound.ENTITY_COW_HURT,2,1);
                }
                if(num == 10){
                    zombie.setGlowing(false);
                    Vector dashVec = zombie.getEyeLocation().getDirection().clone();
                    Vector finalVec = new Vector(dashVec.getX(),0,dashVec.getZ());
                    zombie.setAI(true);
                    BukkitRunnable dash = new BukkitRunnable() {
                        int count = 0;
                        @Override
                        public void run() {
                            if(zombie.isDead()){
                                this.cancel();
                                return;
                            }
                            if(count >= 3){
                                this.cancel();
                                return;
                            }
                            count += 1;
                            zombie.setVelocity(finalVec.multiply(1.5));
                            List<Entity>nearby = zombie.getNearbyEntities(2,2,2);
                            for(Entity e : nearby){
                                if(e instanceof Player){
                                    ((Player) e).damage(10);
                                    if(((Player) e).getHealth() <= 0){
                                        Bukkit.broadcastMessage(ChatColor.RED + e.getName() + "被牛头人撞死了");
                                    }
                                    zombie.attack(e);
                                    zombie.setVelocity(new Vector(0,0,0));
                                    this.cancel();
                                }else if(e instanceof LivingEntity){
                                    abilities.suck(zombie,zombie.getLocation(),2,-2);
                                }
                            }
                        }
                    };
                    dash.runTaskTimer(plugin,0L,5L);
                }
                num += 1;
            }
        };
        count.runTaskTimer(plugin,0L,10L);
    }
    public void bardAbility(Zombie zombie) {
        World w = zombie.getWorld();
        BukkitRunnable music2 = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count > 4) {
                    this.cancel();
                    return;
                }
                w.playSound(zombie.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 0.6f + count * 0.2f);
                count += 1;
            }
        };
        music2.runTaskTimer(plugin, 0L, 3L);
        BukkitRunnable sing = new BukkitRunnable() {
            @Override
            public void run() {
                if (zombie.isDead()) {
                    this.cancel();
                    return;
                }
                BukkitRunnable music = new BukkitRunnable() {
                    int count = 0;

                    @Override
                    public void run() {
                        if (count > 8) {
                            this.cancel();
                            return;
                        }
                        if (count < 2 || count == 3 || count == 5) {
                            w.playSound(zombie.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 0.7f);
                        } else if (count > 5 && count != 7) {
                            w.playSound(zombie.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 0.92f);
                        }
                        count += 1;
                    }
                };
                music.runTaskTimer(plugin, 0L, 3L);
                List<Entity> nearby = zombie.getNearbyEntities(20, 20, 20);
                for (Entity e : nearby) {
                    if (e instanceof Mob) {
                        ((Mob) e).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0, false));
                        ((Mob) e).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, false));
                    }
                }
            }
        };
        sing.runTaskTimer(plugin, 100L, 200L);
    }

    public void wizardKingAbility(LivingEntity entity,Player target) {
        World w = entity.getWorld();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        BukkitRunnable aura = new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead()) this.cancel();
                double health = entity.getHealth();
                if (health >= maxHealth / 2) {
                    abilities.suck(entity, entity.getLocation(), 10, -2);
                } else {
                    abilities.suck(entity, entity.getLocation(), 10, 1);
                }
            }
        };
        BukkitRunnable phaseTwo = new BukkitRunnable() {
            @Override
            public void run() {
                
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.strikeLightningEffect(entity.getLocation());
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 86400, 1));
                entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100D);
                ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
                ItemMeta swordItemMeta = sword.getItemMeta();
                swordItemMeta.setDisplayName(ChatColor.WHITE + "巫妖王权杖");
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "一般人只能看到一把剑");
                swordItemMeta.setLore(lore);
                sword.setItemMeta(swordItemMeta);
                entity.getEquipment().setItemInMainHand(sword);
            }
        };
        BukkitRunnable summon = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.playSound(entity.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 5, 1);
                wizardMinion(entity.getLocation(),target);
                wizardMinion(entity.getLocation(),target);
                wizardMinion(entity.getLocation(),target);
            }
        };
        BukkitRunnable shoot = new BukkitRunnable() {
            @Override
            public void run() {
                
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.playSound(entity.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
                Location shootLoc = entity.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                for (int i = 0; i < 5; i++) {
                    Arrow arrow = w.spawnArrow(shootLoc, shootVec, 1.5f, 12);
                    arrow.setDamage(4);
                    arrow.setTicksLived(1100);
                    arrow.setShooter(entity);
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow.setColor(Color.PURPLE);
                    arrow.setCustomName(ChatColor.LIGHT_PURPLE + "巫妖王的法术");
                }
            }
        };
        BukkitRunnable phase = new BukkitRunnable() {
            @Override
            public void run() {
                double health = entity.getHealth();
                if (entity.isDead()){
                    this.cancel();
                    return;
                }
                if (health <= maxHealth / 2) {
                    phaseTwo.run();
                    shoot.cancel();
                    summon.cancel();
                    this.cancel();
                }
            }
        };
        shoot.runTaskTimer(plugin, 20L, 100L);
        aura.runTaskTimer(plugin, 0L, 100L);
        phase.runTaskTimer(plugin, 0L, 20L);
        summon.runTaskTimer(plugin, 0L, 400L);
    }
    public void beeKingAbility(LivingEntity entity, double amplifier) {
        World w = entity.getWorld();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        BukkitRunnable summon = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.playSound(entity.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 5, 1);
                beeZombie(entity.getLocation(), amplifier);
                beeZombie(entity.getLocation(), amplifier);
            }
        };
        BukkitRunnable shoot = new BukkitRunnable() {
            @Override
            public void run() {
                
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.playSound(entity.getLocation(), Sound.BLOCK_HONEY_BLOCK_BREAK, 1, 1);
                Location shootLoc = entity.getEyeLocation();
                Vector shootVec = shootLoc.getDirection();
                abilities.shootSlowBall(entity, new ItemStack(Material.HONEY_BLOCK), shootLoc, shootVec, 6);
            }
        };
        BukkitRunnable phaseTwo = new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.getVehicle() != null) {
                    Entity ride = entity.getVehicle();
                    ride.remove();
                }
                if(entity.isDead()) {
                    this.cancel();
                    return;
                }
                w.strikeLightningEffect(entity.getLocation());
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 86400, 1));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 86400, 1));
                entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100D);
            }
        };
        BukkitRunnable phase = new BukkitRunnable() {
            @Override
            public void run() {
                double health = entity.getHealth();
                if (entity.isDead()) {
                    this.cancel();
                    return;
                }
                if (health <= maxHealth / 2) {
                    phaseTwo.run();
                    shoot.cancel();
                    summon.cancel();
                    this.cancel();
                }
            }
        };
        shoot.runTaskTimer(plugin, 20L, 50L);
        phase.runTaskTimer(plugin, 0L, 20L);
        summon.runTaskTimer(plugin, 0L, 500L);
    }
    public void DJAbility(LivingEntity entity) {
        abilities.coldSmoke(entity,entity.getLocation(),100L,0);
        World w = entity.getWorld();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        BukkitRunnable phaseTwo = new BukkitRunnable() {
            @Override
            public void run() {
                w.strikeLightningEffect(entity.getLocation());
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 86400, 1));
                entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(100D);
                LivingEntity snowLeopard = (LivingEntity) w.spawnEntity(entity.getLocation(), EntityType.OCELOT);
                snowLeopard.addPassenger(entity);
                snowLeopard.setCustomName(ChatColor.YELLOW + "芝士雪豹");
                snowLeopard.setCustomNameVisible(true);
                snowLeopard.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 86400, 0));
            }
        };
        BukkitRunnable shoot = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (entity.isDead()) {
                    this.cancel();
                    List<Entity> entities = entity.getNearbyEntities(10, 10, 10);
                    for (Entity e : entities) {
                        if (e instanceof Animals) {
                            if (e.getCustomName() != null) {
                                ((Animals) e).setHealth(0);
                                Bukkit.broadcastMessage( e.getCustomName() + ChatColor.RED + "闭嘴了");
                            }
                        }
                    }
                }
                if(count > 8) {
                    count = 0;
                    abilities.coldSmoke(entity, entity.getLocation(), 100L, 0);
                }
                count += 1;
            }
        };
        BukkitRunnable phase = new BukkitRunnable() {
            @Override
            public void run() {
                double health = entity.getHealth();
                if (entity.isDead()) {
                    this.cancel();
                }
                if (health <= maxHealth / 2) {
                    phaseTwo.run();
                    this.cancel();
                }
            }
        };
        shoot.runTaskTimer(plugin, 20L, 20L);
        phase.runTaskTimer(plugin, 0L, 20L);
    }
    public void magicianAbility(LivingEntity entity,double amplifier,Player target){
        World w = entity.getWorld();
        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        BukkitRunnable summon = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                w.playSound(entity.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 5, 1);
                spawnMiniBoss(entity.getLocation(),2,amplifier,target);
            }
        };
        BukkitRunnable switchPlace = new BukkitRunnable() {
            double prvHealth = entity.getHealth();
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                double currentHealth = entity.getHealth();
                if(currentHealth < prvHealth){
                    Location entityLoc = entity.getLocation();
                    List<Entity>nearby = entity.getNearbyEntities(10,10,10);
                    for(Entity e : nearby){
                        if(e.getName().equals(ChatColor.AQUA + "“膜术师”高资 但是分身")){
                            Location cloneLoc = e.getLocation();
                            entity.teleport(cloneLoc);
                            e.teleport(entityLoc);
                            prvHealth = currentHealth;
                            break;
                        }
                    }
                }
            }
        };
        BukkitRunnable phase = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                if(entity.getHealth() < maxHealth / 2){
                    summon.cancel();
                    w.strikeLightningEffect(entity.getLocation());
                    magicianMinion(entity.getLocation(),amplifier,target);
                    magicianMinion(entity.getLocation(),amplifier,target);
                    switchPlace.runTaskTimer(plugin,0L,20L);
                    this.cancel();
                }
            }
        };
        summon.runTaskTimer(plugin,50L,400L);
        phase.runTaskTimer(plugin,0L,20L);
    }
    public void starBodyAbility(LivingEntity entity,Player target){
        World w = entity.getWorld();
        Vector floating = new Vector(0,-2,0);
        BukkitRunnable earthShake = new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                if(((Entity)target).isOnGround()){
                    target.damage(8);
                    target.sendTitle(ChatColor.RED + "Oops！","Boss的撼地攻击需要跳起来躲避",10,30,10);
                }
                w.spawnParticle(Particle.EXPLOSION_LARGE,entity.getLocation().add(floating),50,10,0,10);
                w.playSound(entity.getEyeLocation(),Sound.ENTITY_GENERIC_EXPLODE,5,1);
                abilities.suck(entity,entity.getLocation().add(floating),20,-2);
            }
        };
        BukkitRunnable count = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                if(!entity.isGlowing()){
                    count = 0;
                }
                if(count >= 8 && count < 10){
                    w.playSound(entity.getEyeLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.playSound(entity.getEyeLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.playSound(entity.getEyeLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.playSound(entity.getEyeLocation(),Sound.BLOCK_STONE_BREAK,5,1);
                    w.spawnParticle(Particle.EXPLOSION_LARGE,entity.getEyeLocation(),10,3,3,3);
                }else if(count == 10){
                    earthShake.run();
                    count = 0;
                }
                count += 1;
            }
        };
        count.runTaskTimer(plugin,0L,15L);
    }
    public void carrotAbility(LivingEntity entity,Player target){
        World w = entity.getWorld();
        BukkitRunnable ability = new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(entity.isDead()){
                    this.cancel();
                    return;
                }
                if(count >= 10){
                    w.dropItem(entity.getLocation(),wp.bread());
                    randomAbility(entity,target);
                    count = 0;
                }
                count += 1;
            }
        };
        ability.runTaskTimer(plugin,0L,10L);
    }
    public void randomAbility(LivingEntity entity,Player target) {
        World w = entity.getWorld();
        w.playSound(entity.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1);
        entity.setGlowing(true);
        entity.setAI(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
        BukkitRunnable cast = new BukkitRunnable() {
            @Override
            public void run() {
                entity.setGlowing(false);
                entity.setAI(true);
                switch (random.nextInt(13)) {
                    case 0:
                        abilities.carrotBall(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.GREEN + "史莱姆粘液球", 10, 30, 10);
                        break;
                    case 1:
                        abilities.carrotArrow(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: 箭雨", 10, 30, 10);
                        break;
                    case 2:
                        abilities.carrotBee(entity, target);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.YELLOW + "蜂群", 10, 30, 10);
                        break;
                    case 3:
                        abilities.carrotAnvil(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.GRAY + "投掷铁砧", 10, 30, 10);
                        break;
                    case 4:
                        abilities.carrotChicken(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.GOLD + "数一数二的烧鸡", 10, 30, 10);
                        break;
                    case 5:
                        abilities.carrotDisarm(entity, target);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.BOLD + "缴械", 10, 30, 10);
                        break;
                    case 6:
                        abilities.carrotDash(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.DARK_GREEN + "牛头人冲撞", 10, 30, 10);
                        break;
                    case 7:
                        abilities.coldSmoke(entity, entity.getLocation(), 100L, 1);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.AQUA + "清凉电子烟", 10, 30, 10);
                        break;
                    case 8:
                        spawnMiniBossType(entity.getLocation(), random.nextInt(13), 5, target);
                        spawnMiniBossType(entity.getLocation(), random.nextInt(13), 5, target);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.RED + "膜术师召唤", 10, 30, 10);
                        break;
                    case 9:
                        abilities.carrotMagic(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.LIGHT_PURPLE + "巫妖王法术", 10, 30, 10);
                        break;
                    case 10:
                        abilities.carrotEarthShake(entity, target);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.RED + "类" + ChatColor.GOLD + "星体" + ChatColor.WHITE + "撼地", 10, 30, 10);
                        break;
                    case 11:
                        abilities.carrotDragonShout(entity);
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: " + ChatColor.RED + "龙息", 10, 30, 10);
                        break;
                    case 12:
                        target.sendTitle(" ", ChatColor.WHITE + "Boss使用了: 寂寞", 10, 30, 10);
                        break;
                }
            }
        };
        cast.runTaskLater(plugin, 10L);
    }

    public void bossBar(Entity boss, BarColor color,Player target) {
        BossBar bar = Bukkit.createBossBar(boss.getName(), color, BarStyle.SEGMENTED_10);
        bar.addPlayer(target);
        BukkitRunnable progress = new BukkitRunnable() {
            @Override
            public void run() {
                if (boss.isDead()) {
                    this.cancel();
                    bar.removeAll();
                }
                double health = ((LivingEntity) boss).getHealth();
                double maxHealth = ((LivingEntity) boss).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if (health / maxHealth > 1) {
                    bar.setProgress(1);
                } else {
                    bar.setProgress(health / maxHealth);
                }
            }
        };
        progress.runTaskTimer(plugin, 0L, 2L);
    }
}
